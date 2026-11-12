package com.example.myphotos.photos_showing;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PhotoGetter {

    /**
     * Фото из альбома (папки) с заданным именем.
     * Имя сравнивается без учёта регистра: "testsApp" и "TestsApp" — один и тот же альбом.
     * Если ничего не нашлось, в Logcat (тег PhotoGetter) выводится список реально существующих альбомов.
     */
    public static List<Uri> getPhotosFromAlbum(Context context, String albumName) {
        List<Uri> photoUris = new ArrayList<>();
        Set<String> seenAlbums = new TreeSet<>();
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // Без selection: фильтруем по имени альбома уже в Java, так надёжнее
        try (Cursor cursor = context.getContentResolver().query(
                collection, projection, null, null, sortOrder)) {

            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int bucketColumn = cursor.getColumnIndexOrThrow(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                while (cursor.moveToNext()) {
                    String bucket = cursor.getString(bucketColumn);
                    if (bucket == null) continue;

                    seenAlbums.add(bucket);
                    if (bucket.trim().equalsIgnoreCase(albumName.trim())) {
                        long id = cursor.getLong(idColumn);
                        photoUris.add(ContentUris.withAppendedId(collection, id));
                    }
                }
            }
        }

        if (photoUris.isEmpty()) {
            Log.d("PhotoGetter", "Альбом '" + albumName + "' пуст или не найден. "
                    + "Альбомы на устройстве: " + seenAlbums);
        }
        return photoUris;
    }

    public static List<Uri> getAllPhotos(Context context) {
        List<Uri> photoUris = new ArrayList<>();
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                collection, projection, null, null, sortOrder)) {

            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    photoUris.add(ContentUris.withAppendedId(collection, id));
                }
            }
        }
        return photoUris;
    }
}