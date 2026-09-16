package com.example.myphotos;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoViewerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImageView backIcon;
    private MaterialButton likeButton;
    private TextView fileNameText, dateText;

    private LikesManager likesManager;
    private final List<Uri> photoUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        viewPager = findViewById(R.id.viewPager);
        backIcon = findViewById(R.id.backIcon);
        likeButton = findViewById(R.id.likeButton);
        fileNameText = findViewById(R.id.fileNameText);
        dateText = findViewById(R.id.dateText);

        likesManager = new LikesManager(this);

        // Получаем список фото и стартовую позицию из Intent
        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("photo_uris");
        int startPosition = getIntent().getIntExtra("start_position", 0);

        if (uriStrings == null || uriStrings.isEmpty()) {
            finish();
            return;
        }

        for (String uriString : uriStrings) {
            photoUris.add(Uri.parse(uriString));
        }

        FullscreenPhotoAdapter adapter = new FullscreenPhotoAdapter(photoUris);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition, false);

        backIcon.setOnClickListener(v -> finish());

        likeButton.setOnClickListener(v -> {
            Uri currentUri = photoUris.get(viewPager.getCurrentItem());
            likesManager.toggleLike(currentUri.toString());
            updateLikeButtonState(currentUri);
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateInfoForPosition(position);
            }
        });

        // Показываем инфо для стартового фото
        updateInfoForPosition(startPosition);
    }

    private void updateInfoForPosition(int position) {
        Uri uri = photoUris.get(position);

        updateLikeButtonState(uri);

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

    private void updateLikeButtonState(Uri uri) {
        boolean liked = likesManager.isLiked(uri.toString());
        likeButton.setIconResource(liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    private String formatDate(long unixSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
        return sdf.format(new Date(unixSeconds * 1000));
    }
}
