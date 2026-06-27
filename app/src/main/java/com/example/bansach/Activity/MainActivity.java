package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private ArrayList<Book> saleList;
    private ArrayList<Book> bestSellersList;
    private ArrayList<Book> newArrivalsList;

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

    private void fetchOnlineDataProduct() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference booksRef = database.getReference("book");
        saleList = new ArrayList<>();
        bestSellersList = new ArrayList<>();
        newArrivalsList = new ArrayList<>();

        adapterGiamGia = new MainAdapter(MainActivity.this, saleList);
        adapterBestSeller = new MainAdapter(MainActivity.this, bestSellersList);
        adapterNewArrivals = new MainAdapter(MainActivity.this, newArrivalsList);

        rcvExploreInterest.setAdapter(adapterGiamGia);
        rcvBestSellers.setAdapter(adapterBestSeller);
        rcvNewArrivals.setAdapter(adapterNewArrivals);

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saleList.clear();
                bestSellersList.clear();
                newArrivalsList.clear();

                for (DataSnapshot bookSnap : snapshot.getChildren()){
                    Book book = bookSnap.getValue(Book.class);
                    if (book != null) {

                        if (book.getGia_Ban() < book.getDon_gia()) {
                            saleList.add(book);
                        }
                        if (book.getReviewCount() >= 20) {
                            bestSellersList.add(book);
                        }
                        if (book.getNam_XB() != null &&
                                (book.getNam_XB().contains("2024") || book.getNam_XB().contains("2023"))) {
                            newArrivalsList.add(book);
                        }
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