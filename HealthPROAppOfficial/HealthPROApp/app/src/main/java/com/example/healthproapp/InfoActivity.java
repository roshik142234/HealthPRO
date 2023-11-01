package com.example.healthproapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    SharedPreferences sp;
    private EditText nameEditText;
    private Button saveButton;
    private int age, gender, calorie, protein, fat, carb, steps;
    private String distance, calBurned, lastmeal;

    private DatabaseReference userDatabase;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<UserInfo> userInfoList;

    private boolean dataEntered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        sp = getSharedPreferences("FoodData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        age = sp.getInt("userAge", 0);
        gender = sp.getInt("userGender", 0);
        lastmeal = sp.getString("lastmeal", "0");
        calorie = sp.getInt("calprogress", 0);
        protein = sp.getInt("prot", 0);
        fat = sp.getInt("fat", 0);
        carb = sp.getInt("carb", 0);
        distance = sp.getString("distance", "0");
        steps = sp.getInt("totalsteps", 0);
        calBurned = sp.getString("caloriesburned", "0");

        nameEditText = findViewById(R.id.editTextName);
        saveButton = findViewById(R.id.buttonSave);

        userDatabase = FirebaseDatabase.getInstance().getReference("UserData");

        // Initialize the RecyclerView and the list to store user info
        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userInfoList = new ArrayList<>();
        adapter = new MyAdapter(this, userInfoList);
        recyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataEntered) {
                    // Get the person's name from the EditText
                    String personName = nameEditText.getText().toString().trim();

                    // Check if a name is entered
                    if (!personName.isEmpty()) {
                        dataEntered = true;

                        // Create a child node under the person's name in the Firebase database
                        DatabaseReference personRef = userDatabase.child(personName);

                        // Store user data as children under the person's name
                        personRef.setValue(new UserInfo(personName, age, gender, lastmeal, calorie, calBurned, carb, distance, fat, protein, steps));


                        // Show a message or navigate to a different screen as needed
                        nameEditText.setVisibility(View.GONE);
                        saveButton.setVisibility(View.GONE);

                        // Load user data from Firebase
                        userDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userInfoList.clear();
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    UserInfo userInfo = userSnapshot.getValue(UserInfo.class);

                                    // Extract the parent node's name
                                    String parentName = userSnapshot.getKey();

                                    // Set the name in UserInfo
                                    userInfo.setName(parentName);

                                    userInfoList.add(userInfo);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle an error if needed
                            }
                        });
                    }
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.settings);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.person) {
                    startActivity(new Intent(getApplicationContext(), ExerciseActivity.class));
                    overridePendingTransition(0, 0);
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), NutritionActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.settings) {
                    return true;
                }

                return false;
            }
        });
    }
}
