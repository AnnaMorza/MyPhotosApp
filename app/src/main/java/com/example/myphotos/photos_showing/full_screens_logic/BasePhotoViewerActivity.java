package com.example.myphotos.photos_showing.full_screens_logic;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myphotos.R;
import com.example.myphotos.hidden_photos.HiddensManager;
import com.example.myphotos.photos_showing.FullscreenPhotoAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class BasePhotoViewerActivity extends AppCompatActivity {

    // Защищенные поля, чтобы наследники могли их использовать
    protected ViewPager2 viewPager;
    protected ImageView backIcon;
    protected TextView fileNameText, dateText;
    protected final List<Uri> photoUris = new ArrayList<>();
    protected boolean barsVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId()); // Наследник сам скажет, какую разметку использовать

        // Инициализация общих элементов
        viewPager = findViewById(R.id.viewPager);
        backIcon = findViewById(R.id.backIcon);
        fileNameText = findViewById(R.id.fileNameText);
        dateText = findViewById(R.id.dateText);

        // Получаем список фото и стартовую позицию
        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("photo_uris");
        int startPosition = getIntent().getIntExtra("start_position", 0);

        if (uriStrings == null || uriStrings.isEmpty()) {
            finish();
            return;
        }

        for (String uriString : uriStrings) {
            photoUris.add(Uri.parse(uriString));
        }

        // Создаем адаптер (он у вас общий для всех)
        FullscreenPhotoAdapter adapter = new FullscreenPhotoAdapter(photoUris, this::toggleBars);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition, false);

        backIcon.setOnClickListener(v -> finish());

        // Настройка специфичных кнопок (реализуется в наследниках)
        setupButtons();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateInfoForPosition(position);
            }
        });

        updateInfoForPosition(startPosition);
    }

    // Абстрактные методы, которые ОБЯЗАНЫ реализовать наследники
    protected abstract int getLayoutId(); // ID разметки (R.layout.activity_likes_viewer и т.д.)
    protected abstract void setupButtons(); // Настройка кнопок в нижней панели

    // === ОБЩАЯ ЛОГИКА (наследуется всеми) ===

    // Скрытие/показ панелей по тапу на фото
    protected void toggleBars() {
        barsVisible = !barsVisible;
        setBarVisible(findViewById(R.id.topBar), barsVisible);
        setBarVisible(findViewById(R.id.bottomBar), barsVisible);
    }

    protected void setBarVisible(View bar, boolean visible) {
        if (bar == null) return;
        if (visible) {
            bar.setVisibility(View.VISIBLE);
            bar.animate().alpha(1f).setDuration(200);
        } else {
            bar.animate().alpha(0f).setDuration(200)
                    .withEndAction(() -> bar.setVisibility(View.GONE));
        }
    }

    // Обновление имени файла и даты при свайпе
    protected void updateInfoForPosition(int position) {
        Uri uri = photoUris.get(position);
        updateLikeButtonState(uri);
        updateHiddenButtonState(uri);

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
    }

    // Метод для обновления иконки лайка (наследники могут переопределить, если им не нужно)
    protected void updateLikeButtonState(Uri uri) {
        // По умолчанию ничего не делаем, так как не во всех разметках есть likeButton
    }

    // Метод для обновления кнопки глазика
    protected void updateHiddenButtonState(Uri uri) {
        ImageView hiddenButton = findViewById(R.id.hiddenButton);
        if (hiddenButton != null) {
            HiddensManager hiddensManager = new HiddensManager(this);
            boolean isHidden = hiddensManager.isHidden(uri.toString());

            // Если фото скрыто — показываем один глазик, если нет — другой
            hiddenButton.setImageResource(isHidden ? R.drawable.visibility_off_24 : R.drawable.ic_visibility_24);
        }
    }

    protected String formatDate(long unixSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
        return sdf.format(new Date(unixSeconds * 1000));
    }
}