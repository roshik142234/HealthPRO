package com.example.healthproapp;
// Importing the required libraries

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

// Creating the main class and extending it with the AppCompatActivity class
public class NutritionActivity extends AppCompatActivity {

    // Declaring the required variables
    double totalproteinneeded, totalfatneeded, totalcarbsneeded;


    String url = "https://api.api-ninjas.com/v1/nutrition?query=";
    int calprogress, proteinprogress, fatprog, carbprog;
    private TextInputEditText age, height, weight, activtyEx;
    Button fetchbutton, resetbutton;
    TextView dataresult;
    double totalCalories = 0;
    EditText searchbar;
    ProgressBar calbar, proteinbar, fatbar, carbbar;
    private RadioGroup gender;
    private MaterialRadioButton male, female;
    private TextView required, textView1, textView2, textView3, textView4, textView5, textView6, totalcal, currentcal, proteincurrent, proteinmax, fatcurrent, fatmax, carbcurrent, carbmax;
    private AppCompatButton calculate, reset;

    SharedPreferences sp;

    // Creating the onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the content view to the activity_main.xml file
        setContentView(R.layout.activity_nutrition);

        sp = getSharedPreferences("FoodData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();


        fatcurrent = findViewById(R.id.fats1);
        fatmax = findViewById(R.id.fats2);
        carbcurrent = findViewById(R.id.carbs1);
        carbmax = findViewById(R.id.carbs2);
        proteinbar = findViewById(R.id.linearProgressIndicator);
        fatbar = findViewById(R.id.linearProgressIndicator2);
        carbbar = findViewById(R.id.linearProgressIndicator3);
        totalcal = findViewById(R.id.totalcalories);

        calbar = findViewById(R.id.circularProgressIndicator);
        currentcal = findViewById(R.id.textView);
        calprogress = sp.getInt("calprogress", 0);
        String calprogresString;
        calprogresString = String.valueOf(calprogress);
        currentcal.setText(calprogresString);
        calbar.setProgress(calprogress);

        proteinprogress = sp.getInt("prot", 0);
        proteincurrent = findViewById(R.id.protein1);
        proteinmax = findViewById(R.id.protein2);
        String protprog;
        protprog = String.valueOf(proteinprogress);
        proteincurrent.setText(protprog);
        proteinbar.setProgress(proteinprogress);

        fatprog = sp.getInt("fat", 0);
        String fatprogg;
        fatprogg = String.valueOf(fatprog);
        fatcurrent.setText(fatprogg);
        fatbar.setProgress(fatprog);

        carbprog = sp.getInt("carb", 0);
        String carbstring;
        carbstring = String.valueOf(carbprog);
        carbcurrent.setText(carbstring);
        carbbar.setProgress(carbprog);

        totalCalories = sp.getInt("totcal", 0);
        String string;
        string = String.format("%.2f", totalCalories);
        totalcal.setText("of " + string + " kcal");
        calbar.setMax((int) Math.round(totalCalories));
        totalproteinneeded = sp.getInt("totpro", 0);


        proteinbar.setMax((int) Math.round(totalproteinneeded));
        string = String.format("%.2f", totalproteinneeded);
        proteinmax.setText("of " + string);

        totalfatneeded = sp.getInt("totfat", 0);
        fatbar.setMax((int) Math.round(totalfatneeded));
        string = String.format("%.2f", totalfatneeded);
        fatmax.setText("of " + string);

        totalcarbsneeded = sp.getInt("totcarb", 0);
        carbbar.setMax((int) Math.round(totalcarbsneeded));
        string = String.format("%.2f", totalcarbsneeded);
        carbmax.setText("of " + string);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

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
                    return true;
                } else if (itemId == R.id.settings) {
                    startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

        dataresult = findViewById(R.id.dataresult);
        fetchbutton = findViewById(R.id.add_button);
        searchbar = findViewById(R.id.food_search_edit_text);

        fetchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://api.api-ninjas.com/v1/nutrition?query=";
                String foodName = searchbar.getText().toString();
                editor.putString("lastmeal", foodName);
                editor.apply();

                if (!foodName.equals("")) {
                    url += foodName;
                    fetchData(url);
                } else {
                    Toast.makeText(NutritionActivity.this, "Please enter food name!", Toast.LENGTH_SHORT).show();
                }


            }


