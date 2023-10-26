package com.example.cct46_ver1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton clockButton;
    private ImageButton logEntryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeButton = findViewById(R.id.homeButton);
        clockButton = findViewById(R.id.clockButton);
        logEntryButton = findViewById(R.id.logentryButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You are already on the home page.", Toast.LENGTH_LONG).show();
            }
        });

        logEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LogEntryActivity.class);
                startActivity(intent);
            }
        });

        switchToClockActivity();
    }
    public void switchToClockActivity() {
        clockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ClockActivity.class);
                startActivity(intent);
            }
        });
    }
}