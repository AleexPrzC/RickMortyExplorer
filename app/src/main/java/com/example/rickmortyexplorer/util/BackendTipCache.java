package com.example.rickmortyexplorer.util;

import android.content.Context;
import android.content.SharedPreferences;

public class BackendTipCache {
    private static final String PREFS_NAME = "backend_tip_cache";
    private static final String KEY_TIP = "tip";

    private final SharedPreferences sharedPreferences;

    public BackendTipCache(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveTip(String tip) {
        sharedPreferences.edit()
                .putString(KEY_TIP, tip)
                .apply();
    }

    public String getTip() {
        return sharedPreferences.getString(KEY_TIP, null);
    }
}
