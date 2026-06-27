package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderSuccessActivity extends AppCompatActivity {
    private String orderId;

    // Đã thay đổi ánh xạ cho khớp với giao diện mới rút gọn
    private TextView tvAddressSuccess, tvTotal;
    private Button btnHome, btnContinueShopping;
    private ImageButton btnBack;
    private LinearLayout lvOrderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        orderId = getIntent().getStringExtra("order_id");

        tvAddressSuccess = findViewById(R.id.tvAddressSuccess);
        tvTotal = findViewById(R.id.tvTotal);

        btnHome = findViewById(R.id.btnHome);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
        btnBack = findViewById(R.id.btnBack);
        lvOrderItems = findViewById(R.id.lvOrderItems);

        if (orderId != null) {
            loadOrderData();
        }

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, MainActivity.class);
            // Xóa sạch lịch sử màn hình trước đó, người dùng ấn nút Back trên đt sẽ thoát app chứ ko quay lại giỏ hàng
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, ShoppingCartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
                });
        btnBack.setOnClickListener(v -> btnHome.performClick());
    }

    private void loadOrderData() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        // 1. Gắn địa chỉ và tổng tiền
                        tvAddressSuccess.setText(order.getAddress());
                        tvTotal.setText(String.format(java.util.Locale.US, "%,.0f đ", order.getTotalAmount()));

                        // 2. KHÚC NÀY ĐỂ VẼ DANH SÁCH SẢN PHẨM NÈ MÀY
                        if (order.getListItems() != null) {
                            lvOrderItems.removeAllViews(); // Xóa rác cũ nếu có

                            for (int i = 0; i < order.getListItems().size(); i++) {
                                // Dùng layoutOrderItems, false để không bị xẹp kích thước
                                View itemView = getLayoutInflater().inflate(R.layout.layout_item_order_detail, lvOrderItems, false);

                                ImageView imgBook = itemView.findViewById(R.id.imgBook);
                                TextView tvName = itemView.findViewById(R.id.tvBookName);
                                TextView tvPrice = itemView.findViewById(R.id.tvPrice);
                                TextView tvQty = itemView.findViewById(R.id.tvQuantity);

                                // Lôi data từng món ra gắn vào
                                tvName.setText(order.getListItems().get(i).getTenSP());
                                tvPrice.setText(String.format(java.util.Locale.US, "%,.0f đ", order.getListItems().get(i).getGia_Ban()));
                                tvQty.setText("x" + order.getListItems().get(i).getSoLuong());

                                // Load ảnh
                                com.bumptech.glide.Glide.with(OrderSuccessActivity.this)
                                        .load(order.getListItems().get(i).getImg())
                                        .into(imgBook);

                                // Dọng nó vào giao diện
                                lvOrderItems.addView(itemView);
                            }
                        } else {
                            Toast.makeText(OrderSuccessActivity.this, "Ủa, không kéo được list sản phẩm về!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderSuccessActivity.this, "Lỗi lấy chi tiết đơn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
