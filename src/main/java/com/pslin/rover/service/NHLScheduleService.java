package com.pslin.rover.service;

import com.google.gson.*;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author plin
 */
public class NHLScheduleService {

    private static final String HOCKEY_REFERENCE_SCHEDULE_URL = "http://www.hockey-reference.com/teams/%s/2016_games.html";

    private Properties abbreviationProp;
    private Properties namesProp;

    private NamesUtil namesUtil;

    private SeatGeekService seatGeekService;

    public static void main(String[] args) throws IOException, ParseException {
        NHLScheduleService scheduleService = new NHLScheduleService();

        for(Object key : scheduleService.abbreviationProp.keySet()) {
            String teamName = (String) key;
            System.out.println(teamName);
            List<SimpleGame> games = scheduleService.getSchedule(teamName);
            scheduleService.saveScheduleToFile(games, teamName);
        }

//        List<SimpleGame> games = scheduleService.getSchedule("kings");
//        for(SimpleGame game : games) {
//            System.out.println(game.toString());
//        }
    }

    public NHLScheduleService() throws IOException {
        setup();
        namesUtil = new NamesUtil();
        seatGeekService = new SeatGeekService();
    }

    private void setup() throws IOException {
        abbreviationProp = new Properties();
        namesProp = new Properties();
        abbreviationProp.load(new FileInputStream(new File("resources/nhl-abbr.properties")));
        namesProp.load(new FileInputStream(new File("resources/nhl-team-names.properties")));
    }

    public List<SimpleGame> getSchedule(String teamName) throws IOException, ParseException {
        // Get ticket urls first
        Map<LocalDate, Event> ticketsMap = seatGeekService.getTicketUrls(teamName, SportLeague.NHL);

        String teamAbbreviation = abbreviationProp.getProperty(teamName);
        Document document = Jsoup.connect(String.format(HOCKEY_REFERENCE_SCHEDULE_URL, teamAbbreviation)).get();
        Element scheduleTable = document.getElementById("games");
        Elements rows = scheduleTable.getElementsByTag("tr");

//        DateFormat fromDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        DateFormat completeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm a z");
        DateTimeFormatter completeDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm a ZZZ");
        List<SimpleGame> games = new ArrayList<>();
        int index = 0;
        for(Element row : rows) {
            Elements tds = row.getElementsByTag("td");
            if(tds.size() > 0 && !tds.get(0).text().equals("GP")) {
                SimpleGame game = new SimpleGame();
                game.setId(index);

                String date = tds.get(1).text();
                String time = tds.get(2).text();
                String completeDate;
                completeDate = date + "T" + time + " America/New_York";
                game.setDate(completeDateFormat.parseDateTime(completeDate));
                game.setTime(time);

                Event event = ticketsMap.get(new LocalDate(completeDateFormat.parseDateTime(completeDate)));
                if(event != null) {
                    game.setTicketUrl(event.getUrl());
                    game.setSeatGeekId(event.getId());
                }

                String opponentFullName = tds.get(4).text().replace(" ", "").replace(".", "");
                String opponentName = StringUtils.capitalize(namesProp.getProperty(opponentFullName));
                // is away game
                if(tds.get(3).text().equals("@")) {
                    game.setAway(StringUtils.capitalize(teamName));
                    game.setHome(opponentName);
                } else { // is home game
                    game.setAway(opponentName);
                    game.setHome(StringUtils.capitalize(teamName));
                }
                games.add(game);
                index++;
            }
        }

        return games;
    }

    public void createMasterSchedule() throws IOException, ParseException {
        for(Object key : namesUtil.getNhlAbbreviationProp().keySet()) {
            List<SimpleGame> games = getSchedule((String) key);
            Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
            String json = gson.toJson(games);

        }


    }

    public void saveScheduleToFile(List<SimpleGame> games, String teamName) {
        File file = new File("schedules/" + teamName + "-schedule.json");
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
