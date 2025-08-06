package com.example.teachererp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.teachererp.R;
import com.example.teachererp.adapter.HolidayAdapter;
import com.example.teachererp.model.HolidayModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HolidayActivity extends AppCompatActivity {

    private FloatingActionButton addHolidayBtn;
    private Spinner spinnerMonth;
    private RecyclerView recyclerView;
    private TextView textNoHolidays;
    private SwipeRefreshLayout swipeRefreshLayout;

    private HolidayAdapter adapter;
    private List<HolidayModel> allHolidays = new ArrayList<>();
    private List<HolidayModel> filteredHolidays = new ArrayList<>();
    private FirebaseFirestore db;

    private final String[] months = {
            "All", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_holiday);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI Initialization
        addHolidayBtn = findViewById(R.id.add_holiday);
        spinnerMonth = findViewById(R.id.spinnerMonthFilter);
        recyclerView = findViewById(R.id.recyclerHolidays);
        textNoHolidays = findViewById(R.id.textNoHolidays);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        db = FirebaseFirestore.getInstance();

        // Adapter
        adapter = new HolidayAdapter(filteredHolidays);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Spinner setup
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                months
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMonth.setAdapter(spinnerAdapter);

        // Spinner filtering
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filterByMonth(pos); // 0 = All, 1 = Jan, ..., 12 = Dec
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // FAB
        addHolidayBtn.setOnClickListener(v -> {
            startActivity(new Intent(HolidayActivity.this, AddHolidayActivity.class));
        });

        // Swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::fetchAllHolidays);

        // Initial load
        fetchAllHolidays();
    }

    private void fetchAllHolidays() {
        swipeRefreshLayout.setRefreshing(true);

        db.collection("Holidays")
                .get()
                .addOnSuccessListener(snapshot -> {
                    allHolidays.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        HolidayModel h = doc.toObject(HolidayModel.class);
                        if (h != null) {
                            allHolidays.add(h);
                        }
                    }
                    filterByMonth(spinnerMonth.getSelectedItemPosition());
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> swipeRefreshLayout.setRefreshing(false));
    }

    private void filterByMonth(int monthIndex) {
        filteredHolidays.clear();

        if (monthIndex == 0) {
            filteredHolidays.addAll(allHolidays);
        } else {
            for (HolidayModel h : allHolidays) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf.parse(h.getDate());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int month = calendar.get(Calendar.MONTH) + 1;

                    if (month == monthIndex) {
                        filteredHolidays.add(h);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        adapter.notifyDataSetChanged();
        textNoHolidays.setVisibility(filteredHolidays.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
