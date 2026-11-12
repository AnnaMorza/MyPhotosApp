package com.example.myphotos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myphotos.hidden_photos.HiddensActivity;
import com.example.myphotos.hidden_photos.HiddensManager;
import com.example.myphotos.likes.LikesActivity;
import com.example.myphotos.photos_showing.PhotoGetter;
import com.example.myphotos.screen_activities.RandomGalleryActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);

        // 1. Проверяем разрешения при запуске
        checkPermission();

        // 2. Настройка бокового меню (бургер)
        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_item1) {
                startActivity(new Intent(MainActivity.this, HiddensActivity.class));
                return true;
            } else if (id == R.id.nav_item2) {
                // TODO: пункт 2
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // 3. Настройка кнопок
        MaterialButton randomButton = findViewById(R.id.button_random);
        MaterialButton yearsButton = findViewById(R.id.button_years);
        MaterialButton likesButton = findViewById(R.id.button_likes);
        MaterialButton aiButton = findViewById(R.id.button_ai);

        // Кнопка RANDOM
        randomButton.setOnClickListener(v -> {
            // Получаем все фото из нужного альбома (замените "testsApp" на вашу папку)
            List<Uri> all = PhotoGetter.getAllPhotos(this);

            if (all.isEmpty()) {
                Toast.makeText(this, "В альбоме нет фотографий", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверяем, есть ли не скрытые фото
            HiddensManager hiddensManager = new HiddensManager(this);
            boolean hasAvailable = false;
            for (Uri uri : all) {
                if (!hiddensManager.isHidden(uri.toString())) {
                    hasAvailable = true;
                    break;
                }
            }

            if (!hasAvailable) {
                Toast.makeText(this, "Все фото скрыты", Toast.LENGTH_SHORT).show();
                return;
            }

            // Если всё ок — открываем
            startActivity(new Intent(MainActivity.this, RandomGalleryActivity.class));
        });

        yearsButton.setOnClickListener(v ->
                Toast.makeText(this, "Years later — скоро", Toast.LENGTH_SHORT).show());

        likesButton.setOnClickListener(v ->
                startActivity(new Intent(this, LikesActivity.class)));

        aiButton.setOnClickListener(v ->
                Toast.makeText(this, "AI — скоро", Toast.LENGTH_SHORT).show());
    }

    // === Логика разрешений ===

    private void checkPermission() {
        String permission = Build.VERSION.SDK_INT >= 33
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Доступ к фото разрешен", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Без доступа к фото приложение не работает", Toast.LENGTH_LONG).show();
            }
        }
    }
}