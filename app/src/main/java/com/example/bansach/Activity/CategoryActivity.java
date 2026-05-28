package com.example.bansach.Activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.adapter.CategoryAdapter;
import com.example.bansach.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private ListView lvCategory;
    private CategoryAdapter adapter;
    private List<Category> listParentCate, listSubCate, displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_side_menu);

        lvCategory = findViewById(R.id.lvCategory);
        listParentCate = new ArrayList<>();
        listSubCate = new ArrayList<>();
        displayList = new ArrayList<>();

        adapter = new CategoryAdapter(displayList, this);
        lvCategory.setAdapter(adapter);

        fetchOnlineDataCategory();

        lvCategory.setOnItemClickListener((parent, view, position, id) -> {
            Category cate = displayList.get(position);
            if (cate.getLink() != null && !cate.getLink().trim().isEmpty()) {

                if (!cate.isExpanded()) {
                    // mở submenu
                    int index = position + 1;
                    for (Category sub : listSubCate) {
                        if (sub.getParent_id() == cate.getCategory_id()) {
                            displayList.add(index, sub);
                            index++;
                        }
                    }
                    cate.setExpanded(true);
                } else {
                    // Thu submenu
                    for (int i = displayList.size() - 1; i >= 0; i--) {
                        Category item = displayList.get(i);
                        if (item.getParent_id() == cate.getCategory_id()) {
                            displayList.remove(i);
                        }
                    }
                    cate.setExpanded(false);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchOnlineDataCategory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoryRef = database.getReference("category/category");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listParentCate.clear();
                listSubCate.clear();
                displayList.clear();

                for (DataSnapshot cateSnap : snapshot.getChildren()) {
                    Category cate = cateSnap.getValue(Category.class);
                    if (cate != null) {
                        if (cate.getLink() != null && !cate.getLink().isEmpty()) {
                            listParentCate.add(cate);
                        } else {
                            listSubCate.add(cate);
                        }
                    }
                }

                displayList.addAll(listParentCate);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CategoryActivity.this, "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}