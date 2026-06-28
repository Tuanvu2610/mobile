package com.example.bansach.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
    private ImageButton btnBack;
    private TextView tatca, choxuly, danggiao, dagiao, dahuy;

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
        tatca = findViewById(R.id.tatca);
        choxuly = findViewById(R.id.choxuly);
        danggiao = findViewById(R.id.danggiao);
        dagiao = findViewById(R.id.dagiao);
        dahuy = findViewById(R.id.dahuy);
        setupTabClicks();
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
                            if (order != null) {
                                if (trangThaiChon.equals("Tất cả")) {
                                    listOrder.add(order);
                                } else if (order.getStatus().equals(trangThaiChon)) {
                                    listOrder.add(order);
                                }
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
    private void setupTabClicks() {
        tatca.setOnClickListener(v -> {
            updateTabUI(tatca);
            loadData("Tất cả", userIdInt);
        });
        choxuly.setOnClickListener(v -> {
            updateTabUI(choxuly);
            loadData("Chờ xử lý", userIdInt);
        });
        danggiao.setOnClickListener(v -> {
            updateTabUI(danggiao);
            loadData("Đang giao", userIdInt);
        });
        dagiao.setOnClickListener(v -> {
            updateTabUI(dagiao);
            loadData("Hoàn thành", userIdInt);
        });
        dahuy.setOnClickListener(v -> {
            updateTabUI(dahuy);
            loadData("Đã huỷ", userIdInt);
        });
    }
    private void updateTabUI(TextView selectedTab) {
        TextView[] tabs = {tatca, choxuly, danggiao, dagiao, dahuy};
        for (TextView t : tabs) {
            t.setBackgroundColor(Color.parseColor("#F0F0F0"));
            t.setTextColor(Color.parseColor("#666666"));
        }
        selectedTab.setBackgroundColor(Color.parseColor("#2D68C4"));
        selectedTab.setTextColor(Color.parseColor("#FFFFFF"));
    }
}