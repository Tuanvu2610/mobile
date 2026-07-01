package com.example.bansach.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
        initViews();
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = sessionManager.getUserId();
        addressRef = FirebaseDatabase.getInstance().getReference("addresses");
        boolean isDetail = getIntent().getBooleanExtra("is_detail_address", false);
        if (isDetail) {
            btnAddAddress.setText("Xác nhận");
            btnAddAddress.setOnClickListener(v -> {
                Address selectedAddress = null;
                for (Address addr : addressList) {
                    if (addr.isDefaultAddress()) {
                        selectedAddress = addr;
                        break;
                    }
                }
                if (selectedAddress != null) {
                    Intent data = new Intent();
                    data.putExtra("name_user", selectedAddress.getName());
                    data.putExtra("phone_user", selectedAddress.getPhone());
                    data.putExtra("dc", selectedAddress.getDetail());
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(SavedAddressActivity.this, "Vui lòng chọn một địa chỉ!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            btnAddAddress.setText("Thêm địa chỉ mới");
            btnAddAddress.setOnClickListener(v -> showAddAddressDialog());
        }
        setupRecyclerView();
        loadAddresses();

        ivBack.setOnClickListener(v -> finish());
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

    private void setDefaultAddress(Address selected) {
        Map<String, Object> updates = new HashMap<>();
        for (Address address : addressList) {
            boolean isSelected = address.getAddressId().equals(selected.getAddressId());
            updates.put("/" + address.getAddressId() + "/defaultAddress", isSelected);

        }
        addressRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Intent data = new Intent();
                    data.putExtra("name_user", selected.getName());
                    data.putExtra("phone_user", selected.getPhone());
                    data.putExtra("dc", selected.getDetail());

                    setResult(RESULT_OK, data);
                    finish();
                })
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

        boolean isFirst = addressList.isEmpty();

        Address newAddress = new Address(newId, userId, name, phone, detail, isFirst);
        addressRef.child(newId).setValue(newAddress)
                .addOnSuccessListener(v ->
                        Toast.makeText(this, "Đã thêm địa chỉ", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}