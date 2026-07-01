package com.example.bansach.Activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.adapter.BookGridAdapter;
import com.example.bansach.adapter.CategoryAdapter;
import com.example.bansach.model.Book;
import com.example.bansach.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    List<Book> filterList;
    List<Book> fullBookList;
    private BookGridAdapter adapter;
    private List<Category> listParentCate, listSubCate, displayList;
    private CategoryAdapter adapterCategory;

    protected void setupHeader() {
        ListView lvCategory = findViewById(R.id.lvCategory);
        if (lvCategory != null) {
            listParentCate = new ArrayList<>();
            listSubCate = new ArrayList<>();
            displayList = new ArrayList<>();
            adapterCategory = new CategoryAdapter(displayList, listSubCate, this);
            lvCategory.setAdapter(adapterCategory);
            fetchOnlineDataCategory();
        }

        ImageView imgUser = findViewById(R.id.imgUser);
        if (imgUser != null) {
            imgUser.setOnClickListener(v -> {
                SessionManager sessionManager = new SessionManager(BaseActivity.this);
                if (sessionManager.isLoggedIn()) {
                    Toast.makeText(BaseActivity.this, "đã đăng nhập", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(BaseActivity.this, "chưa đăng nhập", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }

        View btnMenuHeader = findViewById(R.id.layoutMenu);
        ImageView imgIconMenu = findViewById(R.id.imgMenuIcon);
        View layoutSearchBar = findViewById(R.id.layoutSearchBar);
        View layoutDropdownMenu = findViewById(R.id.layoutDropdownMenu);
        View layoutMainContent = findViewById(R.id.layoutMainContent);

        final boolean[] isMenuOpen = {false};
        if (btnMenuHeader != null) {
            btnMenuHeader.setOnClickListener(v -> {
                if (!isMenuOpen[0]) {
                    if (layoutSearchBar != null) layoutSearchBar.setVisibility(View.GONE);
                    if (layoutMainContent != null) layoutMainContent.setVisibility(View.GONE);
                    if (layoutDropdownMenu != null) layoutDropdownMenu.setVisibility(View.VISIBLE);
                    if (imgIconMenu != null) imgIconMenu.setImageResource(R.drawable.ic_close);
                    isMenuOpen[0] = true;
                } else {
                    if (layoutSearchBar != null) layoutSearchBar.setVisibility(View.VISIBLE);
                    if (layoutDropdownMenu != null) layoutDropdownMenu.setVisibility(View.GONE);
                    if (layoutMainContent != null) layoutMainContent.setVisibility(View.VISIBLE);
                    if (imgIconMenu != null) imgIconMenu.setImageResource(R.drawable.ic_menu);
                    isMenuOpen[0] = false;
                }
            });
        }

        TextView txtSeach = findViewById(R.id.txtSeach);
        if (txtSeach != null) {
            txtSeach.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        ImageView imageCart = findViewById(R.id.imgCart);
        if (imageCart != null) {
            imageCart.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
            });
        }

        ImageView imgLike = findViewById(R.id.imgLike);
        if (imgLike != null) {
            imgLike.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, FavoriteActivity.class);
                startActivity(intent);
            });
        }
        TextView imgLogo = findViewById(R.id.imgLogo);

        if (imgLogo != null) {
            imgLogo.setOnClickListener(v -> {
                Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
        capNhatBadgeGioHang();
    }

    public void filterProduct(String keyword) {
        filterList.clear();
        if (keyword.isEmpty()) {
            filterList.addAll(fullBookList);
        } else {
            for (Book p : fullBookList) {
                if (p.getTenSP().toLowerCase().contains(keyword.toLowerCase())) {
                    filterList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void fetchOnlineDataCategory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoryRef = database.getReference("category/category");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listParentCate.clear();
                listSubCate.clear();
                displayList.clear();

                for (DataSnapshot cateSnap : snapshot.getChildren()) {
                    Category cate = cateSnap.getValue(Category.class);
                    if (cate != null) {
                        if (cate.getLink() != null && !cate.getLink().isEmpty()) {
                            listParentCate.add(cate);
                        } else {
                            listSubCate.add(cate);
                        }
                    }
                }

                displayList.addAll(listParentCate);
                adapterCategory.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BaseActivity.this,
                        "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void capNhatBadgeGioHang() {
        TextView tvCartCount = findViewById(R.id.tvCartCount);
        if (tvCartCount == null) return;

        SessionManager sessionManager = new SessionManager(this);
        String uId = sessionManager.getUserId();

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(uId);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalCount = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer qty = child.child("soLuong").getValue(Integer.class);
                    if (qty != null) totalCount += qty;
                }
                tvCartCount.setVisibility(View.VISIBLE);
                tvCartCount.setText(String.valueOf(totalCount));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}