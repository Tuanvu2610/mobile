package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;

public class AdminActivity extends AppCompatActivity {

    private LinearLayout quanlysanpham;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        quanlysanpham = findViewById(R.id.quanlysanpham);
        quanlysanpham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageBookActivity.class);
                startActivity(intent);

            }
        });

    }
}
