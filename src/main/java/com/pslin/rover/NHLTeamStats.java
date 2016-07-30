package com.pslin.rover;

import com.pslin.rover.entity.NHLTeam;

/**
 * @author plin
 */
public class NHLTeamStats {

    private String name;

    private int gamesPlayed;

    private int wins;

    private int losses;

    private int overtimeLosses;

    private int points;

    private int divisionStanding;

    private int conferenceStanding;

    private NHLTeam.Conference conference;

    private NHLTeam.Division division;

    private String streak;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getOvertimeLosses() {
        return overtimeLosses;
    }

    public void setOvertimeLosses(int overtimeLosses) {
        this.overtimeLosses = overtimeLosses;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getDivisionStanding() {
        return divisionStanding;
    }

    public void setDivisionStanding(int divisionStanding) {
        this.divisionStanding = divisionStanding;
    }

    public int getConferenceStanding() {
        return conferenceStanding;
    }

    public void setConferenceStanding(int conferenceStanding) {
        this.conferenceStanding = conferenceStanding;
    }

    public String getStreak() {
        return streak;
    }

    public void setStreak(String streak) {
        this.streak = streak;
    }

    public NHLTeam.Conference getConference() {
        return conference;
    }

    public void setConference(NHLTeam.Conference conference) {
        this.conference = conference;
    }

    public NHLTeam.Division getDivision() {
        return division;
    }

    public void setDivision(NHLTeam.Division division) {
        this.division = division;
    }

    @Override
    public String toString() {
        return "TeamStats{" +
                "streak='" + streak + '\'' +
                ", conferenceStanding=" + conferenceStanding +
                ", divisionStanding=" + divisionStanding +
                ", points=" + points +
                ", overtimeLosses=" + overtimeLosses +
                ", losses=" + losses +
                ", wins=" + wins +
                ", gamesPlayed=" + gamesPlayed +
                ", name='" + name + '\'' +
                '}';
    }
}
