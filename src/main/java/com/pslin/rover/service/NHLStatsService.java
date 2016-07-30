package com.pslin.rover.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pslin.rover.NHLTeamStats;
import com.pslin.rover.data.HockeyRepository;
import com.pslin.rover.entity.NHLTeam;
import com.pslin.rover.serializer.DateTimeTypeConverter;
import com.pslin.rover.util.FTPUtil;
import com.pslin.rover.util.NamesUtil;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves season stats for a team.
 *
 * @author plin
 */
public class NHLStatsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NHLStatsService.class);

    private static final String NHL_CONF_STANDINGS_PAGE = "http://www.nhl.com/ice/standings.htm?season=20152016&type=CON";

    private static final String NHL_DIV_STANDINGS_PAGE = "http://www.nhl.com/ice/standings.htm?season=20152016&type=DIV";

    private HockeyRepository hockeyRepository;

    private NamesUtil namesUtil;

    private Document nhlPage;

    public boolean processAllStats() {
        List<File> statsFilesList = new ArrayList<>();
        for(Object key : namesUtil.getNhlNamesProp().keySet()) {
            String teamFullName = (String) key;
            NHLTeamStats NHLTeamStats = getSeasonStats(namesUtil.getNHLTeamName(teamFullName));
            statsFilesList.add(saveStatsToFile(NHLTeamStats, namesUtil.toNiceName(namesUtil.getNHLTeamName(teamFullName))));
        }

        return FTPUtil.uploadFiles(statsFilesList, "nhl/resources/stats");
    }

    public NHLStatsService() {
        namesUtil = new NamesUtil();
        hockeyRepository = new HockeyRepository();
        getNHLPage();
    }

    private void getNHLPage() {
        try {
            nhlPage = Jsoup.connect(NHL_CONF_STANDINGS_PAGE).get();
        } catch (IOException e) {
            LOGGER.error("There was a problem connecting to the NHL conf standings page.", e);
        }
    }

    /**
     * Parses the table for various season stats, such as streak, standing, games played, etc.
     *
     * @param teamName
     * @return
     */
    public NHLTeamStats getSeasonStats(String teamName) {
        // Retrieve team
        NHLTeam team = hockeyRepository.getTeam(teamName);
        if(team == null) {
            LOGGER.error(teamName + " does not exist in the database.");
            return null;
        }

        // Get conference of team
        String conferenceTableSelector;
        if(team.getConference().equals(NHLTeam.Conference.EASTERN)) {
            conferenceTableSelector = "#wideCol > div.contentBlock > table:nth-child(5)";
        } else {
            conferenceTableSelector = "#wideCol > div.contentBlock > table:nth-child(8)";
        }

        // These team names appear differently on the HTML page so we need to rename them
        String cityNameInTable;
        switch(teamName) {
            case "Canadiens":
                cityNameInTable = "Montr\u00E9al";
                break;

            case "Islanders":
                cityNameInTable = "NY Islanders";
                break;

            case "Rangers":
                cityNameInTable = "NY Rangers";
                break;

            default:
                cityNameInTable = team.getCity();
                break;
        }


        NHLTeamStats nhlTeamStats = new NHLTeamStats();
        // Get conference standing and other stats
        if(nhlPage == null) {
            getNHLPage();
        }

        Element standingsTable = nhlPage.select(conferenceTableSelector).first();
        Elements rows = standingsTable.getElementsByTag("tr");
        for(Element row : rows) {
            Elements tds = row.getElementsByTag("td");
            if(tds.size() == 0) {
                continue;
            }
            String retrievedTeamName = tds.get(1).text().replace("\u00a0", "");

            if(retrievedTeamName.equals(cityNameInTable)) {
                nhlTeamStats.setName(team.getName());
                nhlTeamStats.setConference(team.getConference());
                nhlTeamStats.setDivision(team.getDivision());
                nhlTeamStats.setConferenceStanding(Integer.parseInt(tds.get(0).text()));
                nhlTeamStats.setGamesPlayed(Integer.parseInt(tds.get(3).text()));
                nhlTeamStats.setWins(Integer.parseInt(tds.get(4).text()));
                nhlTeamStats.setLosses(Integer.parseInt(tds.get(5).text()));
                nhlTeamStats.setOvertimeLosses(Integer.parseInt(tds.get(6).text()));
                nhlTeamStats.setPoints(Integer.parseInt(tds.get(7).text()));
                nhlTeamStats.setStreak(tds.get(16).text());
                break;
            }
        }

        String divisionTableSelector = "";
        switch (team.getDivision()) {
            case ATLANTIC:
                divisionTableSelector = "#wideCol > div.contentBlock > table:nth-child(5)";
                break;

            case METROPOLITAN:
                divisionTableSelector = "#wideCol > div.contentBlock > table:nth-child(6)";
                break;

            case CENTRAL:
                divisionTableSelector = "#wideCol > div.contentBlock > table:nth-child(9)";
                break;

            case PACIFIC:
                divisionTableSelector = "#wideCol > div.contentBlock > table:nth-child(10)";
                break;
        }

        // Get division standing
        Document divDocument;
        try {
            divDocument = Jsoup.connect(NHL_DIV_STANDINGS_PAGE).get();
        } catch (IOException e) {
            LOGGER.error("There was a problem connecting to the NHL conf standings page.", e);
            return null;
        }
        Element divStandingsTable = divDocument.select(divisionTableSelector).first();
        Elements divRows = divStandingsTable.getElementsByTag("tr");
        for(Element row : divRows) {
            Elements tds = row.getElementsByTag("td");
            if(tds.size() == 0) {
                continue;
            }
            if(tds.get(1).text().replace("\u00a0", "").equals(cityNameInTable)) {
                nhlTeamStats.setDivisionStanding(Integer.parseInt(tds.get(0).text()));
            }
        }

        return nhlTeamStats;
    }

    private File saveStatsToFile(NHLTeamStats stats, String teamName) {
        File file = new File("/" + teamName + "-stats.json");
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
        String json = gson.toJson(stats);

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
