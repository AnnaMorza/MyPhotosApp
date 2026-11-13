package com.example.myphotos.photos_showing.full_screens_logic;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.myphotos.R;
import com.example.myphotos.hidden_photos.HiddensManager;
import com.example.myphotos.likes.LikesManager;
import com.google.android.material.button.MaterialButton;

public class HiddensPhotoViewerActivity extends BasePhotoViewerActivity {

    private HiddensManager hiddensManager;
    private LikesManager likesManager;
    private MaterialButton likeButton;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hiddens_viewer;
    }

    @Override
    protected void setupButtons() {
        hiddensManager = new HiddensManager(this);
        likesManager = new LikesManager(this);
        likeButton = findViewById(R.id.likeButton);

        // 1. Кнопка "Восстановить" (глазик)
        ImageView hiddenButton = findViewById(R.id.hiddenButton);
        if (hiddenButton != null) {
            hiddenButton.setOnClickListener(v -> {
                int currentPosition = viewPager.getCurrentItem();
                Uri currentUri = photoUris.get(currentPosition);

                hiddensManager.toggleHiddens(currentUri.toString());
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

        // 2. Кнопка "Лайк" (теперь работает и здесь!)
        if (likeButton != null) {
            likeButton.setOnClickListener(v -> {
                Uri currentUri = photoUris.get(viewPager.getCurrentItem());
                likesManager.toggleLike(currentUri.toString());
                updateLikeButtonState(currentUri);
            });
        }

        // 3. Кнопка "Удалить навсегда"
        ImageView deleteButton = findViewById(R.id.deleteButton);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> {
                Toast.makeText(this, "Удаление пока не настроено", Toast.LENGTH_SHORT).show();
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
}