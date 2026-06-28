package com.example.bansach.Activity.Admin;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bansach.R;
import com.example.bansach.adapter.AdminBottomSheetAdapter;
import com.example.bansach.adapter.AdminOrderAdapter;
import com.example.bansach.model.Book;
import com.example.bansach.model.Order;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AdminOrderActivity extends AppCompatActivity {

    private TextView tabAll, tabPending, tabShipping, tabDone, tabCancelled, tvOrderCount;
    private RecyclerView rvOrders;
    private List<Order> allOrderList = new ArrayList<>();
    private List<Order> filteredList = new ArrayList<>();
    private AdminOrderAdapter adapter;
    private String currentStatus = "Tất cả";
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_order);

        // 1. Ánh xạ giao diện
        tabAll = findViewById(R.id.tab_all);
        tabPending = findViewById(R.id.tab_pending);
        tabShipping = findViewById(R.id.tab_shipping);
        tabDone = findViewById(R.id.tab_done);
        tabCancelled = findViewById(R.id.tab_cancelled);
        tvOrderCount = findViewById(R.id.tv_order_count);
        rvOrders = findViewById(R.id.admin_order);

        // 2. Setup RecyclerView chính
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(this, filteredList, new AdminOrderAdapter.OnOrderClickListener() {
            @Override
            public void onDetailClick(Order order) {
                showBottomSheet(order); // Kích hoạt Bottom Sheet khi bấm nút Chi tiết
            }
        });
        rvOrders.setAdapter(adapter);
        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProduct(s.toString().trim());
            }
        });
        // 3. Tải data & Bắt sự kiện chuyển Tab
        fetchOrdersFromFirebase();
        setupTabClicks();
    }

    private void fetchOrdersFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allOrderList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    if (order != null) {
                        allOrderList.add(order);
                    }
                }
                // Đảo ngược list để đơn mới nhất lên đầu
                Collections.reverse(allOrderList);

                // Tải xong thì lọc theo Tab đang đứng
                filterOrdersByStatus(currentStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminOrderActivity.this, "Lỗi tải đơn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTabClicks() {
        tabAll.setOnClickListener(v -> { updateTabUI(tabAll); filterOrdersByStatus("Tất cả"); });
        tabPending.setOnClickListener(v -> { updateTabUI(tabPending); filterOrdersByStatus("Chờ xử lý"); });
        tabShipping.setOnClickListener(v -> { updateTabUI(tabShipping); filterOrdersByStatus("Đang giao"); });
        tabDone.setOnClickListener(v -> { updateTabUI(tabDone); filterOrdersByStatus("Hoàn thành"); });
        tabCancelled.setOnClickListener(v -> { updateTabUI(tabCancelled); filterOrdersByStatus("Đã huỷ"); });
    }

    private void filterOrdersByStatus(String status) {
        currentStatus = status;
        filteredList.clear();

        for (Order o : allOrderList) {
            if (status.equals("Tất cả") || o.getStatus().equals(status)) {
                filteredList.add(o);
            }
        }
        adapter.notifyDataSetChanged();
        tvOrderCount.setText(filteredList.size() + " đơn");
    }

    private void updateTabUI(TextView selectedTab) {
        TextView[] tabs = {tabAll, tabPending, tabShipping, tabDone, tabCancelled};
        for (TextView t : tabs) {
            t.setBackgroundColor(Color.parseColor("#F0F0F0"));
            t.setTextColor(Color.parseColor("#666666"));
        }
        selectedTab.setBackgroundColor(Color.parseColor("#2D68C4"));
        selectedTab.setTextColor(Color.parseColor("#FFFFFF"));
    }

    // --- LOGIC TRỒI BOTTOM SHEET LÊN VÀ XỬ LÝ 2 NÚT ---
    private void showBottomSheet(Order order) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.admin_bottom_sheet_order_detail, null);
        bottomSheetDialog.setContentView(view);

        // Ánh xạ
        TextView tvId = view.findViewById(R.id.tv_detail_order_id);
        TextView tvStatus = view.findViewById(R.id.tv_detail_status);
        TextView tvName = view.findViewById(R.id.tv_detail_customer_name);
        TextView tvPhone = view.findViewById(R.id.tv_detail_customer_id); // Chỗ này XML mày đặt ID là customer_id, tao dùng mượn làm số điện thoại luôn nha
        TextView tvAddress = view.findViewById(R.id.tv_detail_address);
        TextView tvTotal = view.findViewById(R.id.tv_detail_total);
        RecyclerView rvItems = view.findViewById(R.id.rv_order_items);
        Button btnDelete = view.findViewById(R.id.btn_detail_delete);
        Button btnUpdate = view.findViewById(R.id.btn_detail_update);

        // Gắn data text
        tvId.setText("Mã: " + order.getOrderId().substring(0, 8));
        tvStatus.setText(order.getStatus());
        tvName.setText(order.getCustomerName());
        tvPhone.setText(order.getCustomerPhone());
        tvAddress.setText(order.getAddress());
        tvTotal.setText(String.format(Locale.US, "%,.0f đ", order.getTotalAmount()));

        // Gắn danh sách sách vào Adapter
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        AdminBottomSheetAdapter detailAdapter = new AdminBottomSheetAdapter(this, order.getListItems());
        rvItems.setAdapter(detailAdapter);

        // Nút Cập nhật trạng thái đổi tên theo tình hình
        if (order.getStatus().equals("Chờ xử lý")) {
            btnUpdate.setText("Duyệt đơn");
        } else if (order.getStatus().equals("Đang giao")) {
            btnUpdate.setText("Hoàn thành");
        } else {
            btnUpdate.setVisibility(View.GONE); // Ẩn đi nếu đã hoàn thành/hủy
        }

        // Bấm nút Hủy
        btnDelete.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Orders").child(order.getOrderId())
                    .child("status").setValue("Đã huỷ").addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đã chuyển đơn vào mục Đã huỷ!", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                        }
                    });
        });

        // Bấm nút Duyệt
        btnUpdate.setOnClickListener(v -> {
            String newStatus = order.getStatus().equals("Chờ xử lý") ? "Đang giao" : "Hoàn thành";
            FirebaseDatabase.getInstance().getReference("Orders").child(order.getOrderId())
                    .child("status").setValue(newStatus).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                        }
                    });
        });

        bottomSheetDialog.show();
    }
    public void filterProduct(String keyword) {
        filteredList.clear();
        if (keyword.isEmpty()) {
            filteredList.addAll(allOrderList);
        } else {
            for (Order p : allOrderList) {
                if (p.getCustomerName() != null && p.getCustomerName().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
