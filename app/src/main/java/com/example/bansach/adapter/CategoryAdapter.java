package com.example.bansach.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bansach.R;
import com.example.bansach.model.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    List<Category> displayList;
    Context context;

    public CategoryAdapter(List<Category> displayList, Context context) {
        this.displayList = displayList;
        this.context = context;
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
        return convertView;
    }
}
