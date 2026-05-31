package com.example.bansach.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bansach.Activity.ListProductActivity;
import com.example.bansach.R;
import com.example.bansach.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    List<Category> displayList, listSubCate;
    Context context;

    public CategoryAdapter(List<Category> displayList, List<Category> listSubCate, Context context) {
        this.displayList = displayList;
        this.context = context;
        this.listSubCate = listSubCate;
    }

    @Override
    public int getCount() {
        return displayList.size();
    }

    @Override
    public Object getItem(int position) {
        return displayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_cate, parent, false);
        }
        TextView text = convertView.findViewById(R.id.txtCate);
        ImageView btnExpand = convertView.findViewById(R.id.btnExpand);
        btnExpand.setOnClickListener(v -> {
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
                notifyDataSetChanged();
            }
        });
        Category cate = displayList.get(position);
        text.setText(cate.getName_cate());
        if (cate.getLink() != null) {
            text.setTextSize(16);
            text.setTypeface(null, Typeface.BOLD);
            text.setPadding(40,40,40,40);
        } else {
            text.setTextSize(14);
            text.setPadding(120,40,40,40);
        }
        Intent intent = new Intent(context, ListProductActivity.class);
        convertView.setOnClickListener(v -> {
            ArrayList<Integer> listChildIds = new ArrayList<>();
            if (cate.getLink() == null) {
                listChildIds.add(cate.getCategory_id());
                intent.putIntegerArrayListExtra("LIST_CHILD_IDS", listChildIds);
                context.startActivity(intent);
            }
            else {
                listChildIds.add(cate.getCategory_id());
                for (Category item : listSubCate) {
                    if (item.getParent_id() == cate.getCategory_id()) {
                        listChildIds.add(item.getCategory_id());
                    }
                }
                intent.putIntegerArrayListExtra("LIST_CHILD_IDS", listChildIds);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
