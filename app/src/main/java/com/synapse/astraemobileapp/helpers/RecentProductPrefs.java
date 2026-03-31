package com.synapse.astraemobileapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecentProductPrefs {
    private static final String PREF_NAME = "Astrae_Recent_Prefs";
    private static final String KEY_IDS = "recent_ids";
    private SharedPreferences prefs;

    public RecentProductPrefs(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void storeViewedProduct(String productId) {

        String savedString = prefs.getString(KEY_IDS, "");
        List<String> idList = new ArrayList<>(Arrays.asList(savedString.split(",")));
        idList.removeAll(Arrays.asList("", null)); // Clean up

        idList.remove(productId);
        idList.add(0, productId);

        if (idList.size() > 10) {
            idList = idList.subList(0, 10);
        }

        String resultString = android.text.TextUtils.join(",", idList);
        prefs.edit().putString(KEY_IDS, resultString).apply();
    }

    public List<String> getRecentIds() {
        String savedString = prefs.getString(KEY_IDS, "");
        if (savedString.isEmpty()) return new ArrayList<>();
        return Arrays.asList(savedString.split(","));
    }
}
