package com.pslin.rover.service;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.pslin.rover.SportLeague;
import com.pslin.rover.dto.Event;
import com.pslin.rover.dto.SeatGeekPerformer;
import com.pslin.rover.dto.SimpleGame;
import com.pslin.rover.util.NamesUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author plin
 */
public class SeatGeekService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeatGeekService.class);

    private static final String SEAT_GEEK_API_URL = "http://api.seatgeek.com/2/events?performers.id=%s&per_page=%s&aid=11657";

    private static final String SEAT_GEEK_PERFORMER_URL = "http://api.seatgeek.com/2/performers?slug=%s";

    private NamesUtil namesUtil;

    public static void main(String[] args) throws IOException {
        SeatGeekService seatGeekService = new SeatGeekService();
//        List<SimpleGame> games = seatGeekService.getTicketsInfo("kings");
//        Map<DateTime, Event> games = seatGeekService.getTicketUrls("kings");

//        for(Map.Entry<DateTime, Event> game : games.entrySet()) {
//            System.out.println(game.getKey() + " " + game.getValue().getUrl());
//        }

//        Gson gson = new GsonBuilder().setDateFormat("EEE MMM d, yyyy").create();
//        String json = gson.toJson(games);
//        System.out.println(json);

//        Properties nameProperties = new Properties();
//        nameProperties.load(new FileInputStream(new File("resources/nhl-slug-names.properties")));
//        for(Object slug : nameProperties.keySet()) {
//            SeatGeekPerformer performer = seatGeekService.getPerformer(nameProperties.getProperty((String) slug));
//            System.out.println(performer.toString());
//        }
    }

    public SeatGeekService() {
        namesUtil = new NamesUtil();
    }

    public Map<LocalDate, Event> getTicketUrls(String teamName, SportLeague sportLeague) throws IOException {
        int performerId = getPerformer(teamName, sportLeague).getId();

        HttpClient client = HttpClientBuilder.create().build();
        String url = String.format(SEAT_GEEK_API_URL, performerId, "82");
        HttpGet request = new HttpGet(url);

        LOGGER.debug("Seat Geek URL: " + url);

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(entity);

        JsonParser jsonParser = new JsonParser();
        JsonArray events = jsonParser.parse(json).getAsJsonObject().get("events").getAsJsonArray();

        Type collectionType = new TypeToken<Collection<Event>>(){}.getType();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm").create();

        List<Event> eventsList = gson.fromJson(events, collectionType);
//        setGameIds(eventsList, teamName);

        Map<LocalDate, Event> ticketsMap = new HashMap<>();
        for(Event event : eventsList) {
            LocalDate date = new LocalDate(event.getDate());

            ticketsMap.put(date, event);
        }

        return ticketsMap;
    }

    /**
     * Get tickets info and update the schedule file directly.
     *
     * @param teamName
     * @return
     * @throws IOException
     */
    @Deprecated
    public List<SimpleGame> getTicketsInfo(String teamName, SportLeague sportLeague) throws IOException {
        int performerId = getPerformer(teamName, sportLeague).getId();

        HttpClient client = HttpClientBuilder.create().build();
        String url = String.format(SEAT_GEEK_API_URL, performerId, "82");
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(entity);

        JsonParser jsonParser = new JsonParser();
        JsonArray events = jsonParser.parse(json).getAsJsonObject().get("events").getAsJsonArray();

        Type collectionType = new TypeToken<Collection<Event>>(){}.getType();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm").create();

        List<Event> eventsList = gson.fromJson(events, collectionType);
        setGameIds(eventsList, teamName);

        return addToSchedule(eventsList, teamName);
    }

    private void setGameIds(List<Event> events, String teamName) throws IOException{
        String json = new String(Files.readAllBytes(Paths.get("schedules/" + teamName + "-schedule.json")));

//        Gson gson = new GsonBuilder().setDateFormat("EEE MMM d, yyyy").create();
        Gson gson = Converters.registerDateTime(new GsonBuilder()).create();
        Type collectionType = new TypeToken<Collection<SimpleGame>>(){}.getType();
        List<SimpleGame> games = gson.fromJson(json, collectionType);

        Map<DateTime, SimpleGame> gamesMap = new HashMap<>();
        for(SimpleGame game : games) {
            gamesMap.put(game.getDate(), game);
        }

        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
        Date date = null;
        for(Event event : events) {
            try {
                date = dateFormat.parse(dateFormat.format(event.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int gameId = gamesMap.get(new DateTime(date)).getId();
            event.setGameId(gameId);
        }
    }

    @Deprecated
    private List<SimpleGame> addToSchedule(List<Event> events, String teamName) throws IOException {
        Map<Integer, Event> eventsWithId = new HashMap<>();
        for(Event event : events) {
            eventsWithId.put(event.getGameId(), event);
        }

        String json = new String(Files.readAllBytes(Paths.get("schedules/" + teamName + "-schedule.json")));
        Gson gson = new GsonBuilder().setDateFormat("EEE MMM d, yyyy").create();
        Type collectionType = new TypeToken<Collection<SimpleGame>>(){}.getType();
        List<SimpleGame> games = gson.fromJson(json, collectionType);

        for(SimpleGame game : games) {
            Event thisEvent = eventsWithId.get(game.getId());
            if(thisEvent == null) {
                continue;
            }
            game.setSeatGeekId(thisEvent.getId());
            game.setTicketUrl(thisEvent.getUrl());
            game.setTitle(thisEvent.getTitle());
        }

        return games;
    }

    /**
     * Get the SeatGeekPeformer json object
     *
     * @param teamName - team name to search for
     * @param sportLeague - league of team
     * @return
     * @throws IOException
     */
    public SeatGeekPerformer getPerformer(String teamName, SportLeague sportLeague) throws IOException {
        String slugName;
        switch (sportLeague) {
            case NHL:
                slugName = namesUtil.getNHLSlugName(teamName);
                break;

            case NBA:
                slugName = namesUtil.getNBASlugName(teamName);
                break;


            default:
                LOGGER.error("Invalid league: " + sportLeague);
                return null;
        }

        HttpClient client = HttpClientBuilder.create().build();
        String url = String.format(SEAT_GEEK_PERFORMER_URL, slugName);
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(entity);

        JsonParser jsonParser = new JsonParser();
        JsonArray performers = jsonParser.parse(json).getAsJsonObject().get("performers").getAsJsonArray();

        if(performers.size() == 0) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(performers.get(0), SeatGeekPerformer.class);
    }
}
