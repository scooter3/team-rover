package com.pslin.rover.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

/**
 * @author plin
 */
@Entity(value="nhlTeams", noClassnameStored = true)
public class NHLTeam {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String name;

    private String city;

    private Conference conference;

    private Division division;

    public enum Conference{WESTERN, EASTERN}

    public enum Division{ATLANTIC, METROPOLITAN, CENTRAL, PACIFIC}

    public NHLTeam() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
        return "NHLTeam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", conference=" + conference +
                ", division=" + division +
                '}';
    }
}
