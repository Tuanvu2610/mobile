package com.example.bansach.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.Book;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.BookViewHolder> {

    private List<Book> bookList;

    public MainAdapter(List<Book> bookList) {
        this.bookList = bookList;
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

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);

            txtPriceSale = itemView.findViewById(R.id.txtPriceSale);

        }
    }
}