package com.example.teachererp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachererp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoubtsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Spinner spinnerStudentFilter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String teacherUID;
    private DoubtAdapter adapter;
    private final List<Doubt> doubtList = new ArrayList<>();
    private final List<String> studentNames = new ArrayList<>();
    private final List<String> studentUIDs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubts);

        recyclerView = findViewById(R.id.recyclerDoubts);
        spinnerStudentFilter = findViewById(R.id.spinnerStudentFilter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        teacherUID = auth.getCurrentUser().getUid();

        adapter = new DoubtAdapter(doubtList);
        recyclerView.setAdapter(adapter);

        setupStudentFilter();
    }

    private void setupStudentFilter() {
        db.collection("Doubts")
                .whereEqualTo("teacherId", teacherUID)
                .get()
                .addOnSuccessListener(snapshot -> {
                    studentNames.clear();
                    studentUIDs.clear();
                    studentNames.add("All Students");
                    studentUIDs.add("ALL");

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String name = doc.getString("askedBy");
                        String id = doc.getString("askedById");
                        if (name != null && id != null && !studentUIDs.contains(id)) {
                            studentNames.add(name);
                            studentUIDs.add(id);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            R.layout.spinner_item,           // For selected item view
                            studentNames);

                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // For dropdown
                    spinnerStudentFilter.setAdapter(adapter);

                    spinnerStudentFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedUID = studentUIDs.get(position);
                            if (selectedUID.equals("ALL")) {
                                loadDoubts(null);
                            } else {
                                loadDoubts(selectedUID);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                });
    }

    private void loadDoubts(@Nullable String studentUID) {
        db.collection("Doubts")
                .whereEqualTo("teacherId", teacherUID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    doubtList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String askedById = doc.getString("askedById");
                        if (studentUID == null || studentUID.equals(askedById)) {
                            Doubt doubt = new Doubt(
                                    doc.getId(),
                                    doc.getString("question"),
                                    doc.getString("answer"),
                                    doc.getString("askedBy")
                            );
                            doubtList.add(doubt);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load doubts", Toast.LENGTH_SHORT).show());
    }

    static class Doubt {
        String id, question, answer, askedBy;
        Doubt(String id, String question, String answer, String askedBy) {
            this.id = id;
            this.question = question;
            this.answer = answer;
            this.askedBy = askedBy;
        }
    }

    class DoubtAdapter extends RecyclerView.Adapter<DoubtAdapter.DoubtViewHolder> {

        private final List<Doubt> list;
        DoubtAdapter(List<Doubt> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public DoubtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doubt, parent, false);
            return new DoubtViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DoubtViewHolder holder, int position) {
            Doubt doubt = list.get(position);
            holder.textQuestion.setText("Q: " + doubt.question);
            holder.textAskedBy.setText("Asked by: " + doubt.askedBy);
            holder.editAnswer.setText(doubt.answer);

            holder.btnReply.setOnClickListener(v -> {
                String reply = holder.editAnswer.getText().toString().trim();
                if (reply.isEmpty()) {
                    Toast.makeText(v.getContext(), "Answer cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> update = new HashMap<>();
                update.put("answer", reply);
                db.collection("Doubts").document(doubt.id).update(update)
                        .addOnSuccessListener(unused -> Toast.makeText(v.getContext(), "Answer submitted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class DoubtViewHolder extends RecyclerView.ViewHolder {
            TextView textQuestion, textAskedBy;
            EditText editAnswer;
            Button btnReply;

            DoubtViewHolder(@NonNull View itemView) {
                super(itemView);
                textQuestion = itemView.findViewById(R.id.textQuestion);
                textAskedBy = itemView.findViewById(R.id.textAskedBy);
                editAnswer = itemView.findViewById(R.id.editAnswer);
                btnReply = itemView.findViewById(R.id.btnReply);
            }
        }
    }
}
