package com.example.bansach.Activity.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.model.Category;
import com.example.bansach.adapter.AdminCategoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryActivity extends BaseActivityAdmin {

    private Button btnAddCategory;
    private RecyclerView rvCategory;
    private DatabaseReference categoryRef;

    private final List<Category> categoryList = new ArrayList<>();
    private final List<Category> displayList = new ArrayList<>();
    private final List<Category> parentList = new ArrayList<>();

    private AdminCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_category);

        btnAddCategory = findViewById(R.id.btnAddCategory);
        rvCategory = findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(
                new LinearLayoutManager(this));

        categoryRef = FirebaseDatabase.getInstance()
                .getReference("category/category");

        adapter = new AdminCategoryAdapter(
                this,
                displayList,
                categoryList,
                new AdminCategoryAdapter.OnCategoryAction() {
                    @Override
                    public void onEdit(Category category) {
                        showEditDialog(category);
                    }

                    @Override
                    public void onDelete(Category category) {
                        deleteCategory(category);
                    }
                });

        rvCategory.setAdapter(adapter);

        loadCategory();
        setupHeader();
        btnAddCategory.setOnClickListener(v -> showAddDialog());
    }

    private void loadCategory() {

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                categoryList.clear();
                displayList.clear();
                parentList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    Category category =
                            data.getValue(Category.class);

                    if (category != null) {
                        categoryList.add(category);

                        // chỉ hiển thị danh mục cha
                        if (category.getLink() != null &&
                                !category.getLink().trim().isEmpty()) {

                            parentList.add(category);
                            displayList.add(category);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void deleteCategory(Category category) {
        categoryList.remove(category);
        for (int i = categoryList.size() - 1; i >= 0; i--) {
            if (categoryList.get(i).getParent_id()
                    == category.getCategory_id()) {
                categoryList.remove(i);
            }
        }
        categoryRef.setValue(categoryList)
                .addOnSuccessListener(unused ->
                        Toast.makeText(
                                this,
                                "Xóa thành công",
                                Toast.LENGTH_SHORT
                        ).show());
    }


    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.admin_add_category, null);

        EditText edtName = view.findViewById(R.id.edtCategoryName);
        EditText edtLink = view.findViewById(R.id.edtLink);

        Spinner spParent = view.findViewById(R.id.spParent);

        RadioButton rbParent = view.findViewById(R.id.rbParent);
        RadioButton rbChild = view.findViewById(R.id.rbChild);

        ArrayList<String> parentName = new ArrayList<>();

        for (Category c : parentList) {
            parentName.add(c.getName_cate());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, parentName);

        spParent.setAdapter(adapter);
        rbParent.setChecked(true);

        edtLink.setVisibility(View.VISIBLE);
        spParent.setVisibility(View.GONE);

        rbParent.setOnClickListener(v -> {
            edtLink.setVisibility(View.VISIBLE);
            spParent.setVisibility(View.GONE);
        });

        rbChild.setOnClickListener(v -> {
            edtLink.setVisibility(View.GONE);
            spParent.setVisibility(View.VISIBLE);
        });

        new AlertDialog.Builder(this).setTitle("Thêm danh mục")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {

                    String name = edtName.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this,
                                "Nhập tên danh mục",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int maxId = 0;

                    for (Category c : categoryList) {
                        if (c.getCategory_id() > maxId) {
                            maxId = c.getCategory_id();
                        }

                    }

                    Category newCategory = new Category();

                    newCategory.setCategory_id(maxId + 1);
                    newCategory.setName_cate(name);
                    newCategory.setExpanded(false);

                    if (rbParent.isChecked()) {
                        String link = edtLink.getText().toString().trim();
                        if (link.isEmpty()) {
                            link = "Category" + (maxId + 1);
                        }
                        newCategory.setLink(link);
                    } else {

                        if (parentList.isEmpty()) {
                            Toast.makeText(
                                    this,
                                    "Chưa có danh mục cha để chọn",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        int pos = spParent.getSelectedItemPosition();
                        Category parent = parentList.get(pos);
                        newCategory.setParent_id(parent.getCategory_id());
                    }

                    categoryList.add(newCategory);
                    categoryRef.setValue(categoryList)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(
                                            this,
                                            "Thêm thành công",
                                            Toast.LENGTH_SHORT
                                    ).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            this,
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditDialog(Category category) {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.admin_add_category, null);

        EditText edtName =
                view.findViewById(R.id.edtCategoryName);

        edtName.setText(category.getName_cate());

        new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {

                    String name =
                            edtName.getText()
                                    .toString()
                                    .trim();

                    if (name.isEmpty()) {
                        Toast.makeText(
                                this,
                                "Nhập tên",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    category.setName_cate(name);

                    categoryRef.setValue(categoryList)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(
                                            this,
                                            "Cập nhật thành công",
                                            Toast.LENGTH_SHORT
                                    ).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}