package com.example.bansach.adapter;

import android.content.Context;
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

public class BookGridAdapter extends RecyclerView.Adapter<BookGridAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;

    // Hàm khởi tạo để nhận dữ liệu truyền vào
    public BookGridAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp file giao diện 1 ô sách (item_book.xml) vào
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // Lấy thông tin cuốn sách ở vị trí hiện tại
        Book book = bookList.get(position);

        // Đổ chữ vào TextView
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookPrice.setText(book.getPrice() + " đ");

        // Dùng thư viện Glide để load ảnh bìa sách từ Internet đổ vào ImageView
        Glide.with(context)
                .load(book.getImageUrl())
                .into(holder.imgBookCover);
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    // Lớp này dùng để ánh xạ các view trong file item_book.xml
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        TextView tvBookTitle, tvBookPrice;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // Thay đổi ID ở đây nếu file item_book.xml của bạn đặt ID khác
            imgBookCover = itemView.findViewById(R.id.imgBook);
            tvBookTitle = itemView.findViewById(R.id.txtBestSeller);
            tvBookPrice = itemView.findViewById(R.id.txtAuthor);
        }
    }
}