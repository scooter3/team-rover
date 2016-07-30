package com.pslin.rover;

import com.pslin.rover.entity.NBATeam;

/**
 * @author plin
 */
public class NBATeamStats {

    private String name;

    private int gamesPlayed;

    private int wins;

    private int losses;

    private int divisionStanding;

    private int conferenceStanding;

    private NBATeam.Conference conference;

    private NBATeam.Division division;

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

    public NBATeam.Conference getConference() {
        return conference;
    }

    public void setConference(NBATeam.Conference conference) {
        this.conference = conference;
    }

    public NBATeam.Division getDivision() {
        return division;
    }

    public void setDivision(NBATeam.Division division) {
        this.division = division;
    }

    public String getStreak() {
        return streak;
    }

    public void setStreak(String streak) {
        this.streak = streak;
    }

    @Override
    public String toString() {
        return "NBATeamStats{" +
                "name='" + name + '\'' +
                ", gamesPlayed=" + gamesPlayed +
                ", wins=" + wins +
                ", losses=" + losses +
                ", divisionStanding=" + divisionStanding +
                ", conferenceStanding=" + conferenceStanding +
                ", conference=" + conference +
                ", division=" + division +
                ", streak='" + streak + '\'' +
                '}';
    }
}
