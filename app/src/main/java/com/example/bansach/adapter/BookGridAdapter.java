package com.example.bansach.adapter;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.Book;

import java.util.List;

public class BookGridAdapter extends RecyclerView.Adapter<BookGridAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;
    public BookGridAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
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

//        if(soDanhGia > 0) {
        holder.txtRating.setText("(" + soDanhGia + " đánh giá)");
        holder.txtRating.setVisibility(View.VISIBLE);
//        } else {
//            // Nếu = 0 (tức là sách cũ chưa ai đánh giá, hoặc dữ liệu JSON đang thiếu)
//            // Bạn có thể ẩn cái dòng chữ đó đi cho gọn
//            holder.txtRating.setVisibility(View.GONE);
//        }
        float soSao = (float) book.getAverageRating();
        holder.ratingBar.setRating(soSao);
        Glide.with(context)
                .load(book.getImg())
                .into(holder.imgBookCover);
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        TextView tvBookTitle, tvBookPrice, txtPriceSale, txtAuthor, txtRating;
        RatingBar ratingBar;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // Thay đổi ID ở đây nếu file item_book.xml của bạn đặt ID khác
            imgBookCover = itemView.findViewById(R.id.imgBook);
            tvBookTitle = itemView.findViewById(R.id.txtTitle);
            tvBookPrice = itemView.findViewById(R.id.txtPrice);
            txtPriceSale = itemView.findViewById(R.id.txtPriceSale);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtRating = itemView.findViewById(R.id.txtRating);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
    public void countReview(int position){

    }
}