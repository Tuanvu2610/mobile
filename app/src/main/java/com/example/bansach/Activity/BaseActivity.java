package com.example.bansach.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;

public class BaseActivity extends AppCompatActivity {

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
    }
}
