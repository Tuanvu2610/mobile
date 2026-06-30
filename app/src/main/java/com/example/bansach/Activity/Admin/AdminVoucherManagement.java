package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.adapter.AdminBookAdapter;
import com.example.bansach.adapter.VoucherAdminAdapter;
import com.example.bansach.model.Book;
import com.example.bansach.model.Voucher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminVoucherManagement extends BaseActivityAdmin {
    private ListView lvVouchers;
    private VoucherAdminAdapter voucherAdapter;
    private DatabaseReference vouchersRef;
    private List<Voucher> listVoucher, filterList;
    private Button btnAddVoucher;
    private EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout_voucher_management);
        vouchersRef = FirebaseDatabase.getInstance().getReference("vouchers/vouchers");
        lvVouchers = findViewById(R.id.lvVoucher);
        listVoucher = new ArrayList<>();
        filterList = new ArrayList<>();

        voucherAdapter = new VoucherAdminAdapter(listVoucher, R.layout.item_voucher_admin,AdminVoucherManagement.this);
        lvVouchers.setAdapter(voucherAdapter);
        btnAddVoucher = findViewById(R.id.btnAddVoucher);
        btnAddVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(AdminVoucherManagement.this, AdminAddVoucherActivity.class);
            startActivity(intent);
        });
        searchText = findViewById(R.id.edtSearchVoucher);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gọi hàm lọc và cắt khoảng trắng 2 đầu
                filterProduct(s.toString().trim());
            }
        });
        setupHeader();
        loadVoucherFromFirebase();
    }
    private void loadVoucherFromFirebase() {
        vouchersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listVoucher.clear();
                filterList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot bookSnap : snapshot.getChildren()) {
                        Voucher voucher = bookSnap.getValue(Voucher.class);
                        if (voucher != null) {
                            listVoucher.add(voucher);
                            filterList.add(voucher);
                        }
                    }
                }
                voucherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminVoucherManagement.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filterProduct(String keyword) {
        filterList.clear();
        if (keyword.isEmpty()) {
            filterList.addAll(listVoucher);
        } else {
            for (Voucher p : listVoucher) {
                if (p.getTieuDe() != null && p.getTieuDe().toLowerCase().contains(keyword.toLowerCase())) {
                    filterList.add(p);
                }
            }
        }
        voucherAdapter.notifyDataSetChanged();
    }
}
