package com.pslin.rover.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.pslin.rover.Team;
import com.pslin.rover.dto.CompletedGame;
import com.pslin.rover.dto.NHLCompletedGame;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for retrieving individual game results.
 *
 * @author plin
 */
public class NHLResultsService implements ResultsService {

    private static final String HOCKEY_REFERENCE_RESULTS_PAGE = "http://www.hockey-reference.com/teams/%s/2016_games.html";

    private static final Logger LOGGER = LoggerFactory.getLogger(NHLResultsService.class);

    @Inject
    private NamesUtil namesUtil;

    public NHLResultsService() {
        namesUtil = new NamesUtil();
    }

    /**
     * Retrieve, save, and upload results for all teams.
     *
     */
    @Override
    public boolean processAllResults() {
        List<File> resultsFilesList = new ArrayList<>();
        for(Object key : namesUtil.getNhlAbbreviationProp().keySet()) {
            String teamName = (String) key;
            List<CompletedGame> games = getGamesResults(teamName);
            resultsFilesList.add(saveResultsToFile(games, teamName));
        }

        return FTPUtil.uploadFiles(resultsFilesList, "nhl/resources/results");
    }

    @Override
    public List<CompletedGame> getGamesResults(String teamName) {
        String teamAbbreviation = namesUtil.getNHLTeamAbbreviation(teamName);
        Document document;
        String url = String.format(HOCKEY_REFERENCE_RESULTS_PAGE, teamAbbreviation);
        try {

            document = Jsoup.connect(url).timeout(10000).get();
        } catch (Exception e) {
            LOGGER.error("There was an error connecting to the URL: " + url, e);
            throw new RuntimeException(e);
        }

        Element gamesTable = document.getElementById("games");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a z");
        List<CompletedGame> games = new ArrayList<>();
        Elements rows = gamesTable.getElementsByTag("tr");
        int index = 0;
        for(Element row : rows) {
            Elements tds = row.getElementsByTag("td");
            if(tds.size() == 27) {
                NHLCompletedGame game = new NHLCompletedGame();
                game.setGameId(index);

                // game date and time
                try {
                    game.setDate(dateFormat.parse(tds.get(0).text() + " " + tds.get(1).text() + " EDT"));
                } catch (ParseException e) {
                    LOGGER.warn("There was an error parsing the game date.", e);
                }

                // home or away
                if(tds.get(2).text().equals("@")) {
                    game.setHomeGame(false);
                } else {
                    game.setHomeGame(true);
                }

                // opponent
                Team opponent = new Team();
                opponent.setFullName(tds.get(3).text());
                game.setOpponent(opponent);

                // overtime
                if(tds.get(7).text().equals("OT")) {
                    game.setOvertime(NHLCompletedGame.Overtime.OVERTIME);
                } else if(tds.get(7).text().equals("SO")) {
                    game.setOvertime(NHLCompletedGame.Overtime.SHOOTOUT);
                }

                // result
                if(tds.get(8).text().equals("W")) {
                    game.setResult(NHLCompletedGame.Result.WIN);
                } else if(tds.get(9).text().equals("L")) {
                    if(game.getOvertime() == null) {
                        game.setResult(NHLCompletedGame.Result.LOSS);
                    } else {
                        game.setResult(NHLCompletedGame.Result.OTLOSS);
                    }
                }

                // team goals
                game.setOwnGoals(Integer.parseInt(tds.get(4).text()));

                // opp goals
                game.setOpponentGoals(Integer.parseInt(tds.get(5).text()));

                games.add(game);
                index++;
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
