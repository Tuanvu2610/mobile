package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import com.example.bansach.R;
import com.example.bansach.adapter.MainAdapter;
import com.example.bansach.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {

    private RecyclerView rcvExploreInterest, rcvBestSellers, rcvNewArrivals;
    private MainAdapter adapterGiamGia, adapterBestSeller, adapterNewArrivals;
    private ArrayList<Book> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setupHeader();

        rcvExploreInterest = findViewById(R.id.rcvExploreInterest);
        rcvBestSellers = findViewById(R.id.bestSellers);
        rcvNewArrivals = findViewById(R.id.rcvNewArrivals);

        fetchOnlineDataProduct();
    }

    private void setupRecyclerViews() {
        rcvExploreInterest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvBestSellers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvNewArrivals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void fetchOnlineDataProduct() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference booksRef = database.getReference("book");

        dataList = new ArrayList<>();

        adapterGiamGia = new MainAdapter(dataList);
        adapterBestSeller = new MainAdapter(dataList);
        adapterNewArrivals = new MainAdapter(dataList);

        rcvExploreInterest.setAdapter(adapterGiamGia);
        rcvBestSellers.setAdapter(adapterBestSeller);
        rcvNewArrivals.setAdapter(adapterNewArrivals);

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();

                for (DataSnapshot bookSnap : snapshot.getChildren()){
                    Book book = bookSnap.getValue(Book.class);
                    if (book != null) {
                        dataList.add(book);
                    }
                }

                adapterGiamGia.notifyDataSetChanged();
                adapterBestSeller.notifyDataSetChanged();
                adapterNewArrivals.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void chuyen(View view){
        Intent intent = new Intent(MainActivity.this, ListProductActivity.class);
        startActivity(intent);
    }
}