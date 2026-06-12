package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.example.bansach.R;

public class FilterActivity extends BaseActivity {
    Button buttonApply, buttonHuy;
    ImageView btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_choose_filter);
        setupHeader();
        buttonHuy = findViewById(R.id.btnCancel);
        buttonApply = findViewById(R.id.btnApply);
        btnClose = findViewById(R.id.btnClose);
        buttonApply.setOnClickListener(view -> {
            applyFiler();
        });
        buttonHuy.setOnClickListener(view -> {
            finish();
        });
        btnClose.setOnClickListener(view -> {
            finish();
        });

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_down);
    }
    public void applyFiler() {
        RadioGroup radioGroupSort = findViewById(R.id.rgSort);
        RadioGroup radioGroupPrice = findViewById(R.id.rgSortPrice);
        int selectedIdPrice = radioGroupPrice.getCheckedRadioButtonId();
        int selectedIdSort = radioGroupSort.getCheckedRadioButtonId();

        String tieuChiLocSort = null;
        String tieuChiLocPrice = null;

        if (selectedIdSort != -1) {
            RadioButton selectedRadioSort = findViewById(selectedIdSort);
            if (selectedRadioSort != null) {
                tieuChiLocSort = selectedRadioSort.getText().toString();
            }
        }

        if (selectedIdPrice != -1) {
            RadioButton selectedRadioPrice = findViewById(selectedIdPrice);
            if (selectedRadioPrice != null) {
                tieuChiLocPrice = selectedRadioPrice.getText().toString();
            }
        }

        if (tieuChiLocSort != null || tieuChiLocPrice != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("GIA_TRI_LOC_SORT", tieuChiLocSort);
            returnIntent.putExtra("GIA_TRI_LOC_PRICE", tieuChiLocPrice);
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            Toast.makeText(FilterActivity.this, "Vui lòng chọn 1 tiêu chí!", Toast.LENGTH_SHORT).show();
        }
    }

}
