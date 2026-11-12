package com.example.myphotos.screen_activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myphotos.MainActivity;
import com.example.myphotos.R;
import com.example.myphotos.hidden_photos.HiddensManager;
import com.example.myphotos.likes.LikesManager;
import com.example.myphotos.photos_showing.PhotoGetter;
import com.example.myphotos.photos_showing.PhotoViewerActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class RandomGalleryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    // Альбом, из которого берутся фото
    private static final String ALBUM_NAME = "testsApp";

    private PhotoView imageView;
    private TextView fileNameText, dateText, historyText;
    private MaterialButton backButton, randomButton, nextButton;

    private LikesManager likesManager;
    private HiddensManager hiddensManager;

    private MaterialButton likeButton;
    private MaterialButton hidePhotoButton;

    // Доступные для рандома фото: все фото альбома МИНУС скрытые
    private final List<Uri> allPhotos = new ArrayList<>();

    // История просмотра — то, что реально показывалось на экране
    private final List<Uri> history = new ArrayList<>();
    private int historyIndex = -1;

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_gallery);

        imageView = findViewById(R.id.imageView);
        imageView.setOnViewTapListener((view, x, y) -> openHistoryFullscreen());
        fileNameText = findViewById(R.id.fileNameText);
        dateText = findViewById(R.id.dateText);
        historyText = findViewById(R.id.historyText);
        backButton = findViewById(R.id.backButton);
        randomButton = findViewById(R.id.randomButton);
        nextButton = findViewById(R.id.nextButton);
        likeButton = findViewById(R.id.likeButton);
        hidePhotoButton = findViewById(R.id.viewButton);

        likesManager = new LikesManager(this);
        hiddensManager = new HiddensManager(this);

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> finish());

        checkPermission();

        backButton.setOnClickListener(v -> showPreviousInHistory());
        randomButton.setOnClickListener(v -> showRandomPhoto());
        nextButton.setOnClickListener(v -> showNextInHistory());

        likeButton.setOnClickListener(v -> {
            if (!hasCurrentPhoto()) return;
            Uri currentUri = history.get(historyIndex);
            likesManager.toggleLike(currentUri.toString());
            updateLikeButtonState(currentUri);
        });

        hidePhotoButton.setOnClickListener(v -> hideCurrentPhoto());
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

    // === Загрузка фото альбома без скрытых ===
    private void loadPhotos() {
        allPhotos.clear();
        history.clear();
        historyIndex = -1;

        // Набор скрытых берём один раз, а не на каждой итерации цикла
        Set<String> hidden = hiddensManager.getAllHiddens();
        for (Uri uri : PhotoGetter.getAllPhotos(this)) {
            if (!hidden.contains(uri.toString())) {
                allPhotos.add(uri);
            }
        }

        if (allPhotos.isEmpty()) {
            Toast.makeText(this, "Нет доступных фото", Toast.LENGTH_SHORT).show();
            return;
        }

        history.add(allPhotos.get(random.nextInt(allPhotos.size())));
        historyIndex = 0;

        displayCurrentHistoryPhoto();
    }

    private boolean hasCurrentPhoto() {
        return historyIndex >= 0 && historyIndex < history.size();
    }

    // === RANDOM ===
    private void showRandomPhoto() {
        if (allPhotos.isEmpty()) {
            Toast.makeText(this, "Нет доступных фото", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri randomUri = allPhotos.get(random.nextInt(allPhotos.size()));

        history.add(randomUri);
        historyIndex = history.size() - 1;

        displayCurrentHistoryPhoto();
    }

    // === NEXT ===
    private void showNextInHistory() {
        if (historyIndex >= history.size() - 1) {
            Toast.makeText(this, "Дальше в истории пока ничего нет", Toast.LENGTH_SHORT).show();
            return;
        }
        historyIndex++;
        displayCurrentHistoryPhoto();
    }

    // === BACK ===
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

        updateLikeButtonState(uri);
    }

    // === Скрыть текущее фото ===
    private void hideCurrentPhoto() {
        if (!hasCurrentPhoto()) return;

        Uri hiddenUri = history.get(historyIndex);

        // 1. Добавляем в скрытые
        hiddensManager.hide(hiddenUri.toString());

        // 2. Убираем из доступных для рандома
        allPhotos.remove(hiddenUri);

        // 3. Убираем из истории ВСЕ вхождения этого фото (рандом мог выдать его несколько раз).
        //    Заодно считаем, сколько оставшихся записей было до текущей позиции:
        //    это и есть индекс записи, которая шла сразу после скрытого фото.
        List<Uri> newHistory = new ArrayList<>();
        int newIndex = 0;
        for (int i = 0; i < history.size(); i++) {
            Uri u = history.get(i);
            if (u.equals(hiddenUri)) continue;
            newHistory.add(u);
            if (i < historyIndex) newIndex++;
        }
        history.clear();
        history.addAll(newHistory);

        // 4. Скрыли последнее доступное фото — возвращаемся на главный экран
        if (allPhotos.isEmpty()) {
            Toast.makeText(this, "Это было последнее фото. Возврат на главный экран", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // 5. Фото в альбоме ещё есть
        if (history.isEmpty()) {
            // История опустела (скрытое фото было единственным в ней) — показываем новое случайное
            history.add(allPhotos.get(random.nextInt(allPhotos.size())));
            historyIndex = 0;
        } else {
            // Показываем следующее по истории, а если скрытое было последним — предыдущее
            historyIndex = Math.min(newIndex, history.size() - 1);
        }

        displayCurrentHistoryPhoto();
        Toast.makeText(this, "Фото скрыто", Toast.LENGTH_SHORT).show();
    }

    // Открытие полноэкранного просмотра истории
    private void openHistoryFullscreen() {
        if (history.isEmpty()) {
            return;
        }

        ArrayList<String> uriStrings = new ArrayList<>();
        for (Uri uri : history) {
            uriStrings.add(uri.toString());
        }

        Intent intent = new Intent(this, PhotoViewerActivity.class);
        intent.putStringArrayListExtra("photo_uris", uriStrings);
        intent.putExtra("start_position", historyIndex);
        startActivity(intent);
    }

    // === Лайк: обновляем иконку сердечка ===
    private void updateLikeButtonState(Uri uri) {
        boolean liked = likesManager.isLiked(uri.toString());
        likeButton.setIconResource(
                liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    private String formatDate(long unixSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
        return sdf.format(new Date(unixSeconds * 1000));
    }
}