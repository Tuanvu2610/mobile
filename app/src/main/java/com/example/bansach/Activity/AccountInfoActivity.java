package com.example.bansach.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AccountInfoActivity extends AppCompatActivity {

    private ImageView ivBack;
    private LinearLayout layoutFullName, layoutBirthday, layoutGender, layoutPhone, layoutEmail;
    private TextView tvFullNameValue, tvBirthdayValue, tvGenderValue, tvPhoneValue, tvEmailValue;
    private Button btnLogout;

    private SessionManager sessionManager;
    private DatabaseReference userRef;
    private User currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = sessionManager.getUserId();

        initViews();
        loadUserInfo();
        setupClickListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        layoutFullName = findViewById(R.id.layoutFullName);
        layoutBirthday = findViewById(R.id.layoutBirthday);
        layoutGender = findViewById(R.id.layoutGender);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutEmail = findViewById(R.id.layoutEmail);

        tvFullNameValue = findViewById(R.id.tvFullNameValue);
        tvBirthdayValue = findViewById(R.id.tvBirthdayValue);
        tvGenderValue = findViewById(R.id.tvGenderValue);
        tvPhoneValue = findViewById(R.id.tvPhoneValue);
        tvEmailValue = findViewById(R.id.tvEmailValue);

        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserInfo() {
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                if (currentUser == null) return;

                if (currentUser.getFullName() != null && !currentUser.getFullName().isEmpty())
                    tvFullNameValue.setText(currentUser.getFullName());

                if (currentUser.getBirthDay() != null && !currentUser.getBirthDay().isEmpty())
                    tvBirthdayValue.setText(currentUser.getBirthDay());

                if (currentUser.getGender() != null && !currentUser.getGender().isEmpty())
                    tvGenderValue.setText(currentUser.getGender());

                if (currentUser.getSdt() != null && !currentUser.getSdt().isEmpty())
                    tvPhoneValue.setText(currentUser.getSdt());

                if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty())
                    tvEmailValue.setText(currentUser.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AccountInfoActivity.this,
                        "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        layoutFullName.setOnClickListener(v ->
                showEditTextDialog("Họ và tên",
                        currentUser != null ? currentUser.getFullName() : "",
                        InputType.TYPE_CLASS_TEXT,
                        newValue -> {
                            updateField("fullName", newValue);
                            tvFullNameValue.setText(newValue);
                        }));

        layoutPhone.setOnClickListener(v ->
                showEditTextDialog("Số điện thoại",
                        currentUser != null ? currentUser.getSdt() : "",
                        InputType.TYPE_CLASS_PHONE,
                        newValue -> {
                            updateField("sdt", newValue);
                            tvPhoneValue.setText(newValue);
                        }));

        layoutEmail.setOnClickListener(v ->
                showEditTextDialog("Email",
                        currentUser != null ? currentUser.getEmail() : "",
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                        newValue -> {
                            updateField("email", newValue);
                            tvEmailValue.setText(newValue);
                        }));

        layoutBirthday.setOnClickListener(v -> showDatePickerDialog());

        layoutGender.setOnClickListener(v -> showGenderDialog());

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(AccountInfoActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showEditTextDialog(String title, String currentValue, int inputType, OnValueSaved callback) {
        EditText input = new EditText(this);
        input.setInputType(inputType);
        if (currentValue != null) input.setText(currentValue);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newValue = input.getText().toString().trim();
                    if (!newValue.isEmpty()) {
                        callback.onSaved(newValue);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
            updateField("birthDay", dateStr);
            tvBirthdayValue.setText(dateStr);
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showGenderDialog() {
        String[] options = {"Nam", "Nữ", "Khác"};
        new AlertDialog.Builder(this)
                .setTitle("Giới tính")
                .setItems(options, (dialog, which) -> {
                    String selected = options[which];
                    updateField("gender", selected);
                    tvGenderValue.setText(selected);
                })
                .show();
    }

    private void updateField(String field, String value) {
        userRef.child(field).setValue(value)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private interface OnValueSaved {
        void onSaved(String value);
    }
}