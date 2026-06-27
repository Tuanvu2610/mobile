package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.CartItem;
import com.example.bansach.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity {
    private int userId;
    private ArrayList<CartItem> listThanhToan = new ArrayList<>();
    private double tongTienDonHang = 0;

    private TextView tvTotalBottom, tvTotalItemsCount, tvCustomerNamePhone, tvCustomerAddress, tvSelectedPayment, tvSubTotal;
    private LinearLayout layoutProductContainer;
    private Button btnPlaceOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        tvTotalBottom = findViewById(R.id.tvTotalBottom);
        layoutProductContainer = findViewById(R.id.layoutProductContainer);
        tvTotalItemsCount = findViewById(R.id.tvTotalItemsCount);
        tvCustomerNamePhone = findViewById(R.id.tvCustomerNamePhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);
        tvSelectedPayment = findViewById(R.id.tvSelectedPayment);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);


        Intent intent = getIntent();
       userId =Integer.parseInt(intent.getStringExtra("user_id"));
       String name_voucher = intent.getStringExtra("name_voucher");
       if (userId != -1){
           fetchOnlineDataProduct();
       }else {
           Toast.makeText(this, "Lỗi userId", Toast.LENGTH_SHORT).show();
       }
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Bắt sự kiện đặt hàng
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listThanhToan.isEmpty()) {
                    Toast.makeText(OrderDetailActivity.this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                xuLyDatHang();
            }
        });
        TextView txtVoucherDetail = findViewById(R.id.txtVoucherDetail);
        if (name_voucher == null) {
            txtVoucherDetail.setText("Chọn voucher");
        }else {
        txtVoucherDetail.setText(name_voucher);
        }

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        double discount = data.getDoubleExtra("discount", 0);
                        String idVoucher = data.getStringExtra("id_voucher");
                        String tenVoucher = data.getStringExtra("name_voucher");

                        if (tenVoucher != null && txtVoucherDetail != null) {
                            txtVoucherDetail.setText("Mã giảm giá: " + tenVoucher);

                            // Cập nhật tiền (hàm này mình đã hướng dẫn ở trên)
//                                tinhTongTienVoucher(discount);
                        } else {
                            Toast.makeText(OrderDetailActivity.this, "Không nhận được dữ liệu voucher", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // 3. Set sự kiện click sau khi đã có launcher
        if (txtVoucherDetail != null) {
            txtVoucherDetail.setOnClickListener(v -> {
                Intent intentDetail = new Intent(OrderDetailActivity.this, VoucherActivity.class);
                launcher.launch(intentDetail);
            });
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
                        if (!isDestroyed() && !isFinishing()) {
                            Glide.with(OrderDetailActivity.this)
                                    .load(item.getImg())
                                    .into(imgBook);
                        }

                        layoutProductContainer.addView(itemView);
                    }
                }
                tinhTongTien();
            }
            private void tinhTongTien() {
                tongTienDonHang = 0;
                for (CartItem item : listThanhToan) {
                    tongTienDonHang += (item.getGia_Ban() * item.getSoLuong());
                }
                tvTotalItemsCount.setText("Tổng số tiền ( " + listThanhToan.size() + " sản phẩm)");
                tvSubTotal.setText(String.format("%,.0f đ", tongTienDonHang));
                tvTotalBottom.setText(String.format("%,.0f đ", tongTienDonHang));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void xuLyDatHang() {
        // 1. Tạo Orders trên Firebase
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders");
        String orderId = orderRef.push().getKey();

        String rawText = tvCustomerNamePhone.getText().toString();
        String diaChi = tvCustomerAddress.getText().toString();
        String phuongThuc = tvSelectedPayment.getText().toString();

        // 3. Xử lý "chặt" chuỗi Tên và SĐT
        String tenKhach = rawText;
        String sdtKhach = "";
        if (rawText.contains("(+84)")) {
            String[] parts = rawText.split("\\(\\+84\\)");
            tenKhach = parts[0].trim(); // Lấy khúc tên
            sdtKhach = "0" + parts[1].trim(); // Lấy khúc SĐT, thêm số 0
        }

        // 4. Lấy ngày giờ hệ thống hiện tại
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        String ngayDatHang = sdf.format(new java.util.Date());
        // Đóng gói dữ liệu
        Order newOrder = new Order(tenKhach, sdtKhach, ngayDatHang, orderId, userId, diaChi, tongTienDonHang, phuongThuc, "Chờ xử lý", listThanhToan);

        if (orderId != null) {
            orderRef.child(orderId).setValue(newOrder).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Xóa giỏ hàng nhánh OrderDetail
                    FirebaseDatabase.getInstance().getReference("OrderDetail")
                            .child(String.valueOf(userId)).removeValue();

                    // Xóa luôn giỏ hàng nhánh Carts
                    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(String.valueOf(userId));
                    for (CartItem item : listThanhToan) {
                        cartRef.child(String.valueOf(item.getMaSP())).removeValue();
                    }

                    // Chuyển qua trang Success
                    Intent intent = new Intent(OrderDetailActivity.this, OrderSuccessActivity.class);
                    intent.putExtra("order_id", orderId);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(OrderDetailActivity.this, "Lỗi khi đặt hàng!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}