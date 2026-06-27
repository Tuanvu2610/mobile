package com.example.bansach.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.CartItem;
import com.example.bansach.model.Order;

import java.util.List;

public class OrderHistoryAdapter extends BaseAdapter {
    List<Order> listOrder;
    Context context;

    public OrderHistoryAdapter(List<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
    }

    @Override
    public int getCount() {
        return (listOrder != null) ? listOrder.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (listOrder != null) ? listOrder.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_history_orders, parent, false);
        }
        Order order = listOrder.get(position);
        ImageView imgProduct = convertView.findViewById(R.id.imgProduct);
        List<CartItem> danhSachMonHang = order.getListItems();
        TextView tvProductName = convertView.findViewById(R.id.tvProductName);
        if (danhSachMonHang != null && !danhSachMonHang.isEmpty()) {
            CartItem monDauTien = danhSachMonHang.get(0);

            Glide.with(context)
                    .load(monDauTien.getImg())
                    .into(imgProduct);
            if (tvProductName != null) {
                tvProductName.setText(monDauTien.getTenSP());
            }
            TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
            TextView tvNewPrice = convertView.findViewById(R.id.tvNewPrice);
            tvQuantity.setText("x" + monDauTien.getSoLuong());
            tvNewPrice.setText(String.format("%,.0fđ", monDauTien.getGia_Ban()));
            tvNewPrice.setTypeface(null, Typeface.BOLD);
            TextView tvTotalItemLabel = convertView.findViewById(R.id.tvTotalItemLabel);
            TextView tvTotalPrice = convertView.findViewById(R.id.tvTotalPrice);
            tvTotalItemLabel.setText("Tổng số tiền ("+ danhSachMonHang.size()+" sản phẩm): ");
            tvTotalPrice.setText(String.format("%,.0fđ", order.getTotalAmount()));
        }
        return convertView;
    }
}
