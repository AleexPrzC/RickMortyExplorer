package com.example.rickmortyexplorer.model;

import java.util.List;

public class CharacterListResponse {
    private Info info;
    private List<CharacterItem> results;

    public Info getInfo() {
        return info;
    }

    public List<CharacterItem> getResults() {
        return results;
    }
}
