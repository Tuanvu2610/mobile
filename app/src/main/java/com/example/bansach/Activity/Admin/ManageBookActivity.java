package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bansach.R;
import com.example.bansach.adapter.AdminBookAdapter;
import com.example.bansach.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageBookActivity extends BaseActivityAdmin {

    private RecyclerView rvAdminBooks;
    private AdminBookAdapter adminBookAdapter;

    private List<Book> bookList;
    private List<Book> filterList;

    AppCompatButton btnAddBook;
    private DatabaseReference booksRef;

    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_manage_book);

        rvAdminBooks = findViewById(R.id.rvAdminBooks);
        btnAddBook = findViewById(R.id.btnAddBook);
        rvAdminBooks.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo cả 2 danh sách
        bookList = new ArrayList<>();
        filterList = new ArrayList<>();

        // QUAN TRỌNG: Giao filterList cho Adapter để khi mình lọc, màn hình sẽ đổi theo
        adminBookAdapter = new AdminBookAdapter(this, filterList);
        rvAdminBooks.setAdapter(adminBookAdapter);

        btnAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(ManageBookActivity.this, AdminProductDetailManager.class);
            startActivity(intent);
        });

        searchText = findViewById(R.id.edtSearchBook);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gọi hàm lọc và cắt khoảng trắng 2 đầu
                filterProduct(s.toString().trim());
            }
        });

        loadBooksFromFirebase();
    }

    private void loadBooksFromFirebase() {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("book");

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xóa cả 2 danh sách trước khi nạp data mới
                bookList.clear();
                filterList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot bookSnap : snapshot.getChildren()) {
                        Book book = bookSnap.getValue(Book.class);
                        if (book != null) {
                            bookList.add(book);
                            filterList.add(book); // Lúc đầu chưa tìm kiếm thì hiển thị tất cả
                        }
                    }
                }

                adminBookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageBookActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm lọc đã được sửa tên biến cho đồng bộ
    public void filterProduct(String keyword) {
        filterList.clear();
        if (keyword.isEmpty()) {
            // Rỗng thì trả về danh sách gốc
            filterList.addAll(bookList);
        } else {
            // Có chữ thì quét qua danh sách gốc
            for (Book p : bookList) {
                // Thêm điều kiện != null để chống văng app nếu lỡ có sách bị thiếu tên
                if (p.getTenSP() != null && p.getTenSP().toLowerCase().contains(keyword.toLowerCase())) {
                    filterList.add(p);
                }
            }
        }
        // Gọi đúng tên biến adapter
        adminBookAdapter.notifyDataSetChanged();
    }
}