            public void fetchData(String url) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                try {
                                    JSONArray array = new JSONArray(response);
                                    JSONObject object = array.getJSONObject(0);


                                    calprogress = calprogress + object.getInt("calories");
                                    editor.putInt("calprogress", calprogress);
                                    editor.apply();
                                    String calprogresString;
                                    calprogresString = String.valueOf(calprogress);
                                    currentcal.setText(calprogresString);
                                    calbar.setProgress(calprogress);


                                    proteinprogress = proteinprogress + object.getInt("protein_g");
                                    editor.putInt("prot", proteinprogress);
                                    editor.apply();
                                    String protprog;
                                    protprog = String.valueOf(proteinprogress);
                                    proteincurrent.setText(protprog);
                                    proteinbar.setProgress(proteinprogress);

                                    fatprog = fatprog + object.getInt("fat_total_g");
                                    editor.putInt("fat", fatprog);
                                    editor.apply();
                                    String fatprogg;
                                    fatprogg = String.valueOf(fatprog);
                                    fatcurrent.setText(fatprogg);
                                    fatbar.setProgress(fatprog);

                                    carbprog = carbprog + object.getInt("carbohydrates_total_g");
                                    editor.putInt("carb", carbprog);
                                    editor.apply();
                                    String carbstring;
                                    carbstring = String.valueOf(carbprog);
                                    carbcurrent.setText(carbstring);
                                    carbbar.setProgress(carbprog);



                                } catch (JSONException e) {
                                    Toast.makeText(NutritionActivity.this, "Item not found!", Toast.LENGTH_SHORT).show();
                                    //txtFoodName.setText("Not Found");
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(NutritionActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();

                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("X-Api-Key", "JDp4u9XGA23rPg3ZfzWEPQ==AjxhIYcKDlTrIptL");

                        return params;
                    }
                };
                queue.add(getRequest);
            }
        });

        //UI

        resetbutton = findViewById(R.id.resetbutton);

        // Initializing the variables
        age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        gender = findViewById(R.id.gender);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        activtyEx = findViewById(R.id.activity);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView_2);
        textView3 = findViewById(R.id.textView_3);
        textView4 = findViewById(R.id.textView_4);
        textView5 = findViewById(R.id.textView_5);
        textView6 = findViewById(R.id.textView_6);
        required = findViewById(R.id.required);
        calculate = findViewById(R.id.calculate);
        reset = findViewById(R.id.reset);

        // Creating the onClickListener for the reset button
        // This will reset all the values to the default values
        reset.setOnClickListener(v -> {
            age.setText("");
            height.setText("");
            weight.setText("");
            activtyEx.setText("");
            gender.clearCheck();
            textView1.setText("");
            textView2.setText("");
            textView3.setText("");
            textView4.setText("");
            textView5.setText("");
            textView6.setText("");
            required.setVisibility(View.GONE);

        });


        resetbutton.setOnClickListener(v -> {
            editor.putInt("calprogress", 0);
            calbar.setProgress(0);
            calprogress = 0;
            currentcal.setText("0");

            editor.putInt("prot", 0);
            proteinbar.setProgress(0);
            proteinprogress = 0;
            proteincurrent.setText("0");

            editor.putInt("fat", 0);
            fatbar.setProgress(0);
            fatprog = 0;
            fatcurrent.setText("0");

            editor.putInt("carb", 0);
            carbbar.setProgress(0);
            carbprog = 0;
            carbcurrent.setText("0");

            editor.apply();
        });

        // Creating the onClickListener for the calculate button
        calculate.setOnClickListener(v -> {

            // Getting the values from the text fields
            String ageText = age.getText().toString();
            String heightText = height.getText().toString();
            String weightText = weight.getText().toString();

            // Creating the pattern for the regular expression
            // This will check if the value is a number or not
            Pattern pattern = Pattern.compile("[0-9]+");

            // Creating the variables for the checks and setting them to false
            // These will be used to check if the values are empty or not
            boolean ageCheck = false;
            boolean heightCheck = false;
            boolean weightCheck = false;

            // Checking if the age text field is empty or not
            // If it is empty, then it will show an error message
            if (ageText.isEmpty()) {
                age.setError("Please enter your age");
                age.requestFocus();
                ageCheck = false;
            } else if (!pattern.matcher(ageText).matches()) {
                age.setError("Please enter your age correctly");
                age.requestFocus();
                ageCheck = false;
            } else {
                age.setError(null);
                ageCheck = true;
            }

            // Checking if the height text field is empty or not
            // If it is empty, then it will show an error message
            if (heightText.isEmpty()) {
                height.setError("Please enter your height");
                height.requestFocus();
                heightCheck = false;
            } else if (!pattern.matcher(ageText).matches()) {
                age.setError("Please enter your age correctly");
                age.requestFocus();
                heightCheck = false;
            } else {
                height.setError(null);
                heightCheck = true;
            }

            // Checking if the weight text field is empty or not
            // If it is empty, then it will show an error message
            if (weightText.isEmpty()) {
                weight.setError("Please enter your weight");
                weight.requestFocus();
                weightCheck = false;
            } else if (!pattern.matcher(ageText).matches()) {
                age.setError("Please enter your age correctly");
                age.requestFocus();
                weightCheck = false;
            } else {
                weight.setError(null);
                weightCheck = true;
            }

            // Checking if the user has selected the gender or not
            if (gender.getCheckedRadioButtonId() == -1) {
                required.setText("Please Select Gender");
                required.setVisibility(View.VISIBLE);
            } else {
                required.setText("");
                required.setVisibility(View.GONE);

                // Checking if all the values are not empty
                if (ageCheck && heightCheck && weightCheck) {

                    // Calling the calculateBMR method
                    calculateCalorie();

                }
            }


        });

    }

    // Creating the calculate method to calculate the calories required
    public void calculateCalorie() {
        sp = getSharedPreferences("FoodData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // Getting the values from the text fields
        int ageValue = Integer.parseInt(age.getText().toString());
        int heightValue = Integer.parseInt(height.getText().toString());
        int weightValue = Integer.parseInt(weight.getText().toString());




        // Creating the variable for the total calories


        if (gender.getCheckedRadioButtonId() == male.getId()) {
            // If user is "Male" then the following formula will be used to calculate the calories
            totalCalories = (10 * weightValue * 0.45359237) + (6.25 * heightValue * 2.54) - (5 * ageValue) + 5;

            editor.putInt("userHeight", heightValue);
            editor.putInt("userAge", ageValue);
            editor.putInt("userWeight", weightValue);
            editor.putInt("userActivityLevel", Integer.parseInt(activtyEx.getText().toString()));
            editor.putInt("userGender", 1);
            editor.apply();

            // Setting the text to the calories text view

        } else {
            // If user is "Female" then the following formula will be used to calculate the calories
            totalCalories = (10 * weightValue * 0.45359237) + (6.25 * heightValue * 2.54) - (5 * ageValue) - 161;

            editor.putInt("userHeight", heightValue);
            editor.putInt("userAge", ageValue);
            editor.putInt("userWeight", weightValue);
            editor.putInt("userActivityLevel", Integer.parseInt(activtyEx.getText().toString()));
            editor.putInt("userGender", 2);
            editor.apply();
        }



        // Setting the text to the calories in the table layout and rounding it to 2 decimal places
        textView1.setText(String.format("%.2f", totalCalories) + "*");
        textView2.setText(String.format("%.2f", totalCalories * 1.149) + "*");
        textView3.setText(String.format("%.2f", totalCalories * 1.220) + "*");
        textView4.setText(String.format("%.2f", totalCalories * 1.292) + "*");
        textView5.setText(String.format("%.2f", totalCalories * 1.437) + "*");
        textView6.setText(String.format("%.2f", totalCalories * 1.583) + "*");

        int activtylevel = Integer.parseInt(activtyEx.getText().toString());


        if (activtylevel == 1) {
            String string;
            string = String.format("%.2f", totalCalories);
            totalcal.setText("of " + string + " kcal");
            calbar.setMax((int) Math.round(totalCalories));
            totalproteinneeded = (totalCalories * 0.2) / 4;


            proteinbar.setMax((int) Math.round(totalproteinneeded));
            string = String.format("%.2f", totalproteinneeded);
            proteinmax.setText("of " + string);

            totalfatneeded = (totalCalories * 0.3) / 9;
            fatbar.setMax((int) Math.round(totalfatneeded));
            string = String.format("%.2f", totalfatneeded);
            fatmax.setText("of " + string);

            totalcarbsneeded = (totalCalories * 0.55) / 4;
            carbbar.setMax((int) Math.round(totalcarbsneeded));
            string = String.format("%.2f", totalcarbsneeded);
            carbmax.setText("of " + string);

            editor.putInt("totcal", (int) Math.round(totalCalories));
            editor.putInt("totpro", (int) Math.round(totalproteinneeded));
            editor.putInt("totfat", (int) Math.round(totalfatneeded));
            editor.putInt("totcarb", (int) Math.round(totalcarbsneeded));
            editor.apply();


        }
        if (activtylevel == 2) {
            String string;
            string = String.format("%.2f", totalCalories * 1.149);
            totalcal.setText("of " + string + " kcal");
            calbar.setMax((int) Math.round(totalCalories * 1.149));
            totalproteinneeded = (1.149 * totalCalories * 0.2) / 4;
            proteinbar.setMax((int) Math.round(totalproteinneeded));
            string = String.format("%.2f", totalproteinneeded);
            proteinmax.setText("of " + string);
            totalfatneeded = (1.149 * totalCalories * 0.3) / 9;
            fatbar.setMax((int) Math.round(totalfatneeded));
            string = String.format("%.2f", totalfatneeded);
            fatmax.setText("of " + string);
            totalcarbsneeded = (1.149 * totalCalories * 0.55) / 4;
            carbbar.setMax((int) Math.round(totalcarbsneeded));
            string = String.format("%.2f", totalcarbsneeded);
            carbmax.setText("of " + string);

            editor.putInt("totpro", (int) Math.round(totalproteinneeded));
            editor.putInt("totfat", (int) Math.round(totalfatneeded));
            editor.putInt("totcarb", (int) Math.round(totalcarbsneeded));
            editor.putInt("totcal", (int) Math.round(totalCalories * 1.149));
            editor.apply();

        }
        if (activtylevel == 3) {
            String string;
            string = String.format("%.2f", totalCalories * 1.22);
            totalcal.setText("of " + string + " kcal");
            calbar.setMax((int) Math.round(totalCalories * 1.22));
            totalproteinneeded = (1.22 * totalCalories * 0.2) / 4;
            proteinbar.setMax((int) Math.round(totalproteinneeded));
            string = String.format("%.2f", totalproteinneeded);
            proteinmax.setText("of " + string);
            totalfatneeded = (1.22 * totalCalories * 0.3) / 9;
            fatbar.setMax((int) Math.round(totalfatneeded));
            string = String.format("%.2f", totalfatneeded);
            fatmax.setText("of " + string);
            totalcarbsneeded = (1.22 * totalCalories * 0.55) / 4;
            carbbar.setMax((int) Math.round(totalcarbsneeded));
            string = String.format("%.2f", totalcarbsneeded);
            carbmax.setText("of " + string);

            editor.putInt("totpro", (int) Math.round(totalproteinneeded));
            editor.putInt("totfat", (int) Math.round(totalfatneeded));
            editor.putInt("totcarb", (int) Math.round(totalcarbsneeded));
            editor.putInt("totcal", (int) Math.round(totalCalories * 1.22));
            editor.apply();

        }
        if (activtylevel == 4) {
            String string;
            string = String.format("%.2f", totalCalories * 1.292);
            totalcal.setText("of " + string + " kcal");
            calbar.setMax((int) Math.round(totalCalories * 1.292));
            totalproteinneeded = (1.292 * totalCalories * 0.2) / 4;
            proteinbar.setMax((int) Math.round(totalproteinneeded));
            string = String.format("%.2f", totalproteinneeded);
            proteinmax.setText("of " + string);
            totalfatneeded = (1.292 * totalCalories * 0.3) / 9;
            fatbar.setMax((int) Math.round(totalfatneeded));
            string = String.format("%.2f", totalfatneeded);
            fatmax.setText("of " + string);
            totalcarbsneeded = (1.292 * totalCalories * 0.55) / 4;
            carbbar.setMax((int) Math.round(totalcarbsneeded));
            string = String.format("%.2f", totalcarbsneeded);
            carbmax.setText("of " + string);

            editor.putInt("totpro", (int) Math.round(totalproteinneeded));
            editor.putInt("totfat", (int) Math.round(totalfatneeded));
            editor.putInt("totcarb", (int) Math.round(totalcarbsneeded));
            editor.putInt("totcal", (int) Math.round(totalCalories * 1.292));
            editor.apply();

        }
        if (activtylevel == 5) {
            String string;
            string = String.format("%.2f", totalCalories * 1.437);
            totalcal.setText("of " + string + " kcal");
            calbar.setMax((int) Math.round(totalCalories * 1.437));
            totalproteinneeded = (1.437 * totalCalories * 0.2) / 4;
            proteinbar.setMax((int) Math.round(totalproteinneeded));
            string = String.format("%.2f", totalproteinneeded);
            proteinmax.setText("of " + string);
            totalfatneeded = (1.437 * totalCalories * 0.3) / 9;
            fatbar.setMax((int) Math.round(totalfatneeded));
            string = String.format("%.2f", totalfatneeded);
            fatmax.setText("of " + string);
            totalcarbsneeded = (1.437 * totalCalories * 0.55) / 4;
            carbbar.setMax((int) Math.round(totalcarbsneeded));
            string = String.format("%.2f", totalcarbsneeded);
            carbmax.setText("of " + string);

            editor.putInt("totpro", (int) Math.round(totalproteinneeded));
            editor.putInt("totfat", (int) Math.round(totalfatneeded));
            editor.putInt("totcarb", (int) Math.round(totalcarbsneeded));
            editor.putInt("totcal", (int) Math.round(totalCalories * 1.437));
            editor.apply();

        }
        if (activtylevel == 6) {
            String string;
            string = String.format("%.2f", totalCalories * 1.583);
            totalcal.setText("of " + string + " kcal");
            calbar.setMax((int) Math.round(totalCalories * 1.583));
            totalproteinneeded = (1.583 * totalCalories * 0.2) / 4;
            proteinbar.setMax((int) Math.round(totalproteinneeded));
            string = String.format("%.2f", totalproteinneeded);
            proteinmax.setText("of " + string);
            totalfatneeded = (1.583 * totalCalories * 0.3) / 9;
            fatbar.setMax((int) Math.round(totalfatneeded));
            string = String.format("%.2f", totalfatneeded);
            fatmax.setText("of " + string);
            totalcarbsneeded = (1.583 * totalCalories * 0.55) / 4;
            carbbar.setMax((int) Math.round(totalcarbsneeded));
            string = String.format("%.2f", totalcarbsneeded);
            carbmax.setText("of " + string);

            editor.putInt("totpro", (int) Math.round(totalproteinneeded));
            editor.putInt("totfat", (int) Math.round(totalfatneeded));
            editor.putInt("totcarb", (int) Math.round(totalcarbsneeded));
            editor.putInt("totcal", (int) Math.round(totalCalories * 1.583));
            editor.apply();

        }

        // Setting the text to the text view and making it visible
        required.setText("*Calculation is based on the Mifflin-St Jeor Equation");
        required.setTextSize(12);
        required.setVisibility(View.VISIBLE);

    }

}