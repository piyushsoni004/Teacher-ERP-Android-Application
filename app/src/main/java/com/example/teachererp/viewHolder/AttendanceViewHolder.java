package com.example.teachererp.viewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachererp.R;

public class AttendanceViewHolder extends RecyclerView.ViewHolder {
    public TextView studentName;
    public TextView studentClass;
    public CheckBox presentCheckBox;

    public AttendanceViewHolder(@NonNull View itemView) {
        super(itemView);
        studentName = itemView.findViewById(R.id.nameTextView);
        studentClass = itemView.findViewById(R.id.classTextView);
        presentCheckBox = itemView.findViewById(R.id.presentCheckBox);
    }
}
