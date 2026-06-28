package com.example.bansach.Activity.Admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.model.Account;
import com.example.bansach.model.User;
import com.example.bansach.model.UserDisplayItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUserManagementActivity extends AppCompatActivity {

    private LinearLayout lvUsers;
    private EditText edtSearchUser;
    private ImageButton btnFilter;
    private FloatingActionButton fabAddUser;

    private final List<UserDisplayItem> fullList = new ArrayList<>();
    private String currentKeyword = "";

    // Hiển thị "Admin"/"User" cho đẹp, nhưng ghi xuống Firebase dạng chữ thường
    private final String[] roles = {"Admin", "User"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_user_management);

        lvUsers = findViewById(R.id.lvUsers);
        edtSearchUser = findViewById(R.id.edtSearchUser);
        btnFilter = findViewById(R.id.btnFilter);
        fabAddUser = findViewById(R.id.fabAddUser);

        loadUsersFromFirebase();

        edtSearchUser.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString().trim();
                renderUserList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // TODO: gắn chức năng lọc thật cho btnFilter (lọc theo Role/Status) nếu cần
        btnFilter.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng lọc đang phát triển", Toast.LENGTH_SHORT).show());

        // TODO: gắn chức năng thêm user thật cho fabAddUser nếu cần
        fabAddUser.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng thêm người dùng đang phát triển", Toast.LENGTH_SHORT).show());
    }

    private void loadUsersFromFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference("accounts").child("accounts");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Map: account_id -> User, để ghép tên/email vào account tương ứng
                Map<String, User> userMap = new HashMap<>();
                for (DataSnapshot snap : userSnapshot.getChildren()) {
                    User u = snap.getValue(User.class);
                    if (u != null && u.getAccount_id() != null) {
                        userMap.put(u.getAccount_id(), u);
                    }
                }

                accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot accSnapshot) {
                        fullList.clear();

                        for (DataSnapshot snap : accSnapshot.getChildren()) {
                            Account acc = snap.getValue(Account.class);
                            if (acc == null || acc.getUser_id() == null) continue;

                            User u = userMap.get(acc.getUser_id());
                            String fullName = (u != null && u.getFullName() != null) ? u.getFullName() : acc.getUsername();
                            String email = (u != null && u.getEmail() != null) ? u.getEmail() : "";

                            fullList.add(new UserDisplayItem(
                                    acc.getUser_id(),
                                    fullName,
                                    email,
                                    acc.getRole(),
                                    acc.getStatus()
                            ));
                        }

                        renderUserList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminUserManagementActivity.this, "Lỗi tải account: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUserManagementActivity.this, "Lỗi tải user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // KHÚC NÀY ĐỂ VẼ DANH SÁCH USER LÊN MÀN HÌNH
    private void renderUserList() {
        List<UserDisplayItem> filtered = new ArrayList<>();
        for (UserDisplayItem item : fullList) {
            String name = item.getFullName() != null ? item.getFullName().toLowerCase() : "";
            String email = item.getEmail() != null ? item.getEmail().toLowerCase() : "";
            if (currentKeyword.isEmpty() ||
                    name.contains(currentKeyword.toLowerCase()) ||
                    email.contains(currentKeyword.toLowerCase())) {
                filtered.add(item);
            }
        }

        lvUsers.removeAllViews(); // Xóa rác cũ nếu có

        for (UserDisplayItem item : filtered) {
            View itemView = getLayoutInflater().inflate(R.layout.admin_item_user, lvUsers, false);

            TextView tvFullName = itemView.findViewById(R.id.tvFullName);
            TextView tvEmail = itemView.findViewById(R.id.tvEmail);
            Spinner spinnerRole = itemView.findViewById(R.id.spinnerRole);
            Switch switchStatus = itemView.findViewById(R.id.switchStatus);

            // Lôi data từng user ra gắn vào
            tvFullName.setText(item.getFullName());
            tvEmail.setText(item.getEmail());

            // --- Gắn Spinner vai trò ---
            ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
            roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRole.setAdapter(roleAdapter);

            int rolePos = (item.getRole() != null && item.getRole().equalsIgnoreCase("admin")) ? 0 : 1;
            spinnerRole.setSelection(rolePos);

            spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    String selectedRole = roles[pos].toLowerCase(); // "admin" hoặc "user"
                    if (!selectedRole.equalsIgnoreCase(item.getRole())) {
                        item.setRole(selectedRole);
                        updateRoleOnFirebase(item.getId(), selectedRole);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // --- Gắn Switch trạng thái ---
            boolean isActive = "active".equalsIgnoreCase(item.getStatus());
            switchStatus.setChecked(isActive);

            switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String newStatus = isChecked ? "active" : "inactive";
                item.setStatus(newStatus);
                updateStatusOnFirebase(item.getId(), newStatus);
            });

            // Dọng nó vào giao diện
            lvUsers.addView(itemView);
        }
    }

    private void updateRoleOnFirebase(String userId, String newRole) {
        FirebaseDatabase.getInstance().getReference("accounts").child("accounts")
                .child(userId).child("role").setValue(newRole);
        Toast.makeText(this, "Đã đổi vai trò: " + newRole, Toast.LENGTH_SHORT).show();
    }

    private void updateStatusOnFirebase(String userId, String newStatus) {
        FirebaseDatabase.getInstance().getReference("accounts").child("accounts")
                .child(userId).child("status").setValue(newStatus);
    }
}