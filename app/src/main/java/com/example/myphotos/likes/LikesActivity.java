package com.example.myphotos.likes;

import android.content.Intent;
import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myphotos.hidden_photos.HiddensManager;
import com.example.myphotos.photos_showing.PhotoAdapter;
import com.example.myphotos.R;
import com.example.myphotos.photos_showing.full_screens_logic.LikesPhotoViewerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LikesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private LikesManager likesManager;
    private final List<Uri> likedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        recyclerView = findViewById(R.id.likesRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        likesManager = new LikesManager(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLikedPhotos();
    }

    private void loadLikedPhotos() {
        Set<String> likedUriStrings = likesManager.getAllLiked();
        HiddensManager hiddensManager = new HiddensManager(this);
        Set<String> hiddenUris = hiddensManager.getAllHiddens();

        Log.d("LikesDebug", "Найдено лайков: " + likedUriStrings.size());

        likedPhotos.clear();
        for (String uriString : likedUriStrings) {
            // Добавляем в сетку лайков ТОЛЬКО если фото не скрыто
            if (!hiddenUris.contains(uriString)) {
                likedPhotos.add(Uri.parse(uriString));
            }
        }

        if (likedPhotos.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        emptyText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        PhotoAdapter adapter = new PhotoAdapter(likedPhotos, position -> {
            ArrayList<String> uriStrings = new ArrayList<>();
            for (Uri uri : likedPhotos) {
                uriStrings.add(uri.toString());
            }

            Intent intent = new Intent(this, LikesPhotoViewerActivity.class);
            intent.putStringArrayListExtra("photo_uris", uriStrings);
            intent.putExtra("start_position", position);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}