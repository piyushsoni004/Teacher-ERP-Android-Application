package com.example.teachererp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teachererp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText teacherNameInput, personalPhoneInput, personalEmailInput, dobInput, ageInput,
            highestQualificationInput, organizationInput, specializationInput, experiencesInput,
            permanentAddressInput, landmarkInput, cityInput, stateInput;
    private RadioGroup genderInput, countryInput;
    private TextView registerButton;

    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        teacherNameInput = findViewById(R.id.teacherNameInput);
        personalPhoneInput = findViewById(R.id.personalPhoneInput);
        personalEmailInput = findViewById(R.id.personalEmailInput);
        dobInput = findViewById(R.id.dobInput);
        genderInput = findViewById(R.id.genderInput);
        ageInput = findViewById(R.id.ageInput);
        highestQualificationInput = findViewById(R.id.highestQualificationInput);
        organizationInput = findViewById(R.id.organizationInput);
        specializationInput = findViewById(R.id.specializationInput);
        experiencesInput = findViewById(R.id.experiencesInput);
        permanentAddressInput = findViewById(R.id.permanentAddressInput);
        landmarkInput = findViewById(R.id.landmarkInput);
        cityInput = findViewById(R.id.cityInput);
        stateInput = findViewById(R.id.stateInput);
        countryInput = findViewById(R.id.countryInput);
        registerButton = findViewById(R.id.registerButton);

        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teacherName = teacherNameInput.getText().toString().trim();
                String personalPhoneStr = personalPhoneInput.getText().toString().trim();
                String personalEmail = personalEmailInput.getText().toString().trim();
                String dob = dobInput.getText().toString().trim();
                String ageStr = ageInput.getText().toString().trim();
                String highestQualification = highestQualificationInput.getText().toString().trim();
                String organization = organizationInput.getText().toString().trim();
                String specialization = specializationInput.getText().toString().trim();
                String experiencesStr = experiencesInput.getText().toString().trim();
                String permanentAddress = permanentAddressInput.getText().toString().trim();
                String landmark = landmarkInput.getText().toString().trim();
                String city = cityInput.getText().toString().trim();
                String state = stateInput.getText().toString().trim();

                int genderId = genderInput.getCheckedRadioButtonId();
                int countryId = countryInput.getCheckedRadioButtonId();

                if (teacherName.isEmpty() || personalPhoneStr.isEmpty() || personalEmail.isEmpty() || dob.isEmpty()
                        || ageStr.isEmpty() || highestQualification.isEmpty() || organization.isEmpty()
                        || specialization.isEmpty() || experiencesStr.isEmpty() || permanentAddress.isEmpty()
                        || city.isEmpty() || state.isEmpty() || genderId == -1 || countryId == -1) {

                    Toast.makeText(RegistrationActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                    return;
                }

                int personalPhone = Integer.parseInt(personalPhoneStr);
                int age = Integer.parseInt(ageStr);
                long experiences = Long.parseLong(experiencesStr);

                RadioButton genderRadio = findViewById(genderId);
                RadioButton countryRadio = findViewById(countryId);
                String gender = genderRadio.getText().toString();
                String country = countryRadio.getText().toString();

                Map<String, Object> teacherRegistration = new HashMap<>();
                teacherRegistration.put("teacherName", teacherName);
                teacherRegistration.put("personalPhoneStr", personalPhone);
                teacherRegistration.put("personalEmail", personalEmail);
                teacherRegistration.put("dob", dob);
                teacherRegistration.put("gender", gender);
                teacherRegistration.put("age", age);
                teacherRegistration.put("highestQualification", highestQualification);
                teacherRegistration.put("organization", organization);
                teacherRegistration.put("specialization", specialization);
                teacherRegistration.put("experiences", experiences);
                teacherRegistration.put("permanentAddress", permanentAddress);
                teacherRegistration.put("landmark", landmark);
                teacherRegistration.put("city", city);
                teacherRegistration.put("state", state);
                teacherRegistration.put("country", country);

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String customUID = "TEACH_" + personalPhone;
                teacherRegistration.put("customUID", customUID);

                db.collection("teacherRegistration")
                        .document(uid)  // 🔥 save with Firebase UID
                        .set(teacherRegistration)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegistrationActivity.this, "Saved with ID: " + customUID, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegistrationActivity.this, TeacherDashboardActivity.class);
                            intent.putExtra("customUID", customUID); // Pass UID for later use
                            startActivity(intent);
                        });
            }
        });
    }
}