package com.example.rickmortyexplorer.model;

public class CharacterItem {
    private int id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private Origin origin;
    private Origin location;
    private String image;
    private String url;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecies() {
        return species;
    }

    public String getType() {
        return type;
    }

    public String getGender() {
        return gender;
    }

    public Origin getOrigin() {
        return origin;
    }

    public Origin getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}
