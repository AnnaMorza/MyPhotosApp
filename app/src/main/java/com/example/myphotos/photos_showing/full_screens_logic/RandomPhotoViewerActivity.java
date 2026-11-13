package com.example.myphotos.photos_showing.full_screens_logic;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myphotos.MainActivity;
import com.example.myphotos.R;
import com.example.myphotos.hidden_photos.HiddensManager;
import com.example.myphotos.likes.LikesManager;
import com.google.android.material.button.MaterialButton;

public class RandomPhotoViewerActivity extends BasePhotoViewerActivity {

    private LikesManager likesManager;
    private HiddensManager hiddensManager; // <-- ДОБАВИЛИ
    private MaterialButton likeButton;
    private ImageView hiddenButton;        // <-- ДОБАВИЛИ

    @Override
    protected int getLayoutId() {
        return R.layout.activity_random_views;
    }

    @Override
    protected void setupButtons() {
        likesManager = new LikesManager(this);
        hiddensManager = new HiddensManager(this); // <-- Инициализация
        likeButton = findViewById(R.id.likeButton);
        hiddenButton = findViewById(R.id.hiddenButton); // <-- Ищем кнопку

        // 1. Кнопка лайка (ставит/убирает)
        if (likeButton != null) {
            likeButton.setOnClickListener(v -> {
                Uri currentUri = photoUris.get(viewPager.getCurrentItem());
                likesManager.toggleLike(currentUri.toString());
                updateLikeButtonState(currentUri);
            });
        }

        // 2. Кнопка "Скрыть" (Глазик)
        if (hiddenButton != null) {
            hiddenButton.setOnClickListener(v -> {
                int currentPosition = viewPager.getCurrentItem();
                Uri currentUri = photoUris.get(currentPosition);

                hiddensManager.toggleHiddens(currentUri.toString());
                photoUris.remove(currentPosition);

                // Если фото больше нет в истории просмотра
                if (photoUris.isEmpty()) {
                    Toast.makeText(this, "Все фото скрыты. Возврат на главный экран", Toast.LENGTH_SHORT).show();

                    // 1. Возвращаемся в MainActivity, очищая стек
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    // 2. Закрываем текущую активность
                    finish();
                    return;
                }

                // Если фото остались — обновляем адаптер и переключаемся на предыдущее
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
            boolean liked = likesManager.isLiked(uri.toString());
            likeButton.setIconResource(liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        }
    }

    // (Опционально) Обновление иконки глазика, если он должен меняться
    @Override
    protected void updateHiddenButtonState(Uri uri) {
        if (hiddenButton != null) {
            boolean isHidden = hiddensManager.isHidden(uri.toString());
            hiddenButton.setImageResource(isHidden ? R.drawable.visibility_off_24 : R.drawable.ic_visibility_24);
        }
    }
}