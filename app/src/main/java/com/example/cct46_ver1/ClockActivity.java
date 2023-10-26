package com.example.cct46_ver1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ClockActivity extends AppCompatActivity {

    Button checkInButton;
    Button checkOutButton;
    Button resetButton;
    TextView checkInTimeText;
    TextView accumulatedHoursText;
    EditText hoursWorkedEditText;
    Button submitHoursButton;
    ImageButton homeButton;
    ImageButton ToDoListButton;
    long accumulatedMilliseconds = 0;
    private boolean isCheckedIn;
    private static final String ACCUMULATED_HOURS_KEY = "accumulated_hours";
    private static final String CHECKED_IN_KEY = "checked_in";
    private static final String LAST_CHECK_IN_TIMESTAMP_KEY = "last_check_in_timestamp";
    private ArrayList<String> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        accumulatedMilliseconds = getAccumulatedMilliseconds();
        checkInButton = findViewById(R.id.checkInButton);
        checkOutButton = findViewById(R.id.checkOutButton);
        checkInTimeText = findViewById(R.id.checkInTimeText);
        accumulatedHoursText = findViewById(R.id.accumulatedHoursText);
        homeButton = findViewById(R.id.btn_Home);
        ToDoListButton = findViewById(R.id.btn_ToDoList);
        hoursWorkedEditText = findViewById(R.id.hoursWorkedEditText);
        submitHoursButton = findViewById(R.id.submitHoursButton);
        resetButton = findViewById(R.id.resetButton);
        String checkInText = checkInTimeText.getText().toString();

        isCheckedIn = getCheckedInStatus();


        double initialAccumulatedHours = loadAccumulatedHours();
        int accumulatedMinutes = (int) (initialAccumulatedHours * 60);
        int hours = accumulatedMinutes / 60;
        int minutes = accumulatedMinutes % 60;
        accumulatedHoursText.setText("Accumulated Hours: " + hours + "." + minutes);

        String lastText = loadLastText();
        if (!lastText.isEmpty()) {
            checkInTimeText.setText(lastText);
        } else {
            checkInTimeText.setText("Welcome to the app");
        }

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheckedIn) {
                    checkIn();
                    entries.add(checkInTimeText.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "You are already checked in.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToMainActivity();
            }
        });

        ToDoListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { switchToDoListActivity();
            }
        });

        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckedIn) {
                    checkOut();
                    entries.add(checkInTimeText.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "You are already checked out.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submitHoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hoursWorkedStr = hoursWorkedEditText.getText().toString();
                if (!hoursWorkedStr.isEmpty()) {
                    double totalHoursWorked = parseHoursWorked(hoursWorkedStr);
                    if (totalHoursWorked >= 0) {
                        double currentAccumulatedHours = loadAccumulatedHours();
                        double newAccumulatedHours = currentAccumulatedHours + totalHoursWorked;

                        int accumulatedMinutes = (int) ((newAccumulatedHours - (int) newAccumulatedHours) * 60);
                        int hours = (int) newAccumulatedHours;
                        int minutes = accumulatedMinutes;
                        accumulatedHoursText.setText("Accumulated Hours: " + hours + "." + minutes);

                        saveAccumulatedHours(newAccumulatedHours);

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accumulatedHoursText.setText("Accumulated Hours: 0.0");
                saveAccumulatedHours(0.0);

                isCheckedIn = false;
                saveCheckedInStatus(isCheckedIn);

                checkInTimeText.setText("Welcome");
                saveLastText("Welcome");

                clearSavedEntries();
            }
        });
    }

    private String loadLastText() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("lastText", "");
    }

    private void checkIn() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d h:mm a", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String entryText = "Checked In: " + currentTime;
        checkInTimeText.setText(entryText);
        saveLastText(entryText);

        isCheckedIn = true;

        long currentTimeMillis = System.currentTimeMillis();
        saveLastCheckInTimestamp(currentTimeMillis);
        saveEntry(entryText);

    }

    private void checkOut() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        long lastCheckInTimestamp = sharedPreferences.getLong(LAST_CHECK_IN_TIMESTAMP_KEY, 0);

        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTime = currentTimeMillis - lastCheckInTimestamp; // Calculate elapsed time

        // Calculate hours and minutes
        long hours = (elapsedTime / 3600000);
        long minutes = ((elapsedTime / 60000) % 60);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d h:mm a", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String checkOutText = "Checked Out: " + currentTime;
        String entry = "Checked Out: " + currentTime + " Hours: " + hours + "." + minutes;
        checkInTimeText.setText(checkOutText);

        isCheckedIn = false;
        saveLastText(checkInTimeText.getText().toString());

        double accumulatedHours = hours + (minutes / 60.0);
        double weeklyAccumulatedHours = getWeeklyAccumulatedHours() + accumulatedHours;

        int accumulatedMinutes = (int) ((weeklyAccumulatedHours - (int) weeklyAccumulatedHours) * 60);
        int intHours = (int) weeklyAccumulatedHours;
        minutes = accumulatedMinutes;
        accumulatedHoursText.setText("Accumulated Hours: " + intHours + "." + minutes);

        saveAccumulatedHours(weeklyAccumulatedHours);
        saveEntry(entry);

        // Reset the last check-in timestamp
        editor.putLong(LAST_CHECK_IN_TIMESTAMP_KEY, 0);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String lastText = loadLastText();
        saveCheckedInStatus(isCheckedIn);
        if (!lastText.isEmpty()) {
            checkInTimeText.setText(lastText);
        } else {
            checkInTimeText.setText("Welcome to the app");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCheckedIn = getCheckedInStatus();
    }

    private double getWeeklyAccumulatedHours() {
        return loadAccumulatedHours();
    }

    private void saveAccumulatedHours(double accumulatedHours) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(ACCUMULATED_HOURS_KEY, (float) accumulatedHours);
        editor.apply();
    }

    private double loadAccumulatedHours() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getFloat(ACCUMULATED_HOURS_KEY, 0.0f);
    }

    private void saveLastText(String text) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastText", text);
        editor.apply();
    }

    public void switchToMainActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void switchToDoListActivity() {
        Intent intent = new Intent(this, LogEntryActivity.class);
        intent.putStringArrayListExtra("entries", entries);
        startActivity(intent);
    }

    private double parseHoursWorked(String input) {
        try {
            double totalHours = Double.parseDouble(input);
            if (totalHours >= 0) {
                return totalHours;
            }
        } catch (NumberFormatException e) {
        }
        return -1;
        }
    private void saveEntry(String entry) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> savedEntries = sharedPreferences.getStringSet("entries", new HashSet<String>());

        savedEntries.add(entry);
        editor.putStringSet("entries", savedEntries);
        editor.apply();
    }
    private void clearSavedEntries() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("entries");
        editor.apply();
    }
    private boolean saveCheckedInStatus(boolean isCheckedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHECKED_IN_KEY, isCheckedIn);
        editor.apply();
        return isCheckedIn;
    }
    private boolean getCheckedInStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean(CHECKED_IN_KEY, false); // Default to "checked out"
    }
    private long getAccumulatedMilliseconds() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getLong("accumulatedMilliseconds", 0);
    }
    private void saveLastCheckInTimestamp(long timestamp) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_CHECK_IN_TIMESTAMP_KEY, timestamp);
        editor.apply();
    }
    private long loadLastCheckInTimestamp() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_CHECK_IN_TIMESTAMP_KEY, 0);
    }

    }




