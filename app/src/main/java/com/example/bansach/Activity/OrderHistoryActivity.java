package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bansach.R;
import com.example.bansach.adapter.OrderHistoryAdapter;
import com.example.bansach.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private OrderHistoryAdapter adapter;
    private List<Order> listOrder;
    private ListView listView;
    int userIdInt;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        SessionManager sessionManager = new SessionManager(this);
        String userIdString = sessionManager.getUserId();
        if (userIdString == null || userIdString.isEmpty()) {
            return;
        }
        userIdInt = Integer.parseInt(userIdString);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("TRANG_THAI")) {
            String trangThaiChon = intent.getStringExtra("TRANG_THAI");
            loadData(trangThaiChon, userIdInt);
        }
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void loadData(String trangThaiChon, int user_id){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders");
        listOrder = new ArrayList<>();
        adapter = new OrderHistoryAdapter(listOrder, OrderHistoryActivity.this);
        listView = findViewById(R.id.listViewHis);
        listView.setAdapter(adapter);
        orderRef.orderByChild("userId").equalTo(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listOrder.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Order order = data.getValue(Order.class);
                            if (order != null && order.getStatus().equals(trangThaiChon)) {
                                listOrder.add(order);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Lỗi kéo db: " + error.getMessage());
                    }
                });
    }

}