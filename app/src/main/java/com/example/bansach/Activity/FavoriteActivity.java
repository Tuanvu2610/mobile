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

        rvFavorites = findViewById(R.id.rvFavorites);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        favoriteBookList = new ArrayList<>();
        favoriteIds = new ArrayList<>();

        adapter = new BookGridAdapter(this, favoriteBookList);
        rvFavorites.setAdapter(adapter);

        loadFavoriteIds();
        setupHeader();
    }

    private void loadFavoriteIds() {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("YeuThich").child(userId);

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteIds.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot idSnap : snapshot.getChildren()) {
                        try {
                            int maSP = Integer.parseInt(idSnap.getKey());
                            favoriteIds.add(maSP);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                loadFavoriteBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadFavoriteBooks() {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("book");

        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteBookList.clear();

                if (snapshot.exists() && !favoriteIds.isEmpty()) {
                    for (DataSnapshot bookSnap : snapshot.getChildren()) {
                        Book book = bookSnap.getValue(Book.class);
                        if (book != null && favoriteIds.contains((int)book.getMaSP())) {
                            favoriteBookList.add(book);
                        }
                    }
                }

                if (favoriteBookList.isEmpty()) {
                    rvFavorites.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvFavorites.setVisibility(View.VISIBLE);
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