package com.example.bansach.api;


import com.example.bansach.model.Book;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    /**
     * API 1: Lấy danh sách sách có tích hợp Phân trang, Bộ lọc và Sắp xếp (Dành cho Category Page)
     * Thích hợp cho xử lý sự kiện click lọc, sắp xếp tức thời và Infinite Scroll.
     * * @param page Số trang hiện tại (bắt đầu từ 0) phục vụ Pagination
     * @param size Số lượng cuốn sách hiển thị trong một trang
     * @param categoryId ID thể loại sách cần lọc
     * @param minPrice Mức giá thấp nhất muốn lọc
     * @param maxPrice Mức giá cao nhất muốn lọc
     * @param sort Tiêu chí sắp xếp, ví dụ: "price,asc" hoặc "price,desc"
     */
    @GET("api/books")
    Call<List<Book>> getCategoryBooks(
            @Query("page") int page,
            @Query("size") int size,
            @Query("categoryId") Integer categoryId,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("sort") String sort
    );

    /**
     * API 2: Tìm kiếm sách theo từ khóa và Gợi ý lịch sử tìm kiếm (Dành cho Search Page/Modal)
     * API này sẽ được kích hoạt sau khi xử lý xong kỹ thuật "Debounce" 0.5s ở UI để tránh lag.
     * * @param keyword Từ khóa người dùng gõ vào ô tìm kiếm
     */
    @GET("api/books/search")
    Call<List<Book>> searchBooks(
            @Query("keyword") String keyword
    );
}