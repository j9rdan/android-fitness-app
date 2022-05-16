package com.example.fitnessapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.RatingViewHolder> {

    private String list[];
    private Context ctx;

    public RatingListAdapter(String[] list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx.getApplicationContext()).inflate(R.layout.rating_list_row, parent, false);
        return new RatingListAdapter.RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        holder.rating.setHint(list[position]);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class RatingViewHolder extends RecyclerView.ViewHolder {
        EditText rating;
        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            rating = itemView.findViewById(R.id.rating);
        }
    }

}
