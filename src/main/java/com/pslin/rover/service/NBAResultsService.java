package com.pslin.rover.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pslin.rover.Team;
import com.pslin.rover.dto.CompletedGame;
import com.pslin.rover.dto.NBACompletedGame;
import com.pslin.rover.serializer.DateTimeTypeConverter;
import com.pslin.rover.util.FTPUtil;
import com.pslin.rover.util.NamesUtil;
import org.joda.time.DateTime;
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
import java.util.List;

/**
 * @author plin
 */
public class NBAResultsService implements ResultsService {

    private static final String BASKETBALL_REFERENCE_RESULTS_PAGE = "http://www.basketball-reference.com/teams/%s/2016_games.html";

    private static final Logger LOGGER = LoggerFactory.getLogger(NBAResultsService.class);

    private NamesUtil namesUtil;

    public NBAResultsService() {
        namesUtil = new NamesUtil();
    }

    public static void main(String[] args) {
        NBAResultsService nbaResultsService = new NBAResultsService();
        nbaResultsService.processAllResults();
    }

    @Override
    public boolean processAllResults() {
        List<File> resultsFilesList = new ArrayList<>();
        for(Object key : namesUtil.getNbaAbbreviationProp().keySet()) {
            String teamName = (String) key;
            List<CompletedGame> games = getGamesResults(teamName);
            resultsFilesList.add(saveResultsToFile(games, teamName));
        }

        return FTPUtil.uploadFiles(resultsFilesList, "nba/resources/results");
    }

    @Override
    public List<CompletedGame> getGamesResults(String teamName) {
        String teamAbbreviation = namesUtil.getNBATeamAbbreviation(teamName);
        Document document;
        String url = String.format(BASKETBALL_REFERENCE_RESULTS_PAGE, teamAbbreviation);

        try{
            document = Jsoup.connect(url).get();
        } catch (Exception e) {
            LOGGER.error("There was an error connecting to the URL: " + url);
            throw new RuntimeException(e);
        }

        Element gamesTable = document.getElementById("teams_games");

        DateTimeFormatter completeDateFormat = DateTimeFormat.forPattern("EEE, MMM d, yyyy h:mm'p' zzz a");
        Elements rows = gamesTable.getElementsByTag("tr");
        List<CompletedGame> games = new ArrayList<>();
        int index = 0;
        for(Element row : rows) {
            Elements tds = row.getElementsByTag("td");
            if(tds.size() == 14) {
                NBACompletedGame game = new NBACompletedGame();
                game.setGameId(index++);

                // date and time
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
                game.setTime(formattedTime);

                // home or away
                if(tds.get(5).text().equals("@")) {
                    game.setHomeGame(false);
                } else {
                    game.setHomeGame(true);
                }

                // opponent
                Team opponent = new Team();
                opponent.setFullName(tds.get(6).text());
                game.setOpponent(opponent);

                // result
                if(tds.get(7).text().equals("W")) {
                    game.setResult(NBACompletedGame.Result.WIN);
                } else if(tds.get(7).text().equals("L")) {
                    game.setResult(NBACompletedGame.Result.LOSS);
                }

                // overtime
                if(tds.get(8).text().equals("OT")) {
                    game.setOvertime(true);
                } else {
                    game.setOvertime(false);
                }

                game.setPoints(Integer.parseInt(tds.get(9).text()));
                game.setOpponentPoints(Integer.parseInt(tds.get(10).text()));

                games.add(game);
            }
        }

        return games;
    }

    private File saveResultsToFile(List<CompletedGame> games, String teamName) {
        File file = new File("/" + teamName + "-results.json");
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

        return file;
    }
}
