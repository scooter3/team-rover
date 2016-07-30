package com.pslin.rover.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author plin
 */
public class SeatGeekPerformer {

    private int id;

    private String name;

    @SerializedName("short_name")
    private String shortName;

    private String url;

    private String slug;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "SeatGeekPerformer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", url='" + url + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
