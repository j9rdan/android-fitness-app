package com.example.fitnessapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.MyViewHolder> {

    private String list[];
    private Context ctx;

    public ExerciseListAdapter(String[] list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ExerciseListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx.getApplicationContext()).inflate(R.layout.workout_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseListAdapter.MyViewHolder holder, int position) {
        holder.exercise.setText(list[position]);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView exercise;
        public MyViewHolder(View itemView) {
            super(itemView);
            exercise = itemView.findViewById(R.id.exercise);
        }
    }
}
