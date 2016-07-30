package com.pslin.rover.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Represents a SeatGeek event.
 *
 * @author plin
 */
public class Event {

    // Internal ID
    private int gameId;

    // SeatGeek ID
    private int id;

    private String title;

    @SerializedName("datetime_local")
    private Date date;

    private String url;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
