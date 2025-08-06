package com.example.teachererp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.teachererp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView teacherName, teacherPhone, teacherEmail, age, dob, gender,
            highestQualification, organization, specialization,
            permanentAddress, city, state, country, customUID, logoutButton;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        profileImage = findViewById(R.id.profileImage);
        teacherName = findViewById(R.id.teacherName);
        teacherPhone = findViewById(R.id.teacherPhone);
        teacherEmail = findViewById(R.id.teacherEmail);
        age = findViewById(R.id.age);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        highestQualification = findViewById(R.id.highestQualification);
        organization = findViewById(R.id.organization);
        specialization = findViewById(R.id.specialization);
        permanentAddress = findViewById(R.id.permanentAddress);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        logoutButton = findViewById(R.id.logoutButton);

        if (user != null) {
            String uid = user.getUid();

            db.collection("teacherRegistration").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String name = doc.getString("teacherName");

                            teacherName.setText("Name: " + name);
                            teacherPhone.setText("Contact No.: " + doc.getString("teacherPhone"));
                            teacherEmail.setText("Email Id: " + doc.get("teacherEmail"));
                            age.setText("Age: " + doc.get("age"));
                            dob.setText("DOB: " + doc.getString("dob"));
                            gender.setText("Gender: " + doc.getString("gender"));
                            highestQualification.setText("Highest Qualification: " + doc.getString("highestQualification"));
                            organization.setText("Organization: " + doc.get("organization"));
                            specialization.setText("Specialization: " + doc.getString("specialization"));
                            permanentAddress.setText("Address: " + doc.getString("permanentAddress"));
                            city.setText("City: " + doc.getString("city"));
                            state.setText("State: " + doc.getString("state"));
                            country.setText("Country: " + doc.getString("country"));

                            // Load profile image
                            String imageUrl = doc.getString("profileImageUrl");

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this).load(imageUrl).into(profileImage);
                            } else if (name != null && !name.isEmpty()) {
                                String avatarUrl = "https://ui-avatars.com/api/?name=" + name.replace(" ", "+")
                                        + "&background=3986E6&color=FFFFFF&rounded=true&size=128";
                                Glide.with(this).load(avatarUrl).into(profileImage);
                            } else {
                                profileImage.setImageResource(R.drawable.ic_profile_placeholder);
                            }
                        } else {
                            Toast.makeText(this, "Teacher data not found", Toast.LENGTH_SHORT).show();
                        }
                    });

            logoutButton.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }
}