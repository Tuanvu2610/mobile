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
    private String currentImageUrl = ""; // Chứa link ảnh hiện tại trên Firebase

    // Khai báo các View
    private TextInputEditText edtMaSP, edtTenSach, edtTacGia, edtGiaGoc, edtGiaBan, edtSoLuongKho, edtMoTaSach;
    private ImageView imgCover;
    private MaterialButton btnChonAnh, btnLuu, btnXoaSach;
    private ImageButton btnBack;

    // Firebase
    private DatabaseReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product_management); // Tên file XML của bạn

        // 1. Khởi tạo Firebase
        booksRef = FirebaseDatabase.getInstance().getReference("book");

        // 2. Ánh xạ View
        initViews();

        // 3. Phân luồng Thêm mới hay Chỉnh sửa
        Intent intent = getIntent();
        TextView txtTitleHeader = findViewById(R.id.tvTitle);

        if (intent != null && intent.hasExtra("maSP")) {
            // ---> CHẾ ĐỘ SỬA
            maSP = intent.getIntExtra("maSP", -1);
            if (txtTitleHeader != null) txtTitleHeader.setText("Chỉnh sửa sách");

            // Khóa không cho sửa Mã Sản Phẩm (Vì nó là khóa chính)
            edtMaSP.setEnabled(false);
            btnXoaSach.setVisibility(View.VISIBLE); // Hiện nút xóa

            loadBookData(maSP);
        } else {
            // ---> CHẾ ĐỘ THÊM MỚI
            if (txtTitleHeader != null) txtTitleHeader.setText("Thêm sách mới");
            btnXoaSach.setVisibility(View.GONE); // Ẩn nút xóa vì sách chưa tồn tại
        }

        // 4. Bắt sự kiện các nút bấm
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

    // ================== LOGIC FIREBASE ==================

    private void loadBookData(long id) {
        // DÙNG QUERY: Tìm các node con có trường "maSP" bằng với id truyền vào
        booksRef.orderByChild("MaSP").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Thêm dòng thông báo này để dễ bắt bệnh (Debug)
                if (!snapshot.exists()) {
                    Toast.makeText(AdminProductDetailManager.this, "Không tìm thấy sách có mã " + id + " trên hệ thống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Vì dùng Query (tìm kiếm) nên Firebase sẽ trả về một Danh sách (dù chỉ có 1 cuốn)
                // Do đó, phải dùng vòng lặp for để lấy cuốn sách đó ra
                for (DataSnapshot bookSnap : snapshot.getChildren()) {
                    Book book = bookSnap.getValue(Book.class);

                    if (book != null) {
                        // Đổ dữ liệu lên UI
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

                        // Đã tìm thấy và đổ dữ liệu xong thì thoát vòng lặp
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
        // Lấy text từ các ô nhập liệu
        String strMaSP = edtMaSP.getText().toString().trim();
        String tenSach = edtTenSach.getText().toString().trim();
        String tacGia = edtTacGia.getText().toString().trim();
        String strGiaGoc = edtGiaGoc.getText().toString().trim();
        String strGiaBan = edtGiaBan.getText().toString().trim();

        // Validate cơ bản
        if (strMaSP.isEmpty() || tenSach.isEmpty() || strGiaGoc.isEmpty() || strGiaBan.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        long bookId = Long.parseLong(strMaSP);
        double giaGoc = Double.parseDouble(strGiaGoc);
        double giaBan = Double.parseDouble(strGiaBan);

        // Tạo đối tượng Book mới
        Book book = new Book();
        book.setMaSP((int) bookId);
        book.setTenSP(tenSach);
        book.setTG(tacGia);
        book.setDon_gia(giaGoc);
        book.setGia_Ban(giaBan);

        // Nếu có chọn ảnh mới thì tạm thời gán bằng text (Để upload file thật cần dùng Firebase Storage)
        if (imageUri != null) {
            book.setImg(imageUri.toString());
        } else {
            book.setImg(currentImageUrl); // Giữ nguyên link ảnh cũ
        }

        // Lưu lên Firebase Realtime Database
        booksRef.child(String.valueOf(bookId)).setValue(book)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminProductDetailManager.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Lưu xong tự động thoát trang
                })
                .addOnFailureListener(e -> Toast.makeText(AdminProductDetailManager.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteBook() {
        if (maSP != -1) {
            booksRef.child(String.valueOf(maSP)).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminProductDetailManager.this, "Đã xóa sách!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AdminProductDetailManager.this, "Xóa thất bại", Toast.LENGTH_SHORT).show());
        }
    }

    // ================== LOGIC CHỌN ẢNH TỪ THƯ VIỆN ==================

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData(); // Lấy đường dẫn ảnh
                    imgCover.setImageURI(imageUri); // Gắn lên giao diện cho người dùng xem trước
                }
            }
    );

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
}