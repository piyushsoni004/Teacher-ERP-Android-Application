package com.example.teachererp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachererp.R;
import com.example.teachererp.model.HolidayModel;

import java.util.List;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    private final List<HolidayModel> list;

    public HolidayAdapter(List<HolidayModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_holiday, parent, false);
        return new HolidayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        HolidayModel h = list.get(position);
        holder.textDate.setText(h.getDate() + " (" + h.getDay() + ")");
        holder.textReason.setText("Reason: " + h.getReason());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textReason;
        HolidayViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textReason = itemView.findViewById(R.id.textReason);
        }
    }
}
