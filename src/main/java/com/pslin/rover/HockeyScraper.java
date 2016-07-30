package com.pslin.rover;

import com.google.gson.Gson;
import com.pslin.rover.dto.NHLCompletedGame;
import com.pslin.rover.service.EmailService;
import com.pslin.rover.service.NHLResultsService;
import com.pslin.rover.service.NHLStatsService;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author plin
 */
public class HockeyScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HockeyScraper.class);

    private static final String HOCKEY_REFERENCE_TEAM_PAGE = "http://www.hockey-reference.com/teams/LAK/2016.html";

    private static final String HOCKEY_REFERENCE_RESULTS_PAGE = "http://www.hockey-reference.com/teams/LAK/2016_games.html";

    private static final String NHL_CONF_STANDINGS_PAGE = "http://www.nhl.com/ice/standings.htm?season=20152016&type=CON";

    private static final String NHL_DIV_STANDINGS_PAGE = "http://www.nhl.com/ice/standings.htm?season=20152016&type=DIV";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        NHLResultsService resultsService = new NHLResultsService();
        boolean resultsSuccess = resultsService.processAllResults();

        NHLStatsService NHLStatsService = new NHLStatsService();
        boolean statsSuccess = NHLStatsService.processAllStats();
        long totalTime = System.currentTimeMillis() - start;
        long totalTimeSeconds = totalTime / 1000;

        EmailService emailService = new EmailService();
        if (resultsSuccess && statsSuccess) {
            System.out.println("Success");
            emailService.sendSimpleEmail("your_email@gmail.com", "Hockey Scraper Completed", "Total time: " + totalTimeSeconds + " seconds");
        } else {
            String errorMessage = "There was a problem connecting to the FTP server";
            System.err.println(errorMessage);
            emailService.sendSimpleEmail("your_email@gmail.com", "Hockey Scraper Not Successful", errorMessage + "Total time: " + totalTimeSeconds + " seconds");
        }

        System.exit(0);
    }

