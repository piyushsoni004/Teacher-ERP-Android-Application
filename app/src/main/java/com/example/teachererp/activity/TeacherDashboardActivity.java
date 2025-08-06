package com.example.teachererp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.teachererp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherDashboardActivity extends AppCompatActivity {
    private ConstraintLayout profile, doubts_constraint, holidays_constraint,
            notes_constraint, result_constraint, attendance_constraint, view_class_constraint;
    private TextView welcomeName;
    private ImageView profileImageView; // ⬅️ Avatar ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcomeName = findViewById(R.id.welcomeName);
        profileImageView = findViewById(R.id.profileImageView); // ⬅️ Bind avatar view

        profile = findViewById(R.id.profile);
        doubts_constraint = findViewById(R.id.doubts_constraint);
        holidays_constraint = findViewById(R.id.holidays_constraint);
        notes_constraint = findViewById(R.id.notice_board_constraint);
        result_constraint = findViewById(R.id.result_constraint);
        attendance_constraint = findViewById(R.id.attendance_constraint);
        view_class_constraint = findViewById(R.id.view_class_constraint);

        profile.setOnClickListener(view ->
                startActivity(new Intent(TeacherDashboardActivity.this, ProfileActivity.class)));

        doubts_constraint.setOnClickListener(view ->
                startActivity(new Intent(TeacherDashboardActivity.this, DoubtsActivity.class)));

        holidays_constraint.setOnClickListener(view ->
                startActivity(new Intent(TeacherDashboardActivity.this, HolidayActivity.class)));

        notes_constraint.setOnClickListener(view ->
                startActivity(new Intent(TeacherDashboardActivity.this, NoticeBoardActivity.class)));

        result_constraint.setOnClickListener(view ->
                startActivity(new Intent(TeacherDashboardActivity.this, ResultActivity.class)));

        attendance_constraint.setOnClickListener(view ->
                startActivity(new Intent(TeacherDashboardActivity.this, AttendanceActivity.class)));

        view_class_constraint.setOnClickListener(view ->
                startActivity(new Intent(TeacherDashboardActivity.this, ViewClassScheduleActivity.class)));

        // Load teacher data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            db.collection("teacherRegistration").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("teacherName");
                            String specialization = documentSnapshot.getString("specialization");
                            String customUID = documentSnapshot.getString("customUID");
                            String imageUrl = documentSnapshot.getString("profileImageUrl");

                            welcomeName.setText("Welcome, " + name + "\nSpecialization: " + specialization + "\nUID: " + customUID);

                            // Load avatar
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this).load(imageUrl).into(profileImageView);
                            } else if (name != null && !name.isEmpty()) {
                                String avatarUrl = "https://ui-avatars.com/api/?name=" + name.replace(" ", "+")
                                        + "&background=3986E6&color=FFFFFF&rounded=true&size=128";
                                Glide.with(this).load(avatarUrl).into(profileImageView);
                            } else {
                                profileImageView.setImageResource(R.drawable.ic_profile_placeholder);
                            }

                        } else {
                            welcomeName.setText("Teacher data not found");
                        }
                    })
                    .addOnFailureListener(e -> welcomeName.setText("Error: " + e.getMessage()));
        } else {
            welcomeName.setText("Not logged in");
        }
    }
}