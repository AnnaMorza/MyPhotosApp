package com.example.myphotos;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class LikesManager {

    private static final String PREFS_NAME = "likes_prefs";
    private static final String KEY_LIKED_URIS = "liked_uris";

    private final SharedPreferences prefs;

    public LikesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLiked(String uriString) {
        Set<String> liked = prefs.getStringSet(KEY_LIKED_URIS, new HashSet<>());
        return liked.contains(uriString);
    }

    public void toggleLike(String uriString) {
        Set<String> liked = new HashSet<>(prefs.getStringSet(KEY_LIKED_URIS, new HashSet<>()));
        if (liked.contains(uriString)) {
            liked.remove(uriString);
        } else {
            liked.add(uriString);
        }
        prefs.edit().putStringSet(KEY_LIKED_URIS, liked).apply();
    }

    public Set<String> getAllLiked() {
        return new HashSet<>(prefs.getStringSet(KEY_LIKED_URIS, new HashSet<>()));
    }
}
