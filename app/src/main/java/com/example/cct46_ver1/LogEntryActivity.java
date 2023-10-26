package com.example.cct46_ver1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class LogEntryActivity extends AppCompatActivity {

    ImageButton returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logentry);

        returnButton = findViewById(R.id.btn_Return);

        ArrayList<String> entries = loadEntries();

        if (entries != null) {
            ListView entriesListView = findViewById(R.id.entriesListView);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);
            entriesListView.setAdapter(adapter);
        }
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogEntryActivity.this, ClockActivity.class);
                startActivity(intent);
            }
        });
    }

    private ArrayList<String> loadEntries() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Set<String> savedEntries = sharedPreferences.getStringSet("entries", new HashSet<String>());
        return new ArrayList<>(savedEntries);
    }
}

