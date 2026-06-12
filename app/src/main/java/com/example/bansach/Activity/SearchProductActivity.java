package com.example.bansach.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;
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

public class SearchProductActivity extends AppCompatActivity {
    private RecyclerView rvProductsSearch;
    private BookGridAdapter adapter;
    private List<Book> filterList, fullBookList;
    private String keyword = "";
    private TextView tvNoResult;
    private EditText edtSearch;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_search);
        tvNoResult = findViewById(R.id.tvNoResult);
        rvProductsSearch = findViewById(R.id.rvProductsSearch);
        edtSearch = findViewById(R.id.edtSearch);
        rvProductsSearch.setLayoutManager(new GridLayoutManager(this, 2));
        filterList = new ArrayList<>();
        fullBookList = new ArrayList<>();
        adapter = new BookGridAdapter(SearchProductActivity.this, filterList);
        rvProductsSearch.setAdapter(adapter);
        if (getIntent() != null && getIntent().hasExtra("KEYWORD")) {
            keyword = getIntent().getStringExtra("KEYWORD");
            if (keyword != null) {
                edtSearch.setText(keyword);
                edtSearch.setSelection(keyword.length());
            }
        }
        fetchOnlineDataProduct();
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        keyword = s.toString().trim();
                        filterProduct(keyword);
                    }
                };
                handler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        View btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
    public void filterProduct(String keyword) {
        filterList.clear();
        if(keyword == null || keyword.isEmpty()) {
            filterList.addAll(fullBookList);
        } else {
            for(Book p : fullBookList){
                if(p != null && p.getTenSP() != null && p.getTenSP().toLowerCase().contains(keyword.toLowerCase())) {
                    filterList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
        if (filterList.isEmpty()) {
            rvProductsSearch.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.VISIBLE);
        } else {
            rvProductsSearch.setVisibility(View.VISIBLE);
            tvNoResult.setVisibility(View.GONE);
        }
    }
    private void fetchOnlineDataProduct() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference booksRef = database.getReference("book");
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                fullBookList.clear();
                for (DataSnapshot bookSnap : snapshot.getChildren()) {
                    Book book = bookSnap.getValue(Book.class);
                    if (book != null) {
                        fullBookList.add(book);
                    }
                }
                filterProduct(keyword);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
