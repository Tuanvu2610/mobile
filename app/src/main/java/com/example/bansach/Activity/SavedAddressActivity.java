package com.example.bansach.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bansach.R;
import com.example.bansach.adapter.AddressAdapter;
import com.example.bansach.model.Address;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedAddressActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvAddresses;
    private TextView tvEmpty;
    private Button btnAddAddress;

    private SessionManager sessionManager;
    private String userId;
    private DatabaseReference addressRef;

    private final List<Address> addressList = new ArrayList<>();
    private AddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_address);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = sessionManager.getUserId();
        addressRef = FirebaseDatabase.getInstance().getReference("addresses");

        initViews();
        setupRecyclerView();
        loadAddresses();

        ivBack.setOnClickListener(v -> finish());
        btnAddAddress.setOnClickListener(v -> showAddAddressDialog());
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        rvAddresses = findViewById(R.id.rvAddresses);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnAddAddress = findViewById(R.id.btnAddAddress);
    }

    private void setupRecyclerView() {
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addressList, new AddressAdapter.OnAddressActionListener() {
            @Override
            public void onSetDefault(Address address) {
                setDefaultAddress(address);
            }

            @Override
            public void onDelete(Address address) {
                confirmDeleteAddress(address);
            }
        });
        rvAddresses.setAdapter(adapter);
    }

    private void loadAddresses() {
        addressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                addressList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Address address = child.getValue(Address.class);
                    if (address != null && userId.equals(address.getAccountId())) {
                        addressList.add(address);
                    }
                }
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(addressList.isEmpty() ? View.VISIBLE : View.GONE);
                rvAddresses.setVisibility(addressList.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SavedAddressActivity.this,
                        "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Đặt 1 địa chỉ làm mặc định -> tự bỏ mặc định ở tất cả địa chỉ còn lại
    private void setDefaultAddress(Address selected) {
        Map<String, Object> updates = new HashMap<>();
        for (Address address : addressList) {
            boolean isSelected = address.getAddressId().equals(selected.getAddressId());
            updates.put("/" + address.getAddressId() + "/defaultAddress", isSelected);
        }
        addressRef.updateChildren(updates)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void confirmDeleteAddress(Address address) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc muốn xóa địa chỉ này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteAddress(address))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAddress(Address address) {
        addressRef.child(address.getAddressId()).removeValue()
                .addOnSuccessListener(v -> {
                    // Nếu vừa xóa địa chỉ đang là mặc định, tự đặt 1 địa chỉ khác làm mặc định
                    if (address.isDefaultAddress()) {
                        for (Address a : addressList) {
                            if (!a.getAddressId().equals(address.getAddressId())) {
                                setDefaultAddress(a);
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showAddAddressDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 0);

        EditText edtName = new EditText(this);
        edtName.setHint("Họ tên người nhận");

        EditText edtPhone = new EditText(this);
        edtPhone.setHint("Số điện thoại");
        edtPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        EditText edtDetail = new EditText(this);
        edtDetail.setHint("Địa chỉ chi tiết");

        container.addView(edtName);
        container.addView(edtPhone);
        container.addView(edtDetail);

        new AlertDialog.Builder(this)
                .setTitle("Thêm địa chỉ mới")
                .setView(container)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = edtName.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();
                    String detail = edtDetail.getText().toString().trim();

                    if (name.isEmpty() || phone.isEmpty() || detail.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveNewAddress(name, phone, detail);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveNewAddress(String name, String phone, String detail) {
        String newId = addressRef.push().getKey();
        if (newId == null) return;

        // Nếu đây là địa chỉ đầu tiên của user -> tự động mặc định luôn
        boolean isFirst = addressList.isEmpty();

        Address newAddress = new Address(newId, userId, name, phone, detail, isFirst);
        addressRef.child(newId).setValue(newAddress)
                .addOnSuccessListener(v ->
                        Toast.makeText(this, "Đã thêm địa chỉ", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}