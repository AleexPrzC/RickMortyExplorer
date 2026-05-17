package com.example.rickmortyexplorer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rickmortyexplorer.model.CharacterItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class CharacterCache {
    private static final String PREFS_NAME = "character_cache";
    private static final String KEY_CHARACTERS = "characters";

    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public CharacterCache(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveCharacters(List<CharacterItem> characters) {
        if (characters == null) {
            return;
        }
        sharedPreferences.edit()
                .putString(KEY_CHARACTERS, gson.toJson(characters))
                .apply();
    }

    public List<CharacterItem> getCharacters() {
        String json = sharedPreferences.getString(KEY_CHARACTERS, null);
        if (json == null) {
            return Collections.emptyList();
        }

        Type type = new TypeToken<List<CharacterItem>>() {
        }.getType();
        List<CharacterItem> characters = gson.fromJson(json, type);
        return characters == null ? Collections.emptyList() : characters;
    }
}
