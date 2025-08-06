package com.example.teachererp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachererp.R;
import com.example.teachererp.model.ClassScheduleModel;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<ClassScheduleModel> scheduleList;

    public ScheduleAdapter(List<ClassScheduleModel> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ClassScheduleModel model = scheduleList.get(position);
        holder.txtSubject.setText("Subject: " + model.getSubjectName());
        holder.txtTime.setText("Time: " + model.getTimeSlot());
        holder.txtDay.setText("Day: " + model.getDay());
        holder.txtTeacher.setText("Teacher: " + model.getTeacherName());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView txtSubject, txtTime, txtDay, txtTeacher;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtTeacher = itemView.findViewById(R.id.txtTeacher);
        }
    }
}
