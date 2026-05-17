package com.example.rickmortyexplorer.model;

public class FavoriteCharacter {
    private int id;
    private String name;
    private String image;
    private String status;
    private String species;

    public FavoriteCharacter() {
    }

    public FavoriteCharacter(CharacterItem character) {
        this.id = character.getId();
        this.name = character.getName();
        this.image = character.getImage();
        this.status = character.getStatus();
        this.species = character.getSpecies();
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}
