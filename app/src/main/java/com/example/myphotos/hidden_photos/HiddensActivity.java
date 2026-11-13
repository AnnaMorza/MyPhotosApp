package com.example.myphotos.hidden_photos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myphotos.R;
import com.example.myphotos.photos_showing.PhotoAdapter;
import com.example.myphotos.photos_showing.full_screens_logic.HiddensPhotoViewerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HiddensActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView emptyText;
    private HiddensManager hiddensManager;
    private final List<Uri> hidedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiddens);

        recyclerView = findViewById(R.id.hiddensRecyclerView);
        emptyText = findViewById(R.id.emptyTextHiddens);
        hiddensManager = new HiddensManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHiddenPhotos();
    }

    private void loadHiddenPhotos() {
        Set<String> hiddensUriStrings = hiddensManager.getAllHiddens();

        Log.d("HiddensDebug", "Найдено скрытых фоток: " + hiddensUriStrings.size());

        hidedPhotos.clear();
        for (String uriString : hiddensUriStrings) {
            hidedPhotos.add(Uri.parse(uriString));
        }

        if (hidedPhotos.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        emptyText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        PhotoAdapter adapter = new PhotoAdapter(hidedPhotos, position -> {
            ArrayList<String> uriStrings = new ArrayList<>();
            for (Uri uri : hidedPhotos) {
                uriStrings.add(uri.toString());
            }

            // Заменили PhotoViewerActivity на HiddensPhotoViewerActivity
            Intent intent = new Intent(this, HiddensPhotoViewerActivity.class);
            intent.putStringArrayListExtra("photo_uris", uriStrings);
            intent.putExtra("start_position", position);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}