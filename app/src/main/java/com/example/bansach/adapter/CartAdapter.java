package com.example.bansach.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.bansach.Activity.SessionManager;
import com.example.bansach.Activity.ShoppingCartActivity;
import com.example.bansach.R;
import com.example.bansach.model.Book;
import com.example.bansach.model.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<CartItem> cartList;
    private String userId;

    public CartAdapter(Context context, int layout, List<CartItem> cartList) {
        this.context = context;
        this.layout = layout;
        this.cartList = cartList;

        // Khởi tạo luôn userId từ SessionManager có sẵn của mày
        SessionManager sessionManager = new SessionManager(context);
        this.userId = sessionManager.getUserId();
    }

    @Override
    public int getCount() {
        return cartList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
        }

        ImageView imgProduct = convertView.findViewById(R.id.imgProduct);
        TextView tvProductName = convertView.findViewById(R.id.tvProductName);
        TextView tvProductCode = convertView.findViewById(R.id.tvProductCode);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);

        // nút tăng, giảm, xóa
        Button btnMinus = convertView.findViewById(R.id.btnMinus);
        Button btnPlus = convertView.findViewById(R.id.btnPlus);
        TextView tvnDelete = convertView.findViewById(R.id.tvnDelete); // Nút X mày đặt tên là tvnDelete nè
        CheckBox cbSelect = convertView.findViewById(R.id.checkboxproduct);

        CartItem item = cartList.get(position);

        tvProductName.setText(item.getTenSP());
        tvProductCode.setText("Mã SP: " + item.getMaSP());
        tvQuantity.setText(String.valueOf(item.getSoLuong()));
        tvPrice.setText(String.format("%,.0f đ", item.getGia_Ban()));

        Glide.with(context)
                .load(item.getImg())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgProduct);

        // Đường dẫn thẳng tới món hàng này trên Firebase để tí nữa cập nhật
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Carts")
                .child(userId)
                .child(String.valueOf(item.getMaSP()));
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("OrderDetail").child(userId);

        // Nút Cộng (+)
        btnPlus.setOnClickListener(v -> {
            int newQty = item.getSoLuong() + 1;

            // 1. Cập nhật số lượng ngay trên màn hình điện thoại
            item.setSoLuong(newQty);

            // 2. Báo cho Activity tính lại tiền lập tức!
            ((ShoppingCartActivity) context).tinhTongTien();

            // 3. Đẩy dữ liệu lên Firebase chạy ngầm phía sau
            itemRef.child("soLuong").setValue(newQty);
        });

// Nút Trừ (-)
        btnMinus.setOnClickListener(v -> {
            int currentQty = item.getSoLuong();
            if (currentQty > 1) {
                int newQty = currentQty - 1;

                // 1. Cập nhật số lượng ngay trên màn hình điện thoại
                item.setSoLuong(newQty);

                // 2. Báo cho Activity tính lại tiền lập tức!
                ((ShoppingCartActivity) context).tinhTongTien();

                // 3. Đẩy lên Firebase chạy ngầm phía sau
                itemRef.child("soLuong").setValue(newQty);
            } else {
                // Nếu đang là 1 mà bấm Trừ tiếp thì xóa
                itemRef.removeValue();
                FirebaseDatabase.getInstance().getReference("OrderDetail")
                        .child(userId).child(String.valueOf(item.getMaSP())).removeValue();
            }
        });

        // (X)
        tvnDelete.setOnClickListener(v -> {
            itemRef.removeValue();
        });


//      checkbox chọn sản phẩm
        cbSelect.setOnCheckedChangeListener(null);
        cbSelect.setChecked(item.isChecked());
        cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            ((ShoppingCartActivity) context).tinhTongTien();
        });

        return convertView;
    }
}