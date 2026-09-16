package com.example.myphotos;

import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        loadLikedPhotos(); // перезагружаем на случай, если лайки менялись в другом экране
    }

    private void loadLikedPhotos() {
        Set<String> likedUriStrings = likesManager.getAllLiked();

        Log.d("LikesDebug", "Найдено лайков: " + likedUriStrings.size());

        likedPhotos.clear();
        for (String uriString : likedUriStrings) {
            likedPhotos.add(Uri.parse(uriString));
        }

        if (likedPhotos.isEmpty()) {
            emptyText.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
            return;
        }

        emptyText.setVisibility(android.view.View.GONE);
        recyclerView.setVisibility(android.view.View.VISIBLE);

        PhotoAdapter adapter = new PhotoAdapter(likedPhotos, position -> {
            ArrayList<String> uriStrings = new ArrayList<>();
            for (Uri uri : likedPhotos) {
                uriStrings.add(uri.toString());
            }

            android.content.Intent intent = new android.content.Intent(this, PhotoViewerActivity.class);
            intent.putStringArrayListExtra("photo_uris", uriStrings);
            intent.putExtra("start_position", position);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}
