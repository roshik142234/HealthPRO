package com.example.healthproapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    Button nutrbutton;
    Button exerbutton;
    Button infobutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.person) {
                    startActivity(new Intent(getApplicationContext(), ExerciseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), NutritionActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.settings) {
                    startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

        nutrbutton = (Button) findViewById(R.id.button_nutrition);
        nutrbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNutritionActivity();
            }
        });
        exerbutton = (Button) findViewById(R.id.button_exercise);
        exerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExerciseActivity();
            }
        });
        infobutton = (Button) findViewById(R.id.button_info);
        infobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfoActivity();
            }
        });
    }

    public void openNutritionActivity() {
        Intent intent = new Intent(this, NutritionActivity.class);
        startActivity(intent);
    }
    public void openExerciseActivity() {
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }
    public void openInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}