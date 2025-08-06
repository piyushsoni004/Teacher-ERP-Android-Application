package com.example.teachererp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.teachererp.R;
import com.example.teachererp.adapter.NoticeAdapter;
import com.example.teachererp.model.NoticeModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NoticeBoardActivity extends AppCompatActivity {

    private FloatingActionButton add_notice_button;
    private RecyclerView recyclerView;
    private TextView textNoNotices;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<NoticeModel> noticeList = new ArrayList<>();
    private NoticeAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notice_board);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerNotices);
        textNoNotices = findViewById(R.id.textNoNotices);
        add_notice_button = findViewById(R.id.add_notice_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoticeAdapter(this, noticeList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        add_notice_button.setOnClickListener(v ->
                startActivity(new Intent(NoticeBoardActivity.this, AddNoticeActivity.class))
        );

        swipeRefreshLayout.setOnRefreshListener(this::loadNotices);

        loadNotices();

        // ✅ Swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                NoticeModel notice = noticeList.get(position);

                db.collection("Notices")
                        .whereEqualTo("title", notice.getTitle())
                        .whereEqualTo("description", notice.getDescription())
                        .whereEqualTo("date", notice.getDate())
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            if (!snapshot.isEmpty()) {
                                snapshot.getDocuments().get(0).getReference().delete();
                                noticeList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(NoticeBoardActivity.this, "Notice deleted", Toast.LENGTH_SHORT).show();

                                // check if list became empty
                                if (noticeList.isEmpty()) {
                                    textNoNotices.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(NoticeBoardActivity.this, "Notice not found", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadNotices() {
        swipeRefreshLayout.setRefreshing(true);

        db.collection("Notices")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    noticeList.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        try {
                            NoticeModel notice = doc.toObject(NoticeModel.class);
                            if (notice != null) {
                                noticeList.add(notice);
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // Skip bad documents
                        }
                    }

                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                    textNoNotices.setVisibility(noticeList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, "Failed to load notices", Toast.LENGTH_SHORT).show();
                });
    }
}