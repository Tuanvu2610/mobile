package com.example.bansach.Activity;

import static java.lang.String.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bansach.R;
import com.example.bansach.adapter.BookGridAdapter;
import com.example.bansach.api.ApiClient;
import com.example.bansach.api.ApiService;
import com.example.bansach.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListProductActivity extends BaseActivity {

    private RecyclerView rvProducts;
    private BookGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        fetchOnlineDataProduct();
        setupHeader();
    }

    private void fetchOnlineDataProduct() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference booksRef = database.getReference("book");

        List<Book> dataList = new ArrayList<>();
        adapter = new BookGridAdapter(ListProductActivity.this, dataList);
        rvProducts.setAdapter(adapter);
        Intent intent = getIntent();
        int category_id = intent.getIntExtra("CATEGORY_ID", -1);

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot bookSnap : snapshot.getChildren()) {
                    Book book = bookSnap.getValue(Book.class);
                    if (book != null) {
                        if (category_id == book.getCategory_id()){
                            dataList.add(book);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ListProductActivity.this, "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void chuyen(View view){
        Intent intent = new Intent(ListProductActivity.this, FilterActivity.class);
        startActivity(intent);
    }
    public void checked(View view){
        boolean isChecked = false;
        if (isChecked) {
            // TODO: Chỗ này bạn viết lệnh lưu vào Firebase (đánh dấu sách yêu thích)
            Toast.makeText(ListProductActivity.this, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
        } else {
            // TODO: Chỗ này viết lệnh xóa khỏi Firebase
        }
    }
}