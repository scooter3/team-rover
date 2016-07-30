package com.pslin.rover.dto;

import com.pslin.rover.Team;

import java.util.Date;

/**
 * Represents a completed NHL game.
 *
 * @author plin
 */
public class NHLCompletedGame extends CompletedGame {
    private int gameId;

    private Date date;

    private String time;

    private Team opponent;

    private Team thisTeam;

    private Result result;

    private Overtime overtime;

    private boolean homeGame;

    private int ownGoals;

    private int opponentGoals;

    private String score;

    public enum Result {WIN, LOSS, OTLOSS}

    public enum Overtime {OVERTIME, SHOOTOUT}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public Overtime getOvertime() {
        return overtime;
    }

    public void setOvertime(Overtime overtime) {
        this.overtime = overtime;
    }

    public boolean isHomeGame() {
        return homeGame;
    }

    public void setHomeGame(boolean homeGame) {
        this.homeGame = homeGame;
    }

    public int getOwnGoals() {
        return ownGoals;
    }

    public void setOwnGoals(int ownGoals) {
        this.ownGoals = ownGoals;
    }

    public int getOpponentGoals() {
        return opponentGoals;
    }

    public void setOpponentGoals(int opponentGoals) {
        this.opponentGoals = opponentGoals;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", date=" + date +
                ", time='" + time + '\'' +
                ", opponent=" + opponent +
                ", thisTeam=" + thisTeam +
                ", result=" + result +
                ", overtime=" + overtime +
                ", homeGame=" + homeGame +
                ", ownGoals=" + ownGoals +
                ", opponentGoals=" + opponentGoals +
                '}';
    }
}
