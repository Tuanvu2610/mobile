package com.example.bansach.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bansach.Activity.Admin.AdminProductDetailManager;
import com.example.bansach.Activity.ProductDetailActivity;
import com.example.bansach.R;
import com.example.bansach.model.Book;

import java.util.List;

public class AdminBookAdapter extends RecyclerView.Adapter<AdminBookAdapter.AdminBookViewHolder> {

    private Context context;
    private List<Book> bookList;

    public AdminBookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public AdminBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_book, parent, false);
        return new AdminBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookViewHolder holder, int position) {
        Book book = bookList.get(position);
        if (book == null) return;

        holder.txtTitle.setText(book.getTenSP());
        holder.txtAuthor.setText(book.getTG());

        // Format giá tiền
        holder.txtPrice.setText(String.format("%,.0f đ", book.getGia_Ban()));

        // Load ảnh bằng Glide
        Glide.with(context)
                .load(book.getImg())
                .placeholder(R.drawable.book) // Ảnh chờ tạm thời
                .into(holder.imgBook);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminProductDetailManager.class);
            intent.putExtra("maSP", book.getMaSP()); // Gửi mã sản phẩm đi
            context.startActivity(intent);
        });

        // TODO: Chút nữa chúng ta sẽ viết code cho nút Xóa ở đây
        holder.btnDelete.setOnClickListener(v -> {
            Toast.makeText(context, "Bấm xóa: " + book.getTenSP(), Toast.LENGTH_SHORT).show();
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminProductDetailManager.class);
                intent.putExtra("maSP", book.getMaSP());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    public static class AdminBookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView txtTitle, txtAuthor, txtPrice;
        ImageButton btnEdit, btnDelete;

        public AdminBookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgAdminBook);
            txtTitle = itemView.findViewById(R.id.txtAdminBookTitle);
            txtAuthor = itemView.findViewById(R.id.txtAdminBookAuthor);
            txtPrice = itemView.findViewById(R.id.txtAdminBookPrice);
            btnEdit = itemView.findViewById(R.id.btnEditBook);
            btnDelete = itemView.findViewById(R.id.btnDeleteBook);
        }
    }
}