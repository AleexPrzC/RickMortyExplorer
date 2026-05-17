package com.example.rickmortyexplorer.util;

import com.example.rickmortyexplorer.model.CharacterItem;
import com.example.rickmortyexplorer.model.Origin;

public class CharacterUtils {
    private CharacterUtils() {
    }

    public static String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Desconocido";
        }
        return value;
    }

    public static String formatSubtitle(CharacterItem character) {
        return safeText(character.getSpecies()) + " · " + safeText(character.getGender());
    }

    public static String formatStatus(CharacterItem character) {
        return safeText(character.getStatus());
    }

    public static String formatDetail(CharacterItem character) {
        return "ID: " + character.getId()
                + "\nEspecie: " + safeText(character.getSpecies())
                + "\nTipo: " + safeText(character.getType())
                + "\nGenero: " + safeText(character.getGender())
                + "\nOrigen: " + originName(character.getOrigin())
                + "\nUbicacion: " + originName(character.getLocation());
    }

    private static String originName(Origin origin) {
        if (origin == null) {
            return "Desconocido";
        }
        return safeText(origin.getName());
    }
}
