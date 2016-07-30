package com.pslin.rover.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pslin.rover.NBATeamStats;
import com.pslin.rover.NHLTeamStats;
import com.pslin.rover.data.NbaRepository;
import com.pslin.rover.entity.NBATeam;
import com.pslin.rover.serializer.DateTimeTypeConverter;
import com.pslin.rover.util.FTPUtil;
import com.pslin.rover.util.NamesUtil;
import org.apache.commons.lang3.StringUtils;
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
 * @author plin
 */
public class NBAStatsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NBAStatsService.class);

    private static final String NBA_STANDINGS_PAGE = "http://www.nba.com/standings/team_record_comparison/conferenceNew_Std_Cnf.html";

    private NbaRepository nbaRepository;

    private Document standingsPage;

    private NamesUtil namesUtil;

    public static void main(String[] args) {
        NBAStatsService nbaStatsService = new NBAStatsService();

        nbaStatsService.processAllStats();
    }

    public boolean processAllStats() {
        List<File> statsFilesList = new ArrayList<>();
        for(Object key : namesUtil.getNbaNamesProp().keySet()) {
            String teamFullName = (String) key;
            NBATeamStats nbaTeamStats = getSeasonStats(namesUtil.getNBATeamName(teamFullName));
            statsFilesList.add(saveStatsToFile(nbaTeamStats, namesUtil.toNiceName(namesUtil.getNBATeamName(teamFullName))));
        }

        return FTPUtil.uploadFiles(statsFilesList, "nba/resources/stats");
    }

    public NBAStatsService() {
        nbaRepository = new NbaRepository();
        namesUtil = new NamesUtil();
    }

    private void getStatsPage() {
        try {
            standingsPage = Jsoup.connect(NBA_STANDINGS_PAGE).get();
        } catch (IOException e) {
            LOGGER.error("There was a problem connecting to " + NBA_STANDINGS_PAGE);
        }
    }

    public NBATeamStats getSeasonStats(String teamName) {
        NBATeam team = nbaRepository.getTeam(teamName);
        if(team == null) {
            LOGGER.error(teamName + " does not exist in the database.");
            return null;
        }

        String conferenceTableSelector = "#nbaFullContent > table";
//        if(team.getConference().equals(NBATeam.Conference.EASTERN)) {
//            conferenceTableSelector = "#main-container > div > section > div.tab-content > div > div:nth-child(2) > table";
//        } else {
//            conferenceTableSelector = "#main-container > div > section > div.tab-content > div > div:nth-child(3) > table";
//        }

        String cityNameInTable;
        switch (team.getName()) {
            case "Lakers":
                cityNameInTable = "L.A. Lakers";
                break;

            case "Clippers":
                cityNameInTable = "L.A. Clippers";
                break;

            default:
                cityNameInTable = team.getCity();
                break;
        }


        if(standingsPage == null) {
            getStatsPage();
        }

        NBATeamStats nbaTeamStats = new NBATeamStats();

        Element standingsTable = standingsPage.select(conferenceTableSelector).first();
        Elements rows = standingsTable.getElementsByTag("tr");
        for(Element row : rows) {
            Elements tds = row.getElementsByTag("td");
            if(tds.size() == 1 || tds.get(0).text().equals("Eastern") || tds.get(0).text().equals("Western")) {
                continue;
            }

            String retrievedTeamName = tds.get(0).getElementsByTag("a").first().text();

            if(retrievedTeamName.equals(cityNameInTable)) {
                nbaTeamStats.setName(team.getName());
                nbaTeamStats.setConference(team.getConference());
                nbaTeamStats.setDivision(team.getDivision());

                // conferecne standing
                String conferenceStanding = tds.get(0).getElementsByTag("sup").first().text();
                int confStanding;
                if(!StringUtils.isBlank(conferenceStanding)) {
                    confStanding = Integer.parseInt(conferenceStanding);
                    nbaTeamStats.setConferenceStanding(confStanding);
                }

                // wins and losses
                nbaTeamStats.setWins(Integer.parseInt(tds.get(1).text()));
                nbaTeamStats.setLosses(Integer.parseInt(tds.get(2).text()));

                // streak
                String streak = tds.get(10).text();
                String streakText;
                String[] streakTokens = streak.split(" ");
                if(streakTokens[0].equals("W")) {
                    streakText = "Won " + streakTokens[1];
                } else {
                    streakText = "Lost " + streakTokens[1];
                }
                nbaTeamStats.setStreak(streakText);
                break;
            }
        }

        return nbaTeamStats;
    }

    private File saveStatsToFile(NBATeamStats stats, String teamName) {
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
