package com.example.teachererp.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teachererp.R;
import com.example.teachererp.model.ClassScheduleModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AddClassScheduleActivity extends AppCompatActivity {

    private Spinner spinnerDay;
    private EditText editSubject, editTime;
    private TextView btnAdd;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String teacherUID;
    private String teacherName = null; // Null by default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class_schedule);

        spinnerDay = findViewById(R.id.spinnerDay);
        editSubject = findViewById(R.id.editSubject);
        editTime = findViewById(R.id.editTime);
        btnAdd = findViewById(R.id.btnAddSchedule);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        teacherUID = auth.getCurrentUser().getUid();

        // Fetch teacher name from Firestore
        db.collection("teacherRegistration").document(teacherUID)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        teacherName = doc.getString("teacherName"); // Case-sensitive
                        Log.d("TeacherName", "Fetched: " + teacherName);
                    } else {
                        Log.e("TeacherName", "No document found for UID: " + teacherUID);
                        Toast.makeText(this, "Teacher not registered", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TeacherName", "Fetch error: " + e.getMessage());
                    Toast.makeText(this, "Error loading teacher info", Toast.LENGTH_SHORT).show();
                });


        // Populate spinner
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,           // For selected item view
                days);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // For dropdown
        spinnerDay.setAdapter(adapter);

        // Time picker
        editTime.setOnClickListener(v -> showTimePicker());

        btnAdd.setOnClickListener(v -> {
            String day = spinnerDay.getSelectedItem().toString();
            String subject = editSubject.getText().toString().trim();
            String timeSlot = editTime.getText().toString().trim();

            if (teacherName == null) {
                Toast.makeText(this, "Wait! Fetching your name...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (subject.isEmpty() || timeSlot.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ClassScheduleModel schedule = new ClassScheduleModel(day, timeSlot, subject, teacherName);

            db.collection("AllSchedules")
                    .add(schedule)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Schedule added successfully", Toast.LENGTH_SHORT).show();
                        editSubject.setText("");
                        editTime.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    // Time picker for class start/end
    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog startPicker = new TimePickerDialog(this,
                (TimePicker view, int hourOfDay, int minute1) -> {
                    String startTime = formatTime(hourOfDay, minute1);

                    // Second picker for end time
                    new TimePickerDialog(this,
                            (TimePicker view2, int endHour, int endMinute) -> {
                                String endTime = formatTime(endHour, endMinute);
                                editTime.setText(startTime + " - " + endTime);
                            }, hour, minute, false).show();

                }, hour, minute, false);
        startPicker.show();
    }

    private String formatTime(int hour, int minute) {
        String amPm = (hour >= 12) ? "PM" : "AM";
        hour = (hour > 12) ? hour - 12 : hour;
        if (hour == 0) hour = 12;
        return String.format("%02d:%02d %s", hour, minute, amPm);
    }
}
