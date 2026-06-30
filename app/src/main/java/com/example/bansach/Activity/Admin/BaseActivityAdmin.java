package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.Activity.BaseActivity;
import com.example.bansach.R;
import com.example.bansach.adapter.CategoryAdapter;
import com.example.bansach.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BaseActivityAdmin extends AppCompatActivity {
    private TextView txtAdminTitle;
    private ImageView btnAdminMenu;

    protected void setupHeader() {
        txtAdminTitle = findViewById(R.id.txtAdminTitle);
        txtAdminTitle.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivityAdmin.this, AdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        btnAdminMenu = findViewById(R.id.btnAdminMenu);
        boolean isadmin = getIntent().getBooleanExtra("is_admin", false);
        if (isadmin){
            btnAdminMenu.setImageResource(R.drawable.ic_back);
            btnAdminMenu.setOnClickListener(v -> {
                finish();
            });
        }
    }
}
