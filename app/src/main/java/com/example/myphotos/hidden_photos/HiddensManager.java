package com.example.myphotos.hidden_photos;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class HiddensManager {
    private static final String HIDDENS_NAME = "hiddens_photos";
    private static final String KEY_HIDDENS_URIS = "hiddens_uris";
    private final SharedPreferences hiddens;

    public HiddensManager(Context context){
        hiddens = context.getSharedPreferences(HIDDENS_NAME, Context.MODE_PRIVATE);
    }

    /** Только добавляет в скрытые (в отличие от toggle, никогда не "раскрывает" фото). */
    public void hide(String uriString) {
        Set<String> hided = new HashSet<>(hiddens.getStringSet(KEY_HIDDENS_URIS, new HashSet<>()));
        hided.add(uriString);
        hiddens.edit().putStringSet(KEY_HIDDENS_URIS, hided).apply();
    }

    public void toggleHiddens(String uriString) {
        Set<String> hided = new HashSet<>(hiddens.getStringSet(KEY_HIDDENS_URIS, new HashSet<>()));
        if (hided.contains(uriString)) {
            hided.remove(uriString);
        } else {
            hided.add(uriString);
        }
        hiddens.edit().putStringSet(KEY_HIDDENS_URIS, hided).apply();
    }

    public void removeLike(String uriString){
        Set<String> liked = new HashSet<>(hiddens.getStringSet(KEY_HIDDENS_URIS, new HashSet<>()));
        if(liked.contains(uriString)){
            liked.remove(uriString);
        }
        hiddens.edit().putStringSet(KEY_HIDDENS_URIS, liked).apply();
    }

    public Set<String> getAllHiddens(){
        return new HashSet<>(hiddens.getStringSet(KEY_HIDDENS_URIS, new HashSet<>()));
    }

    public void clearAll(){
        hiddens.edit().clear().apply();
    }

    public boolean isHidden(String uri){
        return getAllHiddens().contains(uri);
    }
}