//    public static void main(String[] args) throws IOException, ParseException {
//        EmailService emailService = new EmailService();
//        try {
//            List<CompletedGame> games = getGamesResults();
//            File resultsFile = createJson(games);
//            TeamStats teamStats = getStats();
//            File statsFile = createTeamStatsJson(teamStats);
//
//            boolean resultsSuccess = uploadResults(resultsFile);
//            boolean statsSuccess = uploadResults(statsFile);
//
//            if(resultsSuccess && statsSuccess) {
//                System.out.println("Success");
//                emailService.sendSimpleEmail("your_email@gmail.com", "Hockey Scraper Completed", "-Jarvis");
//            } else {
//                String errorMessage = "There was a problem connecting to the FTP server";
//                System.err.println(errorMessage);
//                emailService.sendSimpleEmail("your_email@gmail.com", "Hockey Scraper Not Successful", errorMessage + "\n\n-Jarvis");
//            }
//
//        } catch (Exception e) {
//            System.out.println("Error");
//            emailService.sendSimpleEmail("your_email@gmail.com", "Hockey Scraper Error", e.getMessage() + "\n\n-Jarvis");
//        }
//    }

    private static NHLTeamStats getStats() throws IOException {
//        Document document = Jsoup.connect(HOCKEY_REFERENCE_TEAM_PAGE).get();
//        Element teamTable = document.getElementById("team_stats");
//        Elements rows = teamTable.getElementsByTag("tr");
//        Element row = rows.get(1);
//        Elements tds = row.getElementsByTag("td");

        NHLTeamStats NHLTeamStats = new NHLTeamStats();
//        teamStats.setName(tds.get(0).text());
//        teamStats.setGamesPlayed(Integer.parseInt(tds.get(2).text()));
//        teamStats.setWins(Integer.parseInt(tds.get(3).text()));
//        teamStats.setLosses(Integer.parseInt(tds.get(4).text()));
//        teamStats.setOvertimeLosses(Integer.parseInt(tds.get(5).text()));
//        teamStats.setPoints(Integer.parseInt(tds.get(6).text()));

        getNHLStats(NHLTeamStats);

        return NHLTeamStats;
    }

    private static void getNHLStats(NHLTeamStats NHLTeamStats) throws IOException {
        // Get conference standing and other stats
        Document document = Jsoup.connect(NHL_CONF_STANDINGS_PAGE).get();
        Element standingsTable = document.select("#wideCol > div.contentBlock > table:nth-child(8)").first();
        Elements rows = standingsTable.getElementsByTag("tr");
        for (Element row : rows) {
            Elements tds = row.getElementsByTag("td");
            if (tds.size() == 0) {
                continue;
            }
            if (tds.get(1).text().replace("\u00a0", "").equals("Los Angeles")) {
                NHLTeamStats.setConferenceStanding(Integer.parseInt(tds.get(0).text()));
                NHLTeamStats.setGamesPlayed(Integer.parseInt(tds.get(3).text()));
                NHLTeamStats.setWins(Integer.parseInt(tds.get(4).text()));
                NHLTeamStats.setLosses(Integer.parseInt(tds.get(5).text()));
                NHLTeamStats.setOvertimeLosses(Integer.parseInt(tds.get(6).text()));
                NHLTeamStats.setPoints(Integer.parseInt(tds.get(7).text()));
                NHLTeamStats.setStreak(tds.get(16).text());
                break;
            }
        }

        // Get division standing
        Document divDocument = Jsoup.connect(NHL_DIV_STANDINGS_PAGE).get();
        Element divStandingsTable = divDocument.select("#wideCol > div.contentBlock > table:nth-child(10)").first();
        Elements divRows = divStandingsTable.getElementsByTag("tr");
        for (Element row : divRows) {
            Elements tds = row.getElementsByTag("td");
            if (tds.size() == 0) {
                continue;
            }
            if (tds.get(1).text().replace("\u00a0", "").equals("Los Angeles")) {
                NHLTeamStats.setDivisionStanding(Integer.parseInt(tds.get(0).text()));
            }
        }
    }

    private static List<NHLCompletedGame> getGamesResults() throws IOException, ParseException {
        Document document = Jsoup.connect(HOCKEY_REFERENCE_RESULTS_PAGE).get();
        Element gamesTable = document.getElementById("games");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a z");
        List<NHLCompletedGame> games = new ArrayList<>();
        Elements rows = gamesTable.getElementsByTag("tr");
        int index = 0;
        for (Element row : rows) {
            Elements tds = row.getElementsByTag("td");
            if (tds.size() == 16) {
                NHLCompletedGame game = new NHLCompletedGame();
                game.setGameId(index);

                // game date and time
                game.setDate(dateFormat.parse(tds.get(1).text() + " " + tds.get(2).text() + " EDT"));

                // home or away
                if (tds.get(3).text().equals("@")) {
                    game.setHomeGame(false);
                } else {
                    game.setHomeGame(true);
                }

                // opponent
                Team opponent = new Team();
                opponent.setFullName(tds.get(4).text());
                game.setOpponent(opponent);

                // overtime
                if (tds.get(6).text().equals("OT")) {
                    game.setOvertime(NHLCompletedGame.Overtime.OVERTIME);
                } else if (tds.get(6).text().equals("SO")) {
                    game.setOvertime(NHLCompletedGame.Overtime.SHOOTOUT);
                }

                // result
                if (tds.get(5).text().equals("W")) {
                    game.setResult(NHLCompletedGame.Result.WIN);
                } else if (tds.get(5).text().equals("L")) {
                    if (game.getOvertime() == null) {
                        game.setResult(NHLCompletedGame.Result.LOSS);
                    } else {
                        game.setResult(NHLCompletedGame.Result.OTLOSS);
                    }
                }

                // team goals
                game.setOwnGoals(Integer.parseInt(tds.get(7).text()));

                // opp goals
                game.setOpponentGoals(Integer.parseInt(tds.get(8).text()));

                games.add(game);
                index++;
            }
        }

        return games;
    }

    private static File createJson(List<NHLCompletedGame> games) {
        String json = new Gson().toJson(games);
        File file = new File("results.json");

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(json);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static File createTeamStatsJson(NHLTeamStats NHLTeamStats) {
        String json = new Gson().toJson(NHLTeamStats);
        File file = new File("stats.json");

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(json);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

//    private static File getTicketInfo() {
//        SeatGeekService seatGeekService = new SeatGeekService();
//        String json = "";
//        try {
//            json = seatGeekService.getTicketsInfo();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        File file = new File("tickets.json");
//        Writer writer = null;
//        try {
//            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//            writer.write(json);
//            return file;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                writer.close();
//            } catch (IOException e) {
//
//            }
//        }
//
//        return null;
//    }

    private static boolean uploadResults(File file) throws IOException {
        if (file == null) {
            return false;
        }

        FTPClient ftp = new FTPClient();

        int reply;
        ftp.connect("SOME_FTP_HOST");
        ftp.login("FTPLOGIN", "PASSWORD");
        reply = ftp.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }

        InputStream inputStream = new FileInputStream(file);
        ftp.storeFile("/" + file.getName(), inputStream);

        return true;
    }
}