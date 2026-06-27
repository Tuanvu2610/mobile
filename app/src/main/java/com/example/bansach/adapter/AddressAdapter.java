package com.example.bansach.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bansach.R;
import com.example.bansach.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    public interface OnAddressActionListener {
        void onSetDefault(Address address);
        void onDelete(Address address);
    }

    private final List<Address> addressList;
    private final OnAddressActionListener listener;

    public AddressAdapter(List<Address> addressList, OnAddressActionListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);

        holder.tvName.setText(address.getName());
        holder.tvPhone.setText(address.getPhone());
        holder.tvDetail.setText(address.getDetail());

        holder.rbDefault.setChecked(address.isDefaultAddress());
        holder.tvDefaultLabel.setVisibility(address.isDefaultAddress() ? View.VISIBLE : View.GONE);

        // Bấm vào cả dòng (trừ icon xóa) -> đặt làm mặc định
        holder.itemView.setOnClickListener(v -> {
            if (!address.isDefaultAddress()) {
                listener.onSetDefault(address);
            }
        });

        holder.ivDelete.setOnClickListener(v -> listener.onDelete(address));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton rbDefault;
        TextView tvName, tvPhone, tvDetail, tvDefaultLabel;
        ImageView ivDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rbDefault = itemView.findViewById(R.id.rbDefault);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDetail = itemView.findViewById(R.id.tvDetail);
            tvDefaultLabel = itemView.findViewById(R.id.tvDefaultLabel);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}