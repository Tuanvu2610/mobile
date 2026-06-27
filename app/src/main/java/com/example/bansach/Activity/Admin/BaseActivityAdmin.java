package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;

public class BaseActivityAdmin extends AppCompatActivity {
    private TextView txtAdminTitle;
    protected void setupHeader() {
        txtAdminTitle = findViewById(R.id.txtAdminTitle);
        txtAdminTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivityAdmin.this, AdminActivity.class);
                Toast.makeText(BaseActivityAdmin.this, "Lỗi ", Toast.LENGTH_SHORT).show();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
