package com.example.bansach.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.Voucher;

import java.util.List;

public class VoucherAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Voucher> voucherList;

    public VoucherAdapter(List<Voucher> voucherList, int layout, Context context) {
        this.voucherList = voucherList;
        this.layout = layout;
        this.context = context;
    }

    @Override
    public int getCount() {
        return voucherList.size();
    }

    @Override
    public Object getItem(int position) {
        return voucherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
        }
        ImageView imgVoucher = convertView.findViewById(R.id.imgVoucher);
        TextView tvVoucher = convertView.findViewById(R.id.tvVoucher);
        TextView tvCondition = convertView.findViewById(R.id.tvCondition);
        TextView tvVoucherCode = convertView.findViewById(R.id.tvVoucherCode);

        Voucher voucher = voucherList.get(position);

        tvVoucher.setText(voucher.getTieuDe());
        tvCondition.setText(voucher.getDieuKien());
        tvVoucherCode.setText("Mã: " + voucher.getMaVoucher());

        Glide.with(context).load(voucher.getHinhAnh()).into(imgVoucher);
        return convertView;
    }
}
