package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.adapter.VoucherAdapter;
import com.example.bansach.model.Voucher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity {
    ListView lvVoucher;
    VoucherAdapter adapter;
    List<Voucher> voucherList;
    Button btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vouher);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lvVoucher =findViewById(R.id.lvVoucher);
        btnApply = findViewById(R.id.btnApply);

        voucherList = new ArrayList<>();
        adapter = new VoucherAdapter(voucherList, R.layout.item_voucher, this);
        lvVoucher.setAdapter(adapter);

        btnApply.setOnClickListener(v -> {

            Voucher voucher = adapter.getSelectedVoucher();
            if (voucher == null) {
                Toast.makeText(this, "Vui lòng chọn voucher", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent data = new Intent();
            data.putExtra("discount", voucher.getGiaTriGiam());

            setResult(RESULT_OK, data);
            finish();
        });

        layDuLieuTuFirebase();
    }


    private void layDuLieuTuFirebase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("vouchers/vouchers");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                voucherList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Voucher voucher = dataSnapshot.getValue(Voucher.class);
                    if (voucher != null) {
                        voucherList.add(voucher);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi kéo db: " + error.getMessage());

            }
        });
    }

}
