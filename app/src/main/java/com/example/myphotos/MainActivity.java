package com.example.myphotos;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    private PhotoView imageView;
    private TextView fileNameText, dateText, historyText;
    private MaterialButton backButton, randomButton, nextButton;

    // Все фото на устройстве (используется только для того, чтобы выбирать случайное фото)
    private final List<Uri> allPhotos = new ArrayList<>();

    // История просмотра — именно то, что реально показывалось на экране
    private final List<Uri> history = new ArrayList<>();
    private int historyIndex = -1; // текущая позиция внутри history

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_gallery);

        // Инициализация UI
        imageView = findViewById(R.id.imageView);
        fileNameText = findViewById(R.id.fileNameText);
        dateText = findViewById(R.id.dateText);
        historyText = findViewById(R.id.historyText);
        backButton = findViewById(R.id.backButton);
        randomButton = findViewById(R.id.randomButton);
        nextButton = findViewById(R.id.nextButton);

        // Проверка разрешений и загрузка фото
        checkPermission();

        // Обработчики кнопок
        backButton.setOnClickListener(v -> showPreviousInHistory());
        randomButton.setOnClickListener(v -> showRandomPhoto());
        nextButton.setOnClickListener(v -> showNextInHistory());
    }

    // === Разрешения ===
    private void checkPermission() {
        String permission = Build.VERSION.SDK_INT >= 33
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE);
        } else {
            loadPhotos();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPhotos();
            } else {
                Toast.makeText(this, "Нужен доступ к фото, чтобы приложение работало", Toast.LENGTH_LONG).show();
            }
        }
    }

    // === Загрузка списка всех фото из галереи ===
    private void loadPhotos() {
        allPhotos.clear();
        allPhotos.addAll(getPhotoList());

        if (allPhotos.isEmpty()) {
            Toast.makeText(this, "Фото не найдены", Toast.LENGTH_SHORT).show();
            return;
        }

        // Первое показанное фото тоже считается частью истории
        history.clear();
        history.add(allPhotos.get(random.nextInt(allPhotos.size())));
        historyIndex = 0;

        displayCurrentHistoryPhoto();
    }

    private List<Uri> getPhotoList() {
        List<Uri> photoUris = new ArrayList<>();
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = getContentResolver().query(
                collection, projection, null, null, sortOrder)) {

            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(collection, id);
                    photoUris.add(contentUri);
                }
            }
        }
        return photoUris;
    }

    // === RANDOM: выбираем новое случайное фото и добавляем его в историю ===
    private void showRandomPhoto() {
        if (allPhotos.isEmpty()) return;

        Uri randomUri = allPhotos.get(random.nextInt(allPhotos.size()));



        history.add(randomUri);
        historyIndex = history.size() - 1;

        displayCurrentHistoryPhoto();
    }

    // === NEXT: идём вперёд по уже накопленной истории ===
    private void showNextInHistory() {
        if (historyIndex >= history.size() - 1) {
            Toast.makeText(this, "Дальше в истории пока ничего нет", Toast.LENGTH_SHORT).show();
            return;
        }
        historyIndex++;
        displayCurrentHistoryPhoto();
    }

    // === BACK: идём назад по истории ===
    private void showPreviousInHistory() {
        if (historyIndex <= 0) {
            Toast.makeText(this, "Это первое фото в истории", Toast.LENGTH_SHORT).show();
            return;
        }
        historyIndex--;
        displayCurrentHistoryPhoto();
    }

    // === Отображение текущего фото из истории + метаданных ===
    private void displayCurrentHistoryPhoto() {
        Uri uri = history.get(historyIndex);

        // Сбрасываем зум предыдущего фото перед показом нового
        imageView.setScale(1f, false);

        Glide.with(this)
                .load(uri)
                .fitCenter()
                .into(imageView);

        String[] projection = {
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(
                        MediaStore.Images.Media.DISPLAY_NAME));
                long dateSec = cursor.getLong(cursor.getColumnIndexOrThrow(
                        MediaStore.Images.Media.DATE_ADDED));

                fileNameText.setText(name);
                dateText.setText(formatDate(dateSec));
            }
        }

        historyText.setText("History: " + (historyIndex + 1) + "/" + history.size());

        backButton.setEnabled(historyIndex > 0);
        backButton.setAlpha(historyIndex > 0 ? 1f : 0.4f);

        nextButton.setEnabled(historyIndex < history.size() - 1);
        nextButton.setAlpha(historyIndex < history.size() - 1 ? 1f : 0.4f);
    }

    private String formatDate(long unixSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
        return sdf.format(new Date(unixSeconds * 1000));
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}