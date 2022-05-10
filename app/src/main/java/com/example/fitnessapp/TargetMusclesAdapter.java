package com.example.fitnessapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TargetMusclesAdapter extends RecyclerView.Adapter<TargetMusclesAdapter.ViewHolder> {

    private List<String> data;
    public List<String> selectedValues;
    private Context context;
    private RecyclerView recyclerView;

    // create view holder to hold checkboxes
    public class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        CheckBox checkBox;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            checkBox = v.findViewById(R.id.checkBox);
            selectedValues = new ArrayList<>();
        }
    }

    // main constructor
    public TargetMusclesAdapter(List<String> data, Context context, RecyclerView recyclerView) {
        this.data = data;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    // populate view with checkbox rows from res/layout/checkbox_rows.xml
    public TargetMusclesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.checkbox_rows, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final TargetMusclesAdapter.ViewHolder holder, final int position){
        final String text = data.get(position);
        holder.checkBox.setText(text);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()){
                    selectedValues.add(text);
                } else {
                    selectedValues.remove(text);
                }
            }
        });
    }

    public int getItemCount() { return data.size(); }

    public List<String> getSelectedValues() { return selectedValues; }
}