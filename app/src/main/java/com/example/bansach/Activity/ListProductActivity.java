package com.example.bansach.Activity;

import android.os.Bundle;
import android.util.Log;
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

public class ListProductActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private BookGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        // 1. Ánh xạ RecyclerView và set layout 2 cột
        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // 2. Kích hoạt hàm lấy dữ liệu online
        fetchOnlineData();
    }

    // Hàm chuyên dụng để gọi mạng
    private void fetchOnlineData() {
        // 1. Khởi tạo và kết nối thẳng lên kho Realtime Database của Google
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // 2. Trỏ bộ định vị vào đúng nút gốc tên là "book" trên web Firebase
        DatabaseReference booksRef = database.getReference("book");

        // 3. Bật bộ lắng nghe sự kiện để hút dữ liệu online về theo thời gian thực
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Tạo một danh sách rỗng để hứng dữ liệu
                List<Book> dataList = new ArrayList<>();

                // Duyệt qua từng nút con (book1, book2...) nằm bên trong nút "book"
                for (DataSnapshot bookSnap : snapshot.getChildren()) {

                    // Ép kiểu dữ liệu JSON từ mạng tự động dịch sang Object Book trong Java
                    Book book = bookSnap.getValue(Book.class);

                    if (book != null) {
                        dataList.add(book); // Thêm cuốn sách vào danh sách tổng
                    }
                }

                // 4. Giao danh sách dữ liệu online cho Adapter để vẽ lên GridView/RecyclerView
                adapter = new BookGridAdapter(ListProductActivity.this, dataList);
                rvProducts.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Hàm này chạy khi bị lỗi đường truyền mạng hoặc cấu hình sai rules bảo mật
                Toast.makeText(ListProductActivity.this, "Lỗi kết nối Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}