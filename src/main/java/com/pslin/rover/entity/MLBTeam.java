package com.pslin.rover.entity;

import org.mongodb.morphia.annotations.Entity;

/**
 * Represents an MLB Team.
 *
 * @author plin
 */
@Entity(value = "mlbTeam", noClassnameStored = true)
public class MLBTeam extends AbstractTeam {

    private League league;

    private Division division;

    public enum League{AMERICAN, NATIONAL}

    public enum Division{EAST, CENTRAL, WEST}

    public MLBTeam() {

    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }
}
