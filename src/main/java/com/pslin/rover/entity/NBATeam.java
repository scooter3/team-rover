package com.pslin.rover.entity;

import org.mongodb.morphia.annotations.Entity;

/**
 * Represents an NBA team.
 *
 * @author plin
 */
@Entity(value = "nbaTeam", noClassnameStored = true)
public class NBATeam  extends AbstractTeam {

    private Conference conference;

    private Division division;

    public enum Conference{WESTERN, EASTERN}

    public enum Division{ATLANTIC, CENTRAL, SOUTHEAST, NORTHWEST, PACIFIC, SOUTHWEST}

    public NBATeam() {

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

    @Override
    public String toString() {
        return super.toString() + " NBATeam{" +
                "conference=" + conference +
                ", division=" + division +
                '}';
    }
}
