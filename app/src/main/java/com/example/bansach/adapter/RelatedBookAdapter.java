package com.example.bansach.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bansach.Activity.ProductDetailActivity;
import com.example.bansach.R;
import com.example.bansach.model.Book;

import java.util.ArrayList;

public class RelatedBookAdapter
        extends RecyclerView.Adapter<RelatedBookAdapter.MyViewHolder> {

    Context context;
    ArrayList<Book> list;

    public RelatedBookAdapter(Context context, ArrayList<Book> list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView txtName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.imgBookRelated);
            txtName = itemView.findViewById(R.id.txtBookRelated);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_related_book,
                        parent,
                        false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MyViewHolder holder,
            int position) {

        Book book = list.get(position);

        holder.txtName.setText(book.getTenSP());

        Glide.with(context)
                .load(book.getImg())
                .into(holder.imgBook);

        holder.itemView.setOnClickListener(v -> {
            Intent intent =
                    new Intent(context, ProductDetailActivity.class);

            intent.putExtra("masp", book.getMaSP());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}