package com.example.healthproapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ExerciseActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean running = false;
    private double totaldistance;
    private double actualdistance;
    private int totalstepstaken = 0;
    private int stepsdiplayer;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private float accelerationThreshold = 14.0f; // Adjust this threshold as needed
    private Handler handler = new Handler();
    private boolean isCountingSteps = false;
    private Button startStopButton;

    private double distancetraversed = 0.0;
    private Button resetButton;
    private boolean countingEnabled = false;
    private Chronometer chronometer;
    private boolean chronometerRunning = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location previousLocation;
    private double totalDistance = 0.0;
    private boolean isGPSTracking = false; // Indicates if GPS tracking is active
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        sp = getSharedPreferences("FoodData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        TextView calBurnedTextView = findViewById(R.id.calburned);

        String string;
        string = sp.getString("caloriesburned", "0");
        calBurnedTextView.setText(string + " kcal");

        TextView stepsindicator = findViewById(R.id.stepstakencircularprogressindicator);
        stepsdiplayer = sp.getInt("totalsteps", 0);
        stepsindicator.setText(String.valueOf(stepsdiplayer));

        TextView distanceTextView = findViewById(R.id.distance);
        distanceTextView.setText(sp.getString("distance", "0"));

        int userHeight = sp.getInt("userHeight", 0);
        int userAge = sp.getInt("userAge", 0);
        int userWeight = sp.getInt("userWeight", 0);
        int userActivityLevel = sp.getInt("userActivityLevel", 0);
        int userGender = sp.getInt("userGender", 0);

        // Calculate recommended steps based on user inputs (use the previously provided formula)
        double recommendedSteps = calculateRecommendedSteps(userHeight, userAge, userWeight, userActivityLevel, userGender);

        // Set the maximum value for the circular progress bar
        CircularProgressIndicator circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
        circularProgressIndicator.setMax((int) recommendedSteps);
        circularProgressIndicator.setProgress(stepsdiplayer);

        TextView stepsmax = findViewById(R.id.maxsteps);
        string = String.valueOf((int) recommendedSteps);
        stepsmax.setText("of " + string + " steps");

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.settings);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.person) {
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

        Button reset = findViewById(R.id.buttonresetexercise);
        reset.setOnClickListener(v -> {
            circularProgressIndicator.setProgress(0);
            stepsindicator.setText("0");
            calBurnedTextView.setText("0"+ " kcal");
            distanceTextView.setText("0 mi");
            editor.putString("caloriesburned", "0");
            editor.putInt("totalsteps", 0);
            editor.putString("distance", "0");
            editor.apply();

        });


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Initialize the start/stop button and set its click listener
        startStopButton = findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCounting();
            }
        });

        // Initialize the reset button and set its click listener

        // Initialize the chronometer
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                // Update chronometer display
            }
        });

        // Initialize GPS tracking
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle location changes here
                if (previousLocation != null) {
                    // Calculate the distance and add to the totalDistance during active timer
                    if (isGPSTracking) {
                        float[] results = new float[1];
                        Location.distanceBetween(previousLocation.getLatitude(), previousLocation.getLongitude(), location.getLatitude(), location.getLongitude(), results);
                        totalDistance += results[0];
                    }
                }
                previousLocation = location;


                totaldistance = totalDistance / 1000.0;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Handle the case where permission is not granted
        }
    }

    private double calculateRecommendedSteps(int height, int age, int weight, int activityLevel, int gender) {
        double recommendedSteps = 0.0;

        // Calculate the Basal Metabolic Rate (BMR) based on gender and other factors
        if (gender == 1) {  // Male
            recommendedSteps = (66.5 + (13.75 * weight) + (5.003 * height) - (6.755 * age)) * getActivityFactor(activityLevel);
        } else {  // Female
            recommendedSteps = (655.1 + (9.563 * weight) + (1.850 * height) - (4.676 * age)) * getActivityFactor(activityLevel);
        }

        return recommendedSteps;
    }

    private double getActivityFactor(int activityLevel) {
        double activityFactor = 0.0;

        switch (activityLevel) {
            case 1:  // Sedentary
                activityFactor = 1.2;
                break;
            case 2:  // Lightly active
                activityFactor = 1.375;
                break;
            case 3:  // Moderately active
                activityFactor = 1.55;
                break;
            case 4:  // Very active
                activityFactor = 1.725;
                break;
            case 5:  // Super active
                activityFactor = 1.9;
                break;
        }

        return activityFactor;
    }

    private void toggleCounting() {
        countingEnabled = !countingEnabled;
        sp = getSharedPreferences("FoodData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (countingEnabled) {
            // Start counting
            startStopButton.setText("Stop");
            // Set the base time to the current system time
            chronometer.setBase(SystemClock.elapsedRealtime());
            startChronometer();

            isGPSTracking = true;
            startGPSTracking();
        } else {
            // Stop counting
            totalSteps = 0f;
            previousTotalSteps = 0f;
            stepsdiplayer = totalstepstaken + stepsdiplayer;
            TextView stepsindicator = findViewById(R.id.stepstakencircularprogressindicator);
            CircularProgressIndicator circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
            circularProgressIndicator.setProgress(stepsdiplayer);
            stepsindicator.setText(String.valueOf(stepsdiplayer));
            double caldisplayer = calculateCaloriesBurned(stepsdiplayer);
            displayCaloriesBurned(caldisplayer);

            editor.putInt("totalsteps", stepsdiplayer);
            editor.apply();
            TextView stepsTaken = findViewById(R.id.stepCountTextView);
            stepsTaken.setText("Step Count: 0");
            chronometer.setBase(SystemClock.elapsedRealtime());
            startStopButton.setText("Start");
            stopChronometer();

            isGPSTracking = false;
            stopGPSTracking();
        }
    }

    private void startChronometer() {
        if (!chronometerRunning) {
            chronometer.start();
            chronometerRunning = true;
        }
    }

    private void stopChronometer() {
        if (chronometerRunning) {
            chronometer.stop();
            chronometerRunning = false;
        }
    }

    private void startGPSTracking() {
        // Request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Handle the case where permission is not granted
        }
    }

    private void stopGPSTracking() {
        locationManager.removeUpdates(locationListener);
        sp = getSharedPreferences("FoodData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        distancetraversed = totaldistance / 1.609;

        actualdistance = distancetraversed + actualdistance;
        TextView distanceTextView = findViewById(R.id.distance);
        String string;
        string = String.format("%.2f", actualdistance);
        distanceTextView.setText(string + " mi");
        editor.putString("distance", string);
        editor.apply();

    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null) {
            Toast.makeText(this, "No accelerometer detected on this device", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView stepsTaken = findViewById(R.id.stepCountTextView);

        if (running && countingEnabled) {
            float acceleration = calculateAcceleration(event.values);
            if (acceleration > accelerationThreshold) {
                if (!isCountingSteps) {
                    isCountingSteps = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            totalSteps++;
                            int currentSteps = (int) (totalSteps - previousTotalSteps);
                            totalstepstaken = (int) (totalSteps - previousTotalSteps);
                            System.out.println(totalstepstaken);
                            stepsTaken.setText(String.valueOf(currentSteps));
                            isCountingSteps = false;
                        }
                    }, 1000); // Count a step after 1 second of motion
                }
            }
        }
    }

    // Helper method to calculate acceleration from accelerometer values
    private float calculateAcceleration(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];

        // Calculate the magnitude of acceleration
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No need to implement anything in this function for this app
    }

    // Method to calculate calories burned (you can adjust the formula based on your requirements)
    private double calculateCaloriesBurned(int steps) {
        // Replace this formula with your own calculation based on user inputs
        double caloriesBurned = steps * 0.04; // Example formula, adjust as needed
        return caloriesBurned;
    }

    // Method to display calories burned in a TextView
    private void displayCaloriesBurned(double caloriesBurned) {
        sp = getSharedPreferences("FoodData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        TextView calBurnedTextView = findViewById(R.id.calburned);
        String string;
        string = String.valueOf(Math.round(caloriesBurned));
        editor.putString("caloriesburned", string);
        editor.apply();
        calBurnedTextView.setText(string + " kcal");
    }
}
