package com.example.bansach.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bansach.Activity.ProductDetailActivity;
import com.example.bansach.Activity.SessionManager;
import com.example.bansach.R;
import com.example.bansach.model.Book;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.BookViewHolder> {
    private Context context;

    private List<Book> bookList;
    SessionManager sessionManager;
    String userId;

    public MainAdapter(Context context,List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
        sessionManager = new SessionManager(context);
        userId = sessionManager.getUserId();
    }

    @NonNull
    @Override
    public MainAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new MainAdapter.BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        if (book == null) return;
        holder.txtAuthor.setText(book.getTG());
        holder.txtTitle.setText(book.getTenSP());

        holder.txtPrice.setText(String.format("%,.0f đ", book.getGia_Ban()));
        holder.txtPriceSale.setText(String.format("%,.0f đ", book.getDon_gia()));

        holder.txtPriceSale.setPaintFlags(holder.txtPriceSale.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        String imageUrl = book.getImg();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.book)
                .error(R.drawable.book)
                .into(holder.imgBook);
        //      thêm vào yêu thích
        if (holder.btnFavorite != null) {
            DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("YeuThich")
                    .child(userId)
                    .child(String.valueOf(book.getMaSP()));

            // Kiểm tra trạng thái tim lúc tải danh sách
            favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.btnFavorite.setChecked(snapshot.exists());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            holder.btnFavorite.setOnClickListener(view -> {
                boolean isChecked = holder.btnFavorite.isChecked();

                view.animate().scaleX(1.3f).scaleY(1.3f).setDuration(150).withEndAction(() -> {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                }).start();

                if (isChecked) {
                    favRef.setValue(true);
                    Snackbar.make(view, "Đã thêm vào danh sách yêu thích 💖", Snackbar.LENGTH_SHORT).show();
                } else {
                    favRef.removeValue();
                    Snackbar.make(view, "Đã bỏ yêu thích", Snackbar.LENGTH_LONG)
                            .setAction("HOÀN TÁC", v -> {
                                holder.btnFavorite.setChecked(true);
                                favRef.setValue(true);
                            })
                            .setActionTextColor(Color.parseColor("#FF5722"))
                            .show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("masp", book.getMaSP());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (bookList != null) {
            return Math.min(bookList.size(), 4);
        }
        return 0;
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView txtAuthor, txtTitle, txtPrice, txtPriceSale;
        CheckBox btnFavorite;


        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);

            txtPriceSale = itemView.findViewById(R.id.txtPriceSale);
            btnFavorite = itemView.findViewById(R.id.btnHeart);

        }
    }
}