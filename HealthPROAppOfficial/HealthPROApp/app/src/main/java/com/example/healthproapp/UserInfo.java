package com.example.healthproapp;

public class UserInfo {
    private String name; // Add this attribute for the name
    private int age;
    private int gender;
    String lastmeal;
    private int calorieProgress;
    private String caloriesBurned;
    private int carbProgress;
    private String distance;
    private int fatProgress;
    private int proteinProgress;
    private int steps;

    public UserInfo() {
        // Default constructor required for Firebase database
    }

    public UserInfo(String name, int age, int gender, String lastmeal, int calorieProgress,
                    String caloriesBurned, int carbProgress, String distance,
                    int fatProgress, int proteinProgress, int steps) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.lastmeal = lastmeal;
        this.calorieProgress = calorieProgress;
        this.caloriesBurned = caloriesBurned;
        this.carbProgress = carbProgress;
        this.distance = distance;
        this.fatProgress = fatProgress;
        this.proteinProgress = proteinProgress;
        this.steps = steps;
    }

    // Getter and setter methods for each attribute

    // Setter for the name attribute
    public void setName(String name) {
        this.name = name;
    }

    // Other getters and setters

    // Getter for the name attribute
    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getlastmeal() {
        return lastmeal;
    }

    public void setlastmeal(String lastmeal) {
        this.lastmeal = lastmeal;
    }

    public int getCalorieProgress() {
        return calorieProgress;
    }

    public void setCalorieProgress(int calorieProgress) {
        this.calorieProgress = calorieProgress;
    }

    public String getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(String caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getCarbProgress() {
        return carbProgress;
    }

    public void setCarbProgress(int carbProgress) {
        this.carbProgress = carbProgress;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getFatProgress() {
        return fatProgress;
    }

    public void setFatProgress(int fatProgress) {
        this.fatProgress = fatProgress;
    }

    public int getProteinProgress() {
        return proteinProgress;
    }

    public void setProteinProgress(int proteinProgress) {
        this.proteinProgress = proteinProgress;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
