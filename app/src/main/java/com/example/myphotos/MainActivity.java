package com.example.myphotos;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.button.MaterialButton;
import android.widget.ImageView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Находим DrawerLayout (это корневой элемент в activity_main.xml)
        drawerLayout = findViewById(R.id.drawerLayout);

        // Иконка бургера — открывает панель справа
        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });

        // Обработка кликов по пунктам меню в панели (опционально)
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_item1) {
                // TODO: действие для пункта 1
            } else if (id == R.id.nav_item2) {
                // TODO: действие для пункта 2
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        MaterialButton randomButton = findViewById(R.id.button_random);
        MaterialButton yearsButton = findViewById(R.id.button_years);
        MaterialButton likesButton = findViewById(R.id.button_likes);
        MaterialButton aiButton = findViewById(R.id.button_ai);

        // RANDOM ведёт на экран галереи
        randomButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RandomGalleryActivity.class));
        });

        // Остальные кнопки пока заглушки (по желанию)
        yearsButton.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "Years later — скоро", android.widget.Toast.LENGTH_SHORT).show());


        likesButton.setOnClickListener(v ->
                startActivity(new Intent(this, LikesActivity.class)));

        aiButton.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "AI — скоро", android.widget.Toast.LENGTH_SHORT).show());
    }
}