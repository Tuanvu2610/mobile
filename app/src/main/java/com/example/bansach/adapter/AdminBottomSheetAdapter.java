package com.example.bansach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.CartItem;
import java.util.List;
import java.util.Locale;

public class AdminBottomSheetAdapter extends RecyclerView.Adapter<AdminBottomSheetAdapter.ViewHolder> {
    private Context context;
    private List<CartItem> listItems;

    public AdminBottomSheetAdapter(Context context, List<CartItem> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_bottom_sheet_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = listItems.get(position);
        holder.tvName.setText(item.getTenSP());
        holder.tvQty.setText("x" + item.getSoLuong());
        holder.tvPrice.setText(String.format(Locale.US, "%,.0f đ", item.getGia_Ban()));


        Glide.with(context).load(item.getImg()).into(holder.imgBook);
    }

    @Override
    public int getItemCount() {
        return listItems != null ? listItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView tvName, tvQty, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgAdminBook);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvQty = itemView.findViewById(R.id.tv_product_qty);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }
}