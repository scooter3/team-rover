package com.pslin.rover.entity;

/**
 * Represents an NFL team.
 *
 * @author plin
 */
public class NFLTeam extends AbstractTeam {

    private Conference conference;

    private Division division;

    public enum Conference{AMERICAN, NATIONAL}

    public enum Division{NORTH, EAST, SOUTH, WEST}

    public NFLTeam() {

    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }
}
