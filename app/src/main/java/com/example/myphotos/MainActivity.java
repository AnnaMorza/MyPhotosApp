package com.example.myphotos;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton randomButton = findViewById(R.id.button_random);
        MaterialButton yearsButton = findViewById(R.id.button_years);
        MaterialButton multiplayerButton = findViewById(R.id.button_multiplayer);
        MaterialButton likesButton = findViewById(R.id.button_likes);
        MaterialButton aiButton = findViewById(R.id.button_ai);

        // RANDOM ведёт на экран галереи
        randomButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RandomGalleryActivity.class));
        });

        // Остальные кнопки пока заглушки (по желанию)
        yearsButton.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "Years later — скоро", android.widget.Toast.LENGTH_SHORT).show());
        multiplayerButton.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "Multiplayer — скоро", android.widget.Toast.LENGTH_SHORT).show());



        likesButton.setOnClickListener(v ->
                startActivity(new Intent(this, LikesActivity.class)));

        aiButton.setOnClickListener(v ->
                android.widget.Toast.makeText(this, "AI — скоро", android.widget.Toast.LENGTH_SHORT).show());
    }
}
