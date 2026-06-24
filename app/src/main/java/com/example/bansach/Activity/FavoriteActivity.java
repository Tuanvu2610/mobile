package com.example.bansach.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bansach.R;
import com.example.bansach.adapter.BookGridAdapter;
import com.example.bansach.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends BaseActivity {

    private RecyclerView rvFavorites;
    private LinearLayout layoutEmpty;
    private BookGridAdapter adapter;
    private List<Book> favoriteBookList;
    private List<Integer> favoriteIds;
    SessionManager sessionManager;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        // Ánh xạ View
        rvFavorites = findViewById(R.id.rvFavorites);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        favoriteBookList = new ArrayList<>();
        favoriteIds = new ArrayList<>();

        // Dùng lại cái Adapter Grid xịn của bạn luôn, đỡ phải viết cái mới
        adapter = new BookGridAdapter(this, favoriteBookList);
        rvFavorites.setAdapter(adapter);

        loadFavoriteIds();
        setupHeader();
    }

    // Hàm 1: Lấy các ID sách đã được thả tim
    private void loadFavoriteIds() {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("YeuThich").child(userId);

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteIds.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot idSnap : snapshot.getChildren()) {
                        // Lấy key (chính là MaSP ví dụ: "2", "200") chuyển thành số nguyên
                        try {
                            int maSP = Integer.parseInt(idSnap.getKey());
                            favoriteIds.add(maSP);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Sau khi có danh sách ID rồi, tiến hành kéo sách về đối chiếu
                loadFavoriteBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Hàm 2: Tải toàn bộ sách và lọc lại theo ID đã thích
    private void loadFavoriteBooks() {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("book");

        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteBookList.clear();

                if (snapshot.exists() && !favoriteIds.isEmpty()) {
                    for (DataSnapshot bookSnap : snapshot.getChildren()) {
                        Book book = bookSnap.getValue(Book.class);
                        // Nếu sách tồn tại và mã sách nằm trong danh sách Id đã thích
                        if (book != null && favoriteIds.contains((int)book.getMaSP())) {
                            favoriteBookList.add(book);
                        }
                    }
                }

                // --- XỬ LÝ ĐỔI GIAO DIỆN (UX EMPTY STATE) ---
                if (favoriteBookList.isEmpty()) {
                    rvFavorites.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE); // Hiện thông báo rỗng
                } else {
                    layoutEmpty.setVisibility(View.GONE);    // Ẩn thông báo rỗng đi
                    rvFavorites.setVisibility(View.VISIBLE); // Hiện danh sách sách lên
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoriteActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}