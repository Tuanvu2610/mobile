    package com.example.bansach.Activity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageButton;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
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
        private TextView tvTotalBottom, tvVoucher;
        private ListView listView;
        ListView lvCart;
        TextView tvTotal;
        CartAdapter adapter;
        List<CartItem> cartList;

        SessionManager sessionManager;
        Button btnBuy;
        String idVoucher;
        String tenVoucher;
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
            // 1. Ánh xạ View trước
            tvVoucher = findViewById(R.id.tvVoucher);

            // 2. Khai báo Launcher (đảm bảo tvVoucher đã được ánh xạ)
            ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();

                            // Dùng phương thức getDoubleExtra/getStringExtra an toàn hơn
                            double discount = data.getDoubleExtra("discount", 0);
                            idVoucher = data.getStringExtra("id_voucher");
                            tenVoucher = data.getStringExtra("name_voucher");

                            // Kiểm tra null ở đây
                            if (tenVoucher != null && tvVoucher != null) {
                                tvVoucher.setText("Mã giảm giá: " + tenVoucher);

                                // Cập nhật tiền (hàm này mình đã hướng dẫn ở trên)
//                                tinhTongTienVoucher(discount);
                            } else {
                                Toast.makeText(ShoppingCartActivity.this, "Không nhận được dữ liệu voucher", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

        // 3. Set sự kiện click sau khi đã có launcher
            if (tvVoucher != null) {
                tvVoucher.setOnClickListener(v -> {
                    Intent intent = new Intent(ShoppingCartActivity.this, VoucherActivity.class);
                    launcher.launch(intent);
                });
            }

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
                    ArrayList<CartItem> danhSachChotDon = new ArrayList<>();
                    for (CartItem item : cartList) {
                        if (item.isChecked()) {
                            danhSachChotDon.add(item);
                        }
                    }

                    // 2. Kiểm tra xem khách có chọn món nào không
                    if (danhSachChotDon.isEmpty()) {
                        Toast.makeText(ShoppingCartActivity.this, "Vui lòng chọn ít nhất 1 sản phẩm để mua!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("OrderDetail").child(userId);

                    orderRef.removeValue();

                    for (CartItem item : danhSachChotDon) {
                        orderRef.child(String.valueOf(item.getMaSP())).setValue(item);
                    }

                    Intent intent = new Intent(ShoppingCartActivity.this, OrderDetailActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("voucher_id", idVoucher);
                    intent.putExtra("name_voucher", tenVoucher);

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
                    ArrayList<String> danhSachDaTick = new ArrayList<>();
                    for(CartItem c : cartList) {
                        if (c.isChecked()) {
                            danhSachDaTick.add(String.valueOf(c.getMaSP()));
                        }
                    }
                    cartList.clear();
    //               double tongTien = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
    //                    CartItem item = dataSnapshot.getValue(CartItem.class);
    //                    if (item != null) {
    //                        cartList.add(item);
    ////                        tongTien += (item.getGia_Ban() * item.getSoLuong());
    //                    }
                        try {
                            // Đưa lệnh ép kiểu vào trong khối try để bảo vệ
                            CartItem item = dataSnapshot.getValue(CartItem.class);
                            if (item != null) {
                                if (danhSachDaTick.contains(String.valueOf(item.getMaSP()))) {
                                    item.setChecked(true); // Có thì tick lại cho nó!
                                }
                                cartList.add(item);
    //                            tongTien += (item.getGia_Ban() * item.getSoLuong());
                            }
                        } catch (Exception e) {
                            // KHI ĐỤNG PHẢI "RÁC" (Long), APP SẼ KHÔNG VĂNG MÀ NHẢY VÀO ĐÂY
                            Log.e("Firebase_Bug", "Phát hiện dữ liệu sai cấu trúc tại Key: "
                                    + dataSnapshot.getKey() + " | Giá trị: " + dataSnapshot.getValue());
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
//    public void tinhTongTien() {
//        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("OrderDetail").child(userId);
//
//        cartRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                cartList.clear();
//                double tongTien = 0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    CartItem item = dataSnapshot.getValue(CartItem.class);
//                    if (item != null) {
//                        tongTien += (item.getGia_Ban() * item.getSoLuong());
//                    }
//                }
//                tvTotal.setText(String.format("%,.0f đ", tongTien));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", "Lỗi kéo db giỏ hàng: " + error.getMessage());
//            }
//        });
//    }
//    public void tinhTongTien() {
//        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("OrderDetail").child(userId);
//
//        cartRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // ĐỪNG gọi cartList.clear() ở đây, nó sẽ xóa sạch danh sách giỏ hàng bạn vừa kéo ở trên!
//                // cartList.clear();
//
//                double tongTien = 0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    try { // BỌC KHIÊN BẢO VỆ VÀO ĐÂY NÈ
//                        CartItem item = dataSnapshot.getValue(CartItem.class);
//                        if (item != null) {
//                            tongTien += (item.getGia_Ban() * item.getSoLuong());
//                        }
//                    } catch (Exception e) {
//                        Log.e("Firebase_Bug", "Lỗi ép kiểu ở OrderDetail, Key: " + dataSnapshot.getKey());
//                    }
//                }
//                tvTotal.setText(String.format("%,.0f đ", tongTien));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", "Lỗi: " + error.getMessage());
//            }
//        });
//    }
    public void tinhTongTien() {
        double tongTien = 0;
        for (CartItem item : cartList) {
            if (item.isChecked()) {
                tongTien += (item.getGia_Ban() * item.getSoLuong());
            }
        }

        tvTotal.setText(String.format("%,.0f đ", tongTien));
    }
    }
