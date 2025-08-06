package com.example.teachererp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teachererp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoticeActivity extends AppCompatActivity {

    private EditText editTitle, editDescription;
    private Button btnPostNotice;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice); // make sure this matches your XML filename

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        btnPostNotice = findViewById(R.id.btnPostNotice);

        firestore = FirebaseFirestore.getInstance();

        btnPostNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNotice();
            }
        });
    }

    private void postNotice() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            return;
        }

        if (description.isEmpty()) {
            editDescription.setError("Description is required");
            return;
        }

        Map<String, Object> notice = new HashMap<>();
        notice.put("title", title);
        notice.put("description", description);
        notice.put("date", System.currentTimeMillis());

        firestore.collection("Notices")
                .add(notice)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddNoticeActivity.this, "Notice posted successfully", Toast.LENGTH_SHORT).show();
                    editTitle.setText("");
                    editDescription.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddNoticeActivity.this, "Failed to post notice: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}