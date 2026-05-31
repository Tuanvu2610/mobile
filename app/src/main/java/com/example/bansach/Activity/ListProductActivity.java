package com.example.bansach.Activity;

import static java.lang.String.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListProductActivity extends BaseActivity {

    private RecyclerView rvProducts;
    private BookGridAdapter adapter;
    private List<Book> dataList;
    private List<Book> fullBookList;

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

        dataList = new ArrayList<>();
        fullBookList = new ArrayList<>();
        adapter = new BookGridAdapter(ListProductActivity.this, dataList);
        rvProducts.setAdapter(adapter);
        ArrayList<Integer> listChildIds = getIntent().getIntegerArrayListExtra("LIST_CHILD_IDS");
        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataList.clear();
                fullBookList.clear();
                for (DataSnapshot bookSnap : snapshot.getChildren()){
                    Book book = bookSnap.getValue(Book.class);
                    if (book != null && listChildIds != null) {
                        if (listChildIds.contains(book.getCategory_id())){
                            dataList.add(book);
                            fullBookList.add(book);
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
        filterLauncher.launch(intent);
        overridePendingTransition(R.anim.slide_up, R.anim.no_anim);
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
    private final ActivityResultLauncher<Intent> filterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String tieuChiLocSort = result.getData().getStringExtra("GIA_TRI_LOC_SORT");
                    String tieuChiLocPrice = result.getData().getStringExtra("GIA_TRI_LOC_PRICE");
                    if (tieuChiLocSort == null && tieuChiLocPrice != null){
                        filterBooks(tieuChiLocPrice);
                    }
                    else if (tieuChiLocSort != null && tieuChiLocPrice == null){
                        sortBooks(tieuChiLocSort);
                    }
                    else if (tieuChiLocSort != null && tieuChiLocPrice != null){
                        filterBooks(tieuChiLocPrice);
                        sortBooks(tieuChiLocSort);
                    }else {
                        Toast.makeText(this, "Vui lòng chọn 1 tiêu chí!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void filterBooks(String tieuChiLoc) {
        dataList.clear();
        for (Book book : fullBookList) {
            double giaSach = book.getGia_Ban();
            if (tieuChiLoc.equals("Dưới 1.000.000₫")) {
                if (giaSach < 1000000) {
                    dataList.add(book);
                }
            } else if (tieuChiLoc.equals("100.000₫ - 300.000₫")) {
                if (giaSach >= 100000 && giaSach <= 300000) {
                    dataList.add(book);
                }
            } else if (tieuChiLoc.equals("300.000₫ - 500.000₫")) {
                if (giaSach >= 300000 && giaSach <= 500000) {
                    dataList.add(book);
                }
            } else if (tieuChiLoc.equals("500.000đ - 1.000.000₫")) {
                if (giaSach >= 500000 && giaSach <= 1000000) {
                    dataList.add(book);
                }
            } else if (tieuChiLoc.equals("Trên 1.000.000₫")) {
                if (giaSach > 1000000) {
                    dataList.add(book);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void sortBooks(String tieuChiLoc) {
        if (tieuChiLoc.equals("Giá: Tăng dần")) {
            Collections.sort(dataList, new Comparator<Book>() {
                @Override
                public int compare(Book b1, Book b2) {
                    return Double.compare(b1.getGia_Ban(), b2.getGia_Ban());
                }
            });
        }
        else if (tieuChiLoc.equals("Giá: Giảm dần")) {
            // Sắp xếp giảm dần theo giá
            Collections.sort(dataList, new Comparator<Book>() {
                @Override
                public int compare(Book b1, Book b2) {
                    return Double.compare(b2.getGia_Ban(), b1.getGia_Ban());
                }
            });
        }
        else if (tieuChiLoc.equals("Tên: A-Z")) {
            Collections.sort(dataList, new Comparator<Book>() {
                @Override
                public int compare(Book b1, Book b2) {
                    return b1.getTenSP().compareToIgnoreCase(b2.getTenSP());
                }
            });
        }
        else if (tieuChiLoc.equals("Tên: Z-A")) {
            Collections.sort(dataList, new Comparator<Book>() {
                @Override
                public int compare(Book b1, Book b2) {
                    return b2.getTenSP().compareToIgnoreCase(b1.getTenSP());
                }
            });
        }
        else if (tieuChiLoc.equals("Mới nhất")) {
            Collections.sort(dataList, new Comparator<Book>() {
                @Override
                public int compare(Book b1, Book b2) {
                    return Double.compare(b2.getMaSP(), b1.getMaSP());
                }
            });
        }
        else if (tieuChiLoc.equals("Cũ nhất")) {
            Collections.sort(dataList, new Comparator<Book>() {
                @Override
                public int compare(Book b1, Book b2) {
                    return Double.compare(b1.getMaSP(), b2.getMaSP());
                }
            });
        }
        adapter.notifyDataSetChanged();
    }
}