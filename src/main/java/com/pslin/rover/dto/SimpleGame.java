package com.pslin.rover.dto;

import org.joda.time.DateTime;

/**
 * Represents a game (used for schedule).
 *
 * @author plin
 */
public class SimpleGame {

    private int id;

    private DateTime date;

    private String away;

    private String home;

    private String time;

    private String ticketUrl;

    private String title;

    private int seatGeekId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getAway() {
        return away;
    }

    public void setAway(String away) {
        this.away = away;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSeatGeekId() {
        return seatGeekId;
    }

    public void setSeatGeekId(int seatGeekId) {
        this.seatGeekId = seatGeekId;
    }

    @Override
    public String toString() {
        return "SimpleGame{" +
                "id=" + id +
                ", date=" + date +
                ", away='" + away + '\'' +
                ", home='" + home + '\'' +
                ", time='" + time + '\'' +
                ", ticketUrl='" + ticketUrl + '\'' +
                ", title='" + title + '\'' +
                ", seatGeekId=" + seatGeekId +
                '}';
    }
}
