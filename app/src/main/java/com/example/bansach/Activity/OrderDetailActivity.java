package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity {
    private int userId;
    private ArrayList<CartItem> listThanhToan = new ArrayList<>();

    private TextView tvTotalBottom, tvTotalItemsCount;
    private LinearLayout layoutProductContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        tvTotalBottom = findViewById(R.id.tvTotalBottom);
        layoutProductContainer = findViewById(R.id.layoutProductContainer);
        tvTotalItemsCount = findViewById(R.id.tvTotalItemsCount);
        Intent intent = getIntent();
       userId =Integer.parseInt(intent.getStringExtra("user_id"));
       if (userId != -1){
           fetchOnlineDataProduct();
       }else {
           Toast.makeText(this, "Lỗi userId", Toast.LENGTH_SHORT).show();
       }
    }

    private void fetchOnlineDataProduct() {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("OrderDetail").child(String.valueOf(userId));

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listThanhToan.clear();
                layoutProductContainer.removeAllViews();

                for (DataSnapshot data : snapshot.getChildren()) {
                    CartItem item = data.getValue(CartItem.class);
                    if (item != null) {
                        listThanhToan.add(item);

                        View itemView = getLayoutInflater().inflate(R.layout.layout_item_order_detail, null);

                        ImageView imgBook = itemView.findViewById(R.id.imgBook);
                        TextView tvName = itemView.findViewById(R.id.tvBookName);
                        TextView tvPrice = itemView.findViewById(R.id.tvPrice);
                        TextView tvQty = itemView.findViewById(R.id.tvQuantity);

                        tvName.setText(item.getTenSP());
                        tvPrice.setText(String.format("%,.0f đ", item.getGia_Ban()));
                        tvQty.setText("x" + item.getSoLuong());
                        Glide.with(OrderDetailActivity.this).load(item.getImg()).into(imgBook);

                        layoutProductContainer.addView(itemView);
                    }
                }
                tinhTongTien();
            }
            private void tinhTongTien() {
                double tongTien = 0;
                for (CartItem item : listThanhToan) {
                    tongTien += (item.getGia_Ban() * item.getSoLuong());
                }
                tvTotalItemsCount.setText("Tổng số tiền ( "+ listThanhToan.size()+" sản phẩm)");
                tvTotalBottom.setText(String.format("%,.0f đ", tongTien));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}