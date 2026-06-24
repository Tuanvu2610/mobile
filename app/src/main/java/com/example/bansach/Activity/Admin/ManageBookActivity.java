package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bansach.Activity.BaseActivity;
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

public class ManageBookActivity extends BaseActivity {

    private RecyclerView rvAdminBooks;
    private AdminBookAdapter adminBookAdapter;
    private List<Book> bookList;
    AppCompatButton btnAddBook;
    private DatabaseReference booksRef;
    private int masp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_manage_book);
        rvAdminBooks = findViewById(R.id.rvAdminBooks);
        btnAddBook = findViewById(R.id.btnAddBook);
        rvAdminBooks.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<>();

        adminBookAdapter = new AdminBookAdapter(this, bookList);
        rvAdminBooks.setAdapter(adminBookAdapter);
        btnAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(ManageBookActivity.this, AdminProductDetailManager.class);
            startActivity(intent);
        });
        loadBooksFromFirebase();
    }

    private void loadBooksFromFirebase() {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("book");

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot bookSnap : snapshot.getChildren()) {
                        Book book = bookSnap.getValue(Book.class);
                        if (book != null) {
                            bookList.add(book);
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
}