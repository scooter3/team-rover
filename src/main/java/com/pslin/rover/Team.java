package com.pslin.rover;

/**
 * Represents a sports team.
 *
 * @author plin
 */
public class Team {

    private String name;

    private String city;

    private String fullName;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Team{" +
                "fullName='" + fullName + '\'' +
                '}';
    }
}
