package com.example.myphotos.photos_showing.full_screens_logic;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.myphotos.R;
import com.example.myphotos.likes.LikesManager;
import com.example.myphotos.hidden_photos.HiddensManager; // <-- ДОБАВИЛИ
import com.google.android.material.button.MaterialButton;

public class LikesPhotoViewerActivity extends BasePhotoViewerActivity {

    private LikesManager likesManager;
    private HiddensManager hiddensManager; // <-- ДОБАВИЛИ
    private MaterialButton likeButton;
    private ImageView hiddenButton;        // <-- ДОБАВИЛИ

    @Override
    protected int getLayoutId() {
        return R.layout.activity_likes_viewer;
    }

    @Override
    protected void setupButtons() {
        likesManager = new LikesManager(this);
        hiddensManager = new HiddensManager(this); // <-- Инициализация
        likeButton = findViewById(R.id.likeButton);
        hiddenButton = findViewById(R.id.hiddenButton); // <-- Ищем глазик

        // 1. Кнопка "Убрать лайк" (как было)
        if (likeButton != null) {
            likeButton.setOnClickListener(v -> {
                int currentPosition = viewPager.getCurrentItem();
                Uri currentUri = photoUris.get(currentPosition);

                likesManager.removeLike(currentUri.toString()); // Убираем лайк
                photoUris.remove(currentPosition);

                if (photoUris.isEmpty()) {
                    finish();
                    return;
                }

                viewPager.getAdapter().notifyDataSetChanged();
                int newPosition = Math.max(0, currentPosition - 1);
                viewPager.setCurrentItem(newPosition, false);
                updateInfoForPosition(newPosition);
            });
        }

        // 2. Кнопка "Скрыть" (глазик)
        if (hiddenButton != null) {
            hiddenButton.setOnClickListener(v -> {
                int currentPosition = viewPager.getCurrentItem();
                Uri currentUri = photoUris.get(currentPosition);

                // Скрываем фото (но лайк НЕ снимаем!)
                hiddensManager.toggleHiddens(currentUri.toString());

                // Убираем из текущего списка отображения (в лайках его больше не видно)
                photoUris.remove(currentPosition);

                Toast.makeText(this, "Фото скрыто, но лайк сохранен", Toast.LENGTH_SHORT).show();

                if (photoUris.isEmpty()) {
                    finish();
                    return;
                }

                viewPager.getAdapter().notifyDataSetChanged();
                int newPosition = Math.max(0, currentPosition - 1);
                viewPager.setCurrentItem(newPosition, false);
                updateInfoForPosition(newPosition);
            });
        }
    }

    @Override
    protected void updateLikeButtonState(Uri uri) {
        if (likeButton != null) {
            // В лайках сердечко всегда заполнено
            likeButton.setIconResource(R.drawable.ic_heart_filled);
        }
    }
}