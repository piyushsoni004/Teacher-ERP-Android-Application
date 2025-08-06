package com.example.teachererp.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teachererp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddHolidayActivity extends AppCompatActivity {

    private EditText editDate, editDay, editReason;
    private Button btnAdd;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String teacherName = "Unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_holiday);

        editDate = findViewById(R.id.editDate);
        editDay = findViewById(R.id.editDay);
        editReason = findViewById(R.id.editReason);
        btnAdd = findViewById(R.id.btnAddHoliday);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String uid = auth.getCurrentUser().getUid();
        db.collection("Teachers").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        teacherName = doc.getString("name");
                    }
                });

        editDate.setFocusable(false);
        editDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth) -> {
                        String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        editDate.setText(dateStr);

                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date date = sdf.parse(dateStr);
                            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                            String dayName = dayFormat.format(date);
                            editDay.setText(dayName);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnAdd.setOnClickListener(v -> {
            String date = editDate.getText().toString().trim();
            String day = editDay.getText().toString().trim();
            String reason = editReason.getText().toString().trim();

            if (date.isEmpty() || day.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> holiday = new HashMap<>();
            holiday.put("date", date);
            holiday.put("day", day);
            holiday.put("reason", reason);
            holiday.put("addedBy", teacherName);

            db.collection("Holidays")
                    .add(holiday)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Holiday Added", Toast.LENGTH_SHORT).show();
                        editDate.setText("");
                        editDay.setText("");
                        editReason.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
