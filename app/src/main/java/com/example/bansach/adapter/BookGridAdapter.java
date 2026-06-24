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
import android.widget.RatingBar;
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

public class BookGridAdapter extends RecyclerView.Adapter<BookGridAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;
    SessionManager sessionManager;
    String userId;

    public BookGridAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
        sessionManager = new SessionManager(context);
        userId = sessionManager.getUserId();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvBookTitle.setText(book.getTenSP());

        String formattedGiaBan = String.format("%,.0fđ", book.getGia_Ban());
        holder.tvBookPrice.setText(formattedGiaBan);

        String formattedDonGia = String.format("%,.0fđ", book.getDon_gia());
        holder.txtPriceSale.setText(formattedDonGia);
        holder.txtPriceSale.setPaintFlags(holder.txtPriceSale.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.txtAuthor.setText(book.getTG());
        int soDanhGia = book.getReviewCount();

        holder.txtRating.setText("(" + soDanhGia + " đánh giá)");
        holder.txtRating.setVisibility(View.VISIBLE);
        float soSao = (float) book.getAverageRating();
        holder.ratingBar.setRating(soSao);

        Glide.with(context)
                .load(book.getImg())
                .into(holder.imgBookCover);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("masp", book.getMaSP());
                context.startActivity(intent);
            }
        });

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
        }
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        TextView tvBookTitle, tvBookPrice, txtPriceSale, txtAuthor, txtRating;
        RatingBar ratingBar;
        CheckBox btnFavorite;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBookCover = itemView.findViewById(R.id.imgBook);
            tvBookTitle = itemView.findViewById(R.id.txtTitle);
            tvBookPrice = itemView.findViewById(R.id.txtPrice);
            txtPriceSale = itemView.findViewById(R.id.txtPriceSale);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtRating = itemView.findViewById(R.id.txtRating);
            ratingBar = itemView.findViewById(R.id.ratingBar);

            btnFavorite = itemView.findViewById(R.id.btnHeart);
        }
    }

    public void countReview(int position) {
    }
}