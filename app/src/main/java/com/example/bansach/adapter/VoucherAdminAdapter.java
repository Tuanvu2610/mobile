package com.example.bansach.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.bansach.Activity.Admin.AdminAddVoucherActivity;
import com.example.bansach.Activity.Admin.AdminProductDetailManager;
import com.example.bansach.Activity.Admin.AdminVoucherManagement;
import com.example.bansach.Activity.ShoppingCartActivity;
import com.example.bansach.R;
import com.example.bansach.model.Voucher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class VoucherAdminAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Voucher> voucherList;
    private int selectedPosition = -1;

    public VoucherAdminAdapter(List<Voucher> voucherList, int layout, Context context) {
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

        ImageView btnDeleteVoucher = convertView.findViewById(R.id.btnDeleteVoucher);
        ImageView btnEditVoucher = convertView.findViewById(R.id.btnEditVoucher);
        btnDeleteVoucher.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa sách: " + voucher.getTieuDe() + " không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        DatabaseReference vouRef = FirebaseDatabase.getInstance().getReference("vouchers/vouchers");

                        vouRef.orderByChild("idVoucher").equalTo(voucher.getidVoucher())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                childSnapshot.getRef().removeValue()
                                                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show())
                                                        .addOnFailureListener(e -> Toast.makeText(context, "Lỗi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                            }
                                        } else {
                                            Toast.makeText(context, "Không tìm thấy dữ liệu để xóa!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(context, "Lỗi kết nối Firebase!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnEditVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminAddVoucherActivity.class);
            intent.putExtra("idvoucher", voucher.getidVoucher());
            context.startActivity(intent);

        });


        return convertView;
    }
}
