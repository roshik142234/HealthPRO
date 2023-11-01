package com.example.healthproapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<UserInfo> userInfoList;

    public MyAdapter(Context context, List<UserInfo> userInfoList) {
        this.context = context;
        this.userInfoList = userInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserInfo userInfo = userInfoList.get(position);
        holder.bind(userInfo);
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView ageTextView;
        private TextView genderTextView;
        private TextView calorieProgressTextView;
        private TextView proteinProgressTextView;
        private TextView fatProgressTextView;
        private TextView carbProgressTextView;
        private TextView caloriesBurnedTextView;
        private TextView lastmealTextView;
        private TextView distanceTextView;
        private TextView stepsTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ageTextView = itemView.findViewById(R.id.age);
            genderTextView = itemView.findViewById(R.id.genderTextView);
            calorieProgressTextView = itemView.findViewById(R.id.calorieProgress);
            proteinProgressTextView = itemView.findViewById(R.id.proteinProgress);
            fatProgressTextView = itemView.findViewById(R.id.fatProgress);
            caloriesBurnedTextView = itemView.findViewById(R.id.caloriesBurned);
            carbProgressTextView = itemView.findViewById(R.id.carbProgress);
            lastmealTextView = itemView.findViewById(R.id.lastmeal);
            distanceTextView = itemView.findViewById(R.id.distance);
            stepsTextView = itemView.findViewById(R.id.steps);
        }

        public void bind(UserInfo userInfo) {
            nameTextView.setText(userInfo.getName());
            ageTextView.setText(String.valueOf(userInfo.getAge()));
            genderTextView.setText(String.valueOf(userInfo.getGender()));
            calorieProgressTextView.setText(String.valueOf(userInfo.getCalorieProgress()));
            carbProgressTextView.setText(String.valueOf(userInfo.getCarbProgress()));
            proteinProgressTextView.setText(String.valueOf(userInfo.getProteinProgress()));
            fatProgressTextView.setText(String.valueOf(userInfo.getFatProgress()));
            caloriesBurnedTextView.setText(userInfo.getCaloriesBurned());
            lastmealTextView.setText(userInfo.getlastmeal());
            distanceTextView.setText(userInfo.getDistance());
            stepsTextView.setText(String.valueOf(userInfo.getSteps()));
        }
    }
}
