package com.example.teachererp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.teachererp.R;
import com.example.teachererp.adapter.ScheduleAdapter;
import com.example.teachererp.model.ClassScheduleModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewClassScheduleActivity extends AppCompatActivity {

    private FloatingActionButton add_classes_button;
    private RecyclerView recyclerView;
    private Spinner daySpinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<ClassScheduleModel> fullScheduleList;
    private List<ClassScheduleModel> filteredList;
    private ScheduleAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class_schedule);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerViewSchedule);
        daySpinner = findViewById(R.id.daySpinner);
        add_classes_button = findViewById(R.id.add_classes_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        fullScheduleList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new ScheduleAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                getResources().getStringArray(R.array.days_array)
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        daySpinner.setAdapter(spinnerAdapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDay = parent.getItemAtPosition(position).toString();
                filterScheduleByDay(selectedDay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        add_classes_button.setOnClickListener(v -> {
            Intent intent = new Intent(ViewClassScheduleActivity.this, AddClassScheduleActivity.class);
            startActivity(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> loadAllSchedules());

        loadAllSchedules();
    }

    private void loadAllSchedules() {
        swipeRefreshLayout.setRefreshing(true);

        db.collection("AllSchedules")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fullScheduleList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ClassScheduleModel schedule = doc.toObject(ClassScheduleModel.class);
                        if (schedule != null) {
                            fullScheduleList.add(schedule);
                        }
                    }
                    filterScheduleByDay(daySpinner.getSelectedItem().toString());
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load schedules", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void filterScheduleByDay(String selectedDay) {
        filteredList.clear();

        if (selectedDay.equals("All Days")) {
            filteredList.addAll(fullScheduleList);
        } else {
            for (ClassScheduleModel model : fullScheduleList) {
                if (model.getDay().equalsIgnoreCase(selectedDay)) {
                    filteredList.add(model);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}
