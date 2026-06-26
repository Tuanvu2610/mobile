package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.adapter.CartAdapter;
import com.example.bansach.model.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends BaseActivity {
    private String userId;
    private ArrayList<CartItem> listThanhToan = new ArrayList<>();
    private TextView tvTotalBottom;
    private ListView listView;
    ListView lvCart;
    TextView tvTotal;
    CartAdapter adapter;
    List<CartItem> cartList;

    SessionManager sessionManager;
    Button btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.acctivity_shopping_cart);
        setupHeader();

        // Ánh xạ view cho danh sách và tổng tiền
        lvCart = findViewById(R.id.lvCart);
        tvTotal = findViewById(R.id.tvTotal);

        //Nút Back
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Nút qua trang Voucher
        TextView tvVoucher = findViewById(R.id.tvVoucher);
        tvVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingCartActivity.this, VoucherActivity.class);
                startActivity(intent);
            }
        });

        // Khởi tạo List và Adapter để hiển thị giỏ hàng
        cartList = new ArrayList<>();
        adapter = new CartAdapter(this, R.layout.item_shopping_cart, cartList);
        lvCart.setAdapter(adapter);

        // Lấy userId
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        btnBuy = findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingCartActivity.this, OrderDetailActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });
        // Bắt đầu kéo dữ liệu
        layDuLieuGioHang();
    }

    private void layDuLieuGioHang() {
        // Trỏ vào node Carts ma kh
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(userId);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
//                double tongTien = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartItem item = dataSnapshot.getValue(CartItem.class);
                    if (item != null) {
                        cartList.add(item);
//                        tongTien += (item.getGia_Ban() * item.getSoLuong());
                    }
                }

                adapter.notifyDataSetChanged();

//                tvTotal.setText(String.format("%,.0f đ", tongTien));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi kéo db giỏ hàng: " + error.getMessage());
            }
        });
    }
//    public void tinhTongTien() {
//        double tongTien = 0;
//        for (CartItem item : listThanhToan) {
//            if (item.isChecked()) {
//                tongTien += (item.getGia_Ban() * item.getSoLuong());
//            }
//        }
//        tvTotal.setText(String.format("%,.0f đ", tongTien));
//    }
public void tinhTongTien() {
    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("OrderDetail").child(userId);

    cartRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            cartList.clear();
            double tongTien = 0;
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                CartItem item = dataSnapshot.getValue(CartItem.class);
                if (item != null) {
                    tongTien += (item.getGia_Ban() * item.getSoLuong());
                }
            }
            tvTotal.setText(String.format("%,.0f đ", tongTien));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("Firebase", "Lỗi kéo db giỏ hàng: " + error.getMessage());
        }
    });
}
}
