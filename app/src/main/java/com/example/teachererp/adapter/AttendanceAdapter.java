package com.example.teachererp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachererp.R;
import com.example.teachererp.model.StudentAttendanceModel;
import com.example.teachererp.viewHolder.AttendanceViewHolder;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceViewHolder> {

    private List<StudentAttendanceModel> studentList;
    private Context context;

    public AttendanceAdapter(List<StudentAttendanceModel> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        StudentAttendanceModel student = studentList.get(position);

        holder.studentName.setText(student.getStudentName());
        holder.studentClass.setText("Class: " + student.getStudentClass());

        holder.presentCheckBox.setOnCheckedChangeListener(null);
        holder.presentCheckBox.setChecked(student.isPresent());

        holder.presentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            student.setPresent(isChecked); // Update attendance state
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public List<StudentAttendanceModel> getStudentList() {
        return studentList;
    }
}
