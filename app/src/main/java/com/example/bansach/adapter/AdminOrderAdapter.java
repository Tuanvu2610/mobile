package com.example.bansach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bansach.R;
import com.example.bansach.model.Order;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener;

    // Interface để lát Activity bắt sự kiện bật Bottom Sheet
    public interface OnOrderClickListener {
        void onDetailClick(Order order);
    }

    public AdminOrderAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Mã: " + order.getOrderId().substring(0, 8));
        holder.tvStatus.setText(order.getStatus());
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvPhone.setText(order.getCustomerPhone());
        holder.tvDate.setText(order.getOrderDate());
        holder.tvTotal.setText(String.format(Locale.US, "%,.0f đ", order.getTotalAmount()));

        // Bấm nút Chi Tiết
        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) listener.onDetailClick(order);
        });
    }

    @Override
    public int getItemCount() { return orderList != null ? orderList.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvCustomerName, tvPhone, tvDate, tvTotal, btnDetail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotal = itemView.findViewById(R.id.tv_total);
            btnDetail = itemView.findViewById(R.id.btn_detail);
        }
    }
}