package com.example.bansach.Activity.Admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.Book;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProductDetailManager extends AppCompatActivity {

    private long maSP = -1;
    private Uri imageUri = null;
    private String nodeKey = "";
    private String currentImageUrl = "";
    private TextInputEditText edtMaSP, edtTenSach, edtTacGia, edtGiaGoc, edtGiaBan, edtSoLuongKho, edtMoTaSach;
    private ImageView imgCover;
    private MaterialButton btnChonAnh, btnLuu, btnXoaSach;
    private ImageButton btnBack;

    // Firebase
    private DatabaseReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product_management);

        booksRef = FirebaseDatabase.getInstance().getReference("book");
        initViews();
        Intent intent = getIntent();
        TextView txtTitleHeader = findViewById(R.id.tvTitle);

        if (intent != null && intent.hasExtra("maSP")) {
            maSP = intent.getIntExtra("maSP", -1);
            if (txtTitleHeader != null) txtTitleHeader.setText("Chỉnh sửa sách");
            edtMaSP.setEnabled(false);
            btnXoaSach.setVisibility(View.VISIBLE);

            loadBookData(maSP);
        } else {
            if (txtTitleHeader != null) txtTitleHeader.setText("Thêm sách mới");
            btnXoaSach.setVisibility(View.GONE);
        }
        setupClickListeners();
    }

    private void initViews() {
        edtMaSP = findViewById(R.id.edtMaSanPham);
        edtTenSach = findViewById(R.id.edtTenSach);
        edtTacGia = findViewById(R.id.edtTacGia);
        edtGiaGoc = findViewById(R.id.edtGiaGoc);
        edtGiaBan = findViewById(R.id.edtGiaBan);
        edtSoLuongKho = findViewById(R.id.edtSoLuongKho);
        edtMoTaSach = findViewById(R.id.edtMoTaSach);

        imgCover = findViewById(R.id.imgCover);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnLuu = findViewById(R.id.btnLuu);
        btnXoaSach = findViewById(R.id.btnXoaSach);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        // Nút Trở về
        btnBack.setOnClickListener(v -> finish());

        // Nút Chọn Ảnh
        btnChonAnh.setOnClickListener(v -> openGallery());

        // Nút Lưu
        btnLuu.setOnClickListener(v -> saveBookData());

        // Nút Xóa
        btnXoaSach.setOnClickListener(v -> deleteBook());
    }

    private void loadBookData(long id) {
        booksRef.orderByChild("MaSP").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(AdminProductDetailManager.this, "Không tìm thấy sách có mã " + id + " trên hệ thống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DataSnapshot bookSnap : snapshot.getChildren()) {
                    nodeKey = bookSnap.getKey();
                    Book book = bookSnap.getValue(Book.class);

                    if (book != null) {
                        edtMaSP.setText(String.valueOf(book.getMaSP()));
                        edtTenSach.setText(book.getTenSP());
                        edtTacGia.setText(book.getTG());

                        edtGiaGoc.setText(String.valueOf(Math.round(book.getDon_gia())));
                        edtGiaBan.setText(String.valueOf(Math.round(book.getGia_Ban())));

                        currentImageUrl = book.getImg();
                        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                            Glide.with(AdminProductDetailManager.this)
                                    .load(currentImageUrl)
                                    .into(imgCover);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminProductDetailManager.this, "Lỗi truy vấn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBookData() {
        String strMaSP = edtMaSP.getText().toString().trim();
        String tenSach = edtTenSach.getText().toString().trim();
        String tacGia = edtTacGia.getText().toString().trim();
        String strGiaGoc = edtGiaGoc.getText().toString().trim();
        String strGiaBan = edtGiaBan.getText().toString().trim();

        if (strMaSP.isEmpty() || tenSach.isEmpty() || strGiaGoc.isEmpty() || strGiaBan.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        long bookId = Long.parseLong(strMaSP);
        double giaGoc = Double.parseDouble(strGiaGoc);
        double giaBan = Double.parseDouble(strGiaBan);

        Book book = new Book();
        book.setMaSP((int) bookId);
        book.setTenSP(tenSach);
        book.setTG(tacGia);
        book.setDon_gia(giaGoc);
        book.setGia_Ban(giaBan);

        if (imageUri != null) {
            book.setImg(imageUri.toString());
        } else {
            book.setImg(currentImageUrl);
        }
        String finalKey;
        if (maSP != -1 && !nodeKey.isEmpty()) {
            finalKey = nodeKey;
        } else {
            finalKey = String.valueOf(bookId);
        }
        booksRef.child(finalKey).setValue(book)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminProductDetailManager.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AdminProductDetailManager.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteBook() {
        if (!nodeKey.isEmpty()) {
            booksRef.child(nodeKey).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminProductDetailManager.this, "Đã xóa sách thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AdminProductDetailManager.this, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Không thể xác định vị trí sách để xóa!", Toast.LENGTH_SHORT).show();
        }
    }


    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgCover.setImageURI(imageUri);
                }
            }
    );

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
}