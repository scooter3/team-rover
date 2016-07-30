package com.pslin.rover.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pslin.rover.SportLeague;
import com.pslin.rover.dto.Event;
import com.pslin.rover.dto.SimpleGame;
import com.pslin.rover.serializer.DateTimeTypeConverter;
import com.pslin.rover.util.NamesUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author plin
 */
public class NBAScheduleService implements ScheduleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NBAScheduleService.class);

    private static final String BASKETBALL_REFERENCE_SCHEDULE_URL = "http://www.basketball-reference.com/teams/%s/2016_games.html";

    private SeatGeekService seatGeekService;

    private NamesUtil namesUtil;

    public static void main(String[] args) {
        NBAScheduleService nbaScheduleService = new NBAScheduleService();
        for(Object key : nbaScheduleService.namesUtil.getNbaAbbreviationProp().keySet()) {
            String teamName = (String) key;
            System.out.println(teamName);
            List<SimpleGame> games = nbaScheduleService.getSchedule(teamName);
            nbaScheduleService.saveScheduleToFile(games, teamName);
        }
    }

    public NBAScheduleService() {
        seatGeekService = new SeatGeekService();
        namesUtil = new NamesUtil();
    }

    public List<SimpleGame> getSchedule(String teamName) {
        Map<LocalDate, Event> ticketsMap;
        try {
            ticketsMap = seatGeekService.getTicketUrls(teamName, SportLeague.NBA);
        } catch (IOException e) {
            LOGGER.error("There was an error getting ticket URLs for " + teamName);
            return Collections.emptyList();
        }

        String teamAbbreviation = namesUtil.getNBATeamAbbreviation(teamName);
        Document document;

        try {
            document = Jsoup.connect(String.format(BASKETBALL_REFERENCE_SCHEDULE_URL, teamAbbreviation)).get();
        } catch (IOException e) {
            LOGGER.error("There was an error connecting to " + String.format(BASKETBALL_REFERENCE_SCHEDULE_URL, teamAbbreviation));
            return Collections.emptyList();
        }

        if(document == null) {
            LOGGER.error("There was an error retrieving the document for " + teamAbbreviation);
            return Collections.emptyList();
        }

        Element scheduleTable = document.getElementById("teams_games");
        Elements rows = scheduleTable.getElementsByTag("tr");

        DateTimeFormatter completeDateFormat = DateTimeFormat.forPattern("EEE, MMM d, yyyy h:mm'p' zzz a");
        List<SimpleGame> games = new ArrayList<>();
        int index = 0;
        for(Element row : rows) {
            Elements tds = row.getElementsByTag("td");
            if(tds.size() > 0 && !tds.get(0).text().equals("G")) {
                SimpleGame game = new SimpleGame();
                game.setId(index++);

                String date = tds.get(1).text();
                String time = tds.get(2).text();
                String halfday = String.valueOf(time.charAt(time.length()-5));
                if(halfday.equals("p")) {
                    halfday = "PM";
                } else {
                    halfday = "AM";
                }
                String completeDate = date + " " + time + " " + halfday;
                game.setDate(completeDateFormat.parseDateTime(completeDate));
                String formattedTime = game.getDate().toString("h:mm a");
//                LOGGER.debug(formattedTime);
                game.setTime(formattedTime);

                Event event = ticketsMap.get(new LocalDate(completeDateFormat.parseDateTime(completeDate)));
                if(event != null) {
                    game.setTicketUrl(event.getUrl());
                    game.setSeatGeekId(event.getId());
                }

                String opponentFullName = tds.get(6).text().replace(" ", "");
                String opponentName = StringUtils.capitalize(namesUtil.getNBATeamName(opponentFullName)); // get the team name

                if(tds.get(5).text().equals("@")) {
                    game.setAway(StringUtils.capitalize(teamName));
                    game.setHome(opponentName);
                } else {
                    game.setAway(opponentName);
                    game.setHome(StringUtils.capitalize(teamName));
                }
                games.add(game);
            }
        }

        return games;
    }

    public void saveScheduleToFile(List<SimpleGame> games, String teamName) {
        File file = new File("nba-schedules/" + teamName + "-schedule.json");
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
        String json = gson.toJson(games);

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

    }
}
