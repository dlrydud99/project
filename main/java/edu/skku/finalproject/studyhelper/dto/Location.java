package edu.skku.finalproject.studyhelper.dto;

public class Location{
    String name = "";
    String latlng = "";

    public Location() {
    }

    public Location(String name, String latlng) {
        this.name = name;
        this.latlng = latlng;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
