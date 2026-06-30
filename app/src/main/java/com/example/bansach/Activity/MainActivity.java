package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
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
    private ImageView imgMainBanner, bg1, bg2, bg3, bg4;
    private Button btnSeeAll;
    private ArrayList<Integer> listChildIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setupHeader();

        rcvExploreInterest = findViewById(R.id.rcvExploreInterest);
        rcvBestSellers = findViewById(R.id.bestSellers);
        rcvNewArrivals = findViewById(R.id.rcvNewArrivals);
        imgMainBanner = findViewById(R.id.imgMainBanner);
        bg1 = findViewById(R.id.bg1);
        bg2 = findViewById(R.id.bg2);
        bg3 = findViewById(R.id.bg3);
        bg4 = findViewById(R.id.bg4);
        Glide.with(MainActivity.this)
                .load("https://png.pngtree.com/thumb_back/fh260/background/20210908/pngtree-library-noon-library-environment-photography-bookstore-learning-photography-map-with-map-image_829742.jpg")
                .into(imgMainBanner);
        Glide.with(MainActivity.this)
                .load("https://tse4.mm.bing.net/th/id/OIP.-QpCN_EcL8HPIhRIGv9LJAHaDK?pid=Api&P=0&h=180")
                .into(bg1);
        Glide.with(MainActivity.this)
                .load("https://thietkelogo.edu.vn/uploads/images/thiet-ke-do-hoa-khac/banner-sach/14.png")
                .into(bg2);
        Glide.with(MainActivity.this)
                .load("https://file.hstatic.net/1000230347/file/4_banner_1920x600_d714af7f299c410194c2eb71c70d1f68.png")
                .into(bg3);
        Glide.with(MainActivity.this)
                .load("https://thietkelogo.edu.vn/uploads/images/thiet-ke-do-hoa-khac/banner-sach/20.png")
                .into(bg4);
        btnSeeAll = findViewById(R.id.seenAll);
        listChildIds = new ArrayList<>();
        listChildIds.add(10);
        listChildIds.add(12);
        listChildIds.add(13);
        btnSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListProductActivity.class);
                intent.putIntegerArrayListExtra("LIST_CHILD_IDS", listChildIds);
                startActivity(intent);
            }
        });

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
                        if (book.getReviewCount() >= 1) {
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