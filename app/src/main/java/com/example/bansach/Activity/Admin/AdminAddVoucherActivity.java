package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.Book;
import com.example.bansach.model.Voucher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminAddVoucherActivity extends BaseActivityAdmin {

    private TextInputEditText etMaVoucher, etTieuDe, etGiaTriGiam, etDieuKien, etHinhAnh;
    private AppCompatButton btnSaveVoucher;
    private DatabaseReference vouchersRef;
    private String currentImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_voucher);
        vouchersRef = FirebaseDatabase.getInstance().getReference("vouchers/vouchers");
        Intent intent = getIntent();
        int mavoucher = intent.getIntExtra("idvoucher", 0);
        initViews();
        if (mavoucher != 0){
            btnSaveVoucher.setText("CẬP NHẬT VOUCHER");
            loadVoucherData(mavoucher);
        }


        btnSaveVoucher.setOnClickListener(v -> handleSaveVoucher());
        setupHeader();
    }

    private void initViews() {
        etMaVoucher = findViewById(R.id.etMaVoucher);
        etTieuDe = findViewById(R.id.etTieuDe);
        etGiaTriGiam = findViewById(R.id.etGiaTriGiam);
        etDieuKien = findViewById(R.id.etDieuKien);
        etHinhAnh = findViewById(R.id.etHinhAnh);
        btnSaveVoucher = findViewById(R.id.btnSaveVoucher);
    }

    private void handleSaveVoucher() {
        String maVoucher = etMaVoucher.getText().toString().trim();
        String tieuDe = etTieuDe.getText().toString().trim();
        String giaTriGiamStr = etGiaTriGiam.getText().toString().trim();
        String dieuKien = etDieuKien.getText().toString().trim();
        String hinhAnh = etHinhAnh.getText().toString().trim();

        if (maVoucher.isEmpty() || tieuDe.isEmpty() || giaTriGiamStr.isEmpty() || dieuKien.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        int giaTriGiam = Integer.parseInt(giaTriGiamStr);
        String finalHinhAnh = hinhAnh.isEmpty() ? "https://cdn.hstatic.net/themes/200000896417/1001488735/14/home_coupon_1_img.png?v=343" : hinhAnh;
        btnSaveVoucher.setEnabled(false);
        vouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int nextId = (int) snapshot.getChildrenCount() + 1;
                String customKey = "v" + nextId;
                Voucher newVoucher = new Voucher(nextId, finalHinhAnh, tieuDe, dieuKien, maVoucher, giaTriGiam);
                vouchersRef.child(customKey).setValue(newVoucher)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AdminAddVoucherActivity.this, "Đã phát hành mã giảm giá " + maVoucher + " thành công!", Toast.LENGTH_SHORT).show();
                            clearFields();
                            btnSaveVoucher.setEnabled(true);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AdminAddVoucherActivity.this, "Lỗi thêm dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            btnSaveVoucher.setEnabled(true);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnSaveVoucher.setEnabled(true);
                Toast.makeText(AdminAddVoucherActivity.this, "Lỗi kết nối database: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        etMaVoucher.setText("");
        etTieuDe.setText("");
        etGiaTriGiam.setText("");
        etDieuKien.setText("");
        etHinhAnh.setText("");
    }
    private void loadVoucherData(int id) {
        vouchersRef.orderByChild("idVoucher").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(AdminAddVoucherActivity.this, "Không tìm thấy voucher có mã " + id + " trên hệ thống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DataSnapshot bookSnap : snapshot.getChildren()) {
                    Voucher voucher = bookSnap.getValue(Voucher.class);

                    if (voucher != null) {
                        etMaVoucher.setText(String.valueOf(voucher.getMaVoucher()));
                        etTieuDe.setText(voucher.getTieuDe());
                        etDieuKien.setText(voucher.getDieuKien());
                        etGiaTriGiam.setText(String.valueOf(voucher.getGiaTriGiam()));
                        etHinhAnh.setText(voucher.getHinhAnh());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminAddVoucherActivity.this, "Lỗi truy vấn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}