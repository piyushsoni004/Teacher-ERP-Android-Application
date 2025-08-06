package com.example.teachererp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // ✅ Import added

import com.example.teachererp.R;
import com.example.teachererp.adapter.AttendanceAdapter;
import com.example.teachererp.model.StudentAttendanceModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView submitButton;
    private SwipeRefreshLayout swipeRefreshLayout; // ✅ New field added

    private AttendanceAdapter adapter;
    private List<StudentAttendanceModel> studentList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // 🔗 UI references
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // ✅ SwipeRefreshLayout
        recyclerView = findViewById(R.id.recyclerViewAttendance);
        progressBar = findViewById(R.id.progressBarAttendance);
        submitButton = findViewById(R.id.submitAttendanceButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();
        adapter = new AttendanceAdapter(studentList, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // 🔄 Swipe refresh action
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadStudentsFromFirestore();
        });

        loadStudentsFromFirestore();

        submitButton.setOnClickListener(v -> submitAttendanceToFirestore());
    }

    private void loadStudentsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true); // ✅ Show swipe loading
        studentList.clear();

        db.collection("studentAdmission")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No students found!", Toast.LENGTH_SHORT).show();
                    }

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            String name = doc.getString("studentName");
                            Object classObj = doc.get("studentClass");
                            String studentClass = (classObj != null) ? String.valueOf(classObj) : null;
                            String uid = doc.getId();

                            if (name != null && studentClass != null && uid != null) {
                                StudentAttendanceModel student = new StudentAttendanceModel();
                                student.setUid(uid);
                                student.setStudentName(name);
                                student.setStudentClass(studentClass);
                                student.setPresent(false);
                                studentList.add(student);
                            }
                        } catch (Exception e) {
                            Log.e("ATTENDANCE", "Error parsing document: " + e.getMessage());
                        }
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false); // ✅ Stop refresh
                    Toast.makeText(this, "Students loaded: " + studentList.size(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ATTENDANCE", "Firestore failure: ", e);
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false); // ✅ Stop refresh
                });
    }

    private void submitAttendanceToFirestore() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (StudentAttendanceModel student : studentList) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", student.getStudentName());
            data.put("class", student.getStudentClass());
            data.put("present", student.isPresent());
            data.put("timestamp", System.currentTimeMillis());

            db.collection("attendance")
                    .document(student.getUid())
                    .collection("records")
                    .document(date)
                    .set(data)
                    .addOnSuccessListener(unused ->
                            Log.d("ATTENDANCE", "Saved for " + student.getStudentName()))
                    .addOnFailureListener(e ->
                            Log.e("ATTENDANCE", "Failed to save for " + student.getStudentName() + ": " + e.getMessage()));
        }

        Toast.makeText(this, "Attendance submitted for " + date, Toast.LENGTH_SHORT).show();
    }
}