package com.pslin.rover.dto;

import com.pslin.rover.Team;
import org.joda.time.DateTime;

/**
 * @author plin
 */
public class NBACompletedGame extends CompletedGame{
    private int gameId;

    private DateTime date;

    private String time;

    private Team opponent;

    private Team thisTeam;

    private Result result;

    private boolean overtime;

    private boolean homeGame;

    private int points;

    private int opponentPoints;

    private String score;

    public enum Result {WIN, LOSS}

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Team getOpponent() {
        return opponent;
    }

    public void setOpponent(Team opponent) {
        this.opponent = opponent;
    }

    public Team getThisTeam() {
        return thisTeam;
    }

    public void setThisTeam(Team thisTeam) {
        this.thisTeam = thisTeam;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isOvertime() {
        return overtime;
    }

    public void setOvertime(boolean overtime) {
        this.overtime = overtime;
    }

    public boolean isHomeGame() {
        return homeGame;
    }

    public void setHomeGame(boolean homeGame) {
        this.homeGame = homeGame;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getOpponentPoints() {
        return opponentPoints;
    }

    public void setOpponentPoints(int opponentPoints) {
        this.opponentPoints = opponentPoints;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
