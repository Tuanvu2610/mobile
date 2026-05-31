package com.example.bansach.Activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class BaseActivity extends AppCompatActivity {
    List<Book> filterList;
    List<Book> fullBookList;
    private BookGridAdapter adapter;
    protected void setupHeader() {
        View headerView = findViewById(R.id.layoutMenu);
        if (headerView != null) {
            LinearLayout btnMenu = headerView.findViewById(R.id.layoutMenu);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, CategoryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.stay_still);
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

    }
    public void filterProduct(String keyword){
        filterList.clear();
        if(keyword.isEmpty()) {
            filterList.addAll(fullBookList);
        }
        else {
            for(Book p : fullBookList){
                if(p.getTenSP().toLowerCase().contains(keyword.toLowerCase())){
                    filterList.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
