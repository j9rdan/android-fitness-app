package com.example.fitnessapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder> {

    private String list[];
    private Context ctx;

    public ExerciseListAdapter(String[] list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx.getApplicationContext()).inflate(R.layout.workout_list_row, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.exercise.setText(list[position]);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exercise;
        public ExerciseViewHolder(View itemView) {
            super(itemView);
            exercise = itemView.findViewById(R.id.exercise);
        }
    }
}
