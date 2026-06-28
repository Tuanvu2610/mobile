package com.example.bansach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bansach.R;
import com.example.bansach.model.Category;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    public interface OnCategoryAction {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    private Context context;
    private List<Category> displayList;
    private List<Category> categoryList;
    private OnCategoryAction listener;

    public AdminCategoryAdapter(Context context,List<Category> displayList,
                                List<Category> categoryList,
                                OnCategoryAction listener) {
        this.context = context;
        this.displayList = displayList;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgExpand, imgEdit, imgDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            imgExpand = itemView.findViewById(R.id.imgExpand);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.admin_item_category,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Category category = displayList.get(position);

        holder.txtName.setText(category.getName_cate());

        if (category.getParent_id() != 0) {
            holder.txtName.setPadding(120, 0, 0, 0);
            holder.imgExpand.setVisibility(View.INVISIBLE);
        } else {
            holder.txtName.setPadding(40, 0, 0, 0);
            holder.imgExpand.setVisibility(View.VISIBLE);
        }

        holder.imgEdit.setOnClickListener(v ->
                listener.onEdit(category));

        holder.imgDelete.setOnClickListener(v ->
                listener.onDelete(category));

        holder.imgExpand.setOnClickListener(v -> {

            if (category.getParent_id() != 0) {
                return;
            }

            if (!category.isExpanded()) {

                int index = position + 1;

                for (Category sub : categoryList) {
                    if (sub.getParent_id() == category.getCategory_id()) {
                        displayList.add(index, sub);
                        index++;
                    }
                }

                category.setExpanded(true);

            } else {

                for (int i = displayList.size() - 1; i >= 0; i--) {
                    Category item = displayList.get(i);

                    if (item.getParent_id() == category.getCategory_id()) {
                        displayList.remove(i);
                    }
                }

                category.setExpanded(false);
            }

            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }
}