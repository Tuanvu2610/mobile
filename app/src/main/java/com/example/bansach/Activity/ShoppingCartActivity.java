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
        TextView tvOldTotal;
        double tongTien = 0;
        double discount = 0;
        CartAdapter adapter;
        List<CartItem> cartList;

        SessionManager sessionManager;
        Button btnBuy;
        int idVoucher;
        String tenVoucher;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.acctivity_shopping_cart);
            setupHeader();

            lvCart = findViewById(R.id.lvCart);
            tvTotal = findViewById(R.id.tvTotal);
            tvOldTotal = findViewById(R.id.tvOldTotal);

            ImageButton btnBack = findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            tvVoucher = findViewById(R.id.tvVoucher);

            ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();

                            discount = data.getIntExtra("discount", 0);
                            idVoucher = data.getIntExtra("id_voucher",0);
                            tenVoucher = data.getStringExtra("name_voucher");

                            if (tenVoucher != null && tvVoucher != null) {
                                tvVoucher.setText("Mã giảm giá: " + tenVoucher);

                                capNhatTongTien();
                            } else {
                                Toast.makeText(ShoppingCartActivity.this, "Không nhận được dữ liệu voucher", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            if (tvVoucher != null) {
                tvVoucher.setOnClickListener(v -> {
                    Intent intent = new Intent(ShoppingCartActivity.this, VoucherActivity.class);
                    launcher.launch(intent);
                });
            }

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
                    intent.putExtra("discount", discount);
                    startActivity(intent);
                }
            });
            //nut chon tat ca
            android.widget.CheckBox cbSelectAll = findViewById(R.id.cbSelectAll);

            if (cbSelectAll != null) {
                cbSelectAll.setOnClickListener(v -> {
                    boolean isChecked = cbSelectAll.isChecked();

                    if (cartList == null || cartList.isEmpty()) {
                        return;
                    }

                    for (CartItem item : cartList) {
                        item.setChecked(isChecked);
                    }

                    adapter.notifyDataSetChanged();

                    tinhTongTien();
                });
            }
            layDuLieuGioHang();
        }

        private void layDuLieuGioHang() {
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

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            CartItem item = dataSnapshot.getValue(CartItem.class);
                            if (item != null) {
                                if (danhSachDaTick.contains(String.valueOf(item.getMaSP()))) {
                                    item.setChecked(true);
                                }
                                cartList.add(item);
                            }
                        } catch (Exception e) {
                            Log.e("Firebase_Bug", "Phát hiện dữ liệu sai cấu trúc tại Key: "
                                    + dataSnapshot.getKey() + " | Giá trị: " + dataSnapshot.getValue());
                        }
                    }

                    adapter.notifyDataSetChanged();
                    tinhTongTien();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Lỗi kéo db giỏ hàng: " + error.getMessage());
                }
            });
        }

    public void tinhTongTien() {
        tongTien = 0;
        for (CartItem item : cartList) {
            if (item.isChecked()) {
                tongTien += (item.getGia_Ban() * item.getSoLuong());
            }
        }

        capNhatTongTien();
    }

        private void capNhatTongTien() {
            double tongMoi = tongTien - discount;

            if (tongMoi < 0) {
                tongMoi = 0;
            }

            if (discount > 0) {
                tvOldTotal.setVisibility(View.VISIBLE);

                tvOldTotal.setText(
                        String.format("%,.0f đ", tongTien));

                tvOldTotal.setPaintFlags(
                        tvOldTotal.getPaintFlags()
                                | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                tvTotal.setText(
                        String.format("%,.0f đ", tongMoi));
            } else {
                tvOldTotal.setVisibility(View.GONE);
                tvTotal.setText(
                        String.format("%,.0f đ", tongTien));
            }
        }
    }
