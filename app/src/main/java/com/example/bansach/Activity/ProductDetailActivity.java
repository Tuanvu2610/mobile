package com.example.bansach.Activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bansach.R;
import com.example.bansach.model.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    //view
    ImageView imgBook;

    TextView txtDescription;
    TextView txtReadMore;
    TextView txtQuantity;

    TextView btnMinus;
    TextView btnPlus;
    Button btnAddToCart;
    ImageButton btnBack;

    //review
    Button btnWriteReview;
    Button btnSendReview;

    LinearLayout layoutReviewForm;
    LinearLayout layoutReviewList;

    EditText edtReview;

    RatingBar ratingInput;

    //data
    boolean isExpanded = false;
    int quantity = 1;
    private int maSachHienTai;

    //zoom img
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_detail);

        imgBook = findViewById(R.id.imgBook);

        txtDescription = findViewById(R.id.txtDescription);
        txtReadMore = findViewById(R.id.txtReadMore);
        txtQuantity = findViewById(R.id.txtQuantity);

        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        //back
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        //review
        btnWriteReview = findViewById(R.id.btnWriteReview);
        btnSendReview = findViewById(R.id.btnSendReview);

        layoutReviewForm = findViewById(R.id.layoutReviewForm);
        layoutReviewList = findViewById(R.id.layoutReviewList);

        edtReview = findViewById(R.id.edtReview);

        ratingInput = findViewById(R.id.ratingInput);
        ratingInput.setStepSize(0.5f);

        SessionManager sessionManager = new SessionManager(this);
        String currentUser = sessionManager.getUsername();

        //xem them/thu gon
        txtReadMore.setOnClickListener(v -> {
            if (isExpanded) {
                txtDescription.setMaxLines(3);
                txtReadMore.setText("Xem thêm");
            } else {
                txtDescription.setMaxLines(Integer.MAX_VALUE);
                txtReadMore.setText("Thu gọn");
            }
            isExpanded = !isExpanded;
        });

        //gửi id
        int maSachHienTai = getIntent().getIntExtra("masp", -1);
        if (maSachHienTai != -1) {
            fetchBookDetailFromFirebase(maSachHienTai);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sách!", Toast.LENGTH_SHORT).show();
        }

        //tang so luong
        btnPlus.setOnClickListener(v -> {
            quantity++;
            txtQuantity.setText(String.valueOf(quantity));
        });

        //giam so luong
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        //add cart
        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(
                    ProductDetailActivity.this,
                    "Đã thêm " + quantity + " sản phẩm vào giỏ hàng",
                    Toast.LENGTH_SHORT
            ).show();
        });

        //an/hien review form
        btnWriteReview.setOnClickListener(v -> {
            if (layoutReviewForm.getVisibility() == View.GONE) {
                layoutReviewForm.setVisibility(View.VISIBLE);
                btnWriteReview.setText("Ẩn đánh giá");
            } else {
                layoutReviewForm.setVisibility(View.GONE);
                btnWriteReview.setText("Viết đánh giá");
            }
        });

        //gui rw
        btnSendReview.setOnClickListener(v -> {
            String review = edtReview.getText().toString();
            float rating = ratingInput.getRating();
            if (review.isEmpty()) {
                Toast.makeText(
                        ProductDetailActivity.this,
                        "Vui lòng nhập đánh giá",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            //tao review
            DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("review");
            String key = reviewRef.push().getKey();
            Review reviewModel = new Review();

            reviewModel.setId(key);
            reviewModel.setBook_id(maSachHienTai);
            reviewModel.setUsername(currentUser);
            reviewModel.setContent(review);
            reviewModel.setRating(rating);
            reviewModel.setVisible(true);

            String time = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
            ).format(new Date());

            reviewModel.setTime(time);

            reviewRef.child(key)
                    .setValue(reviewModel)
                    .addOnSuccessListener(unused -> {

                        edtReview.setText("");
                        ratingInput.setRating(0);
                        layoutReviewForm.setVisibility(View.GONE);
                        btnWriteReview.setText("Viết đánh giá");
                        Toast.makeText(
                                ProductDetailActivity.this,
                                "Đã gửi đánh giá",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });
    }
    // =========================
    // LẤY DỮ LIỆU TỪ FIREBASE
    // =========================
    private void fetchBookDetailFromFirebase(double idSach) {
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("book");
        int maSanPhamCachTim = (int) idSach;
        bookRef.orderByChild("MaSP").equalTo(maSanPhamCachTim).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot bookSnap : snapshot.getChildren()) {
                        Book currentBook = bookSnap.getValue(Book.class);
                        if (currentBook != null) {
                            TextView txtBookName = findViewById(R.id.txtBookName);
                            TextView txtAuthor = findViewById(R.id.txtAuthor);
                            TextView txtPrice = findViewById(R.id.txtPrice);
                            ImageView imgBook = findViewById(R.id.imgBook);
                            TextView txtDescription = findViewById(R.id.txtDescription);
                            TextView detailTG = findViewById(R.id.detailTG);
                            TextView namSX = findViewById(R.id.namSX);
                            TextView nhaXB = findViewById(R.id.nhaXB);
                            String tenSach = currentBook.getTenSP();
                            txtBookName.setText(tenSach);
                            txtAuthor.setText(currentBook.getTG());
                            if (currentBook.getNam_XB() != null) {
                                namSX.setText("Năm sản xuất: " + currentBook.getNam_XB());
                            }else {
                                namSX.setText("");
                            }
                            if (currentBook.getNXB() != null) {
                                nhaXB.setText("Nhà sản xuất: "+ currentBook.getNXB());
                            }else if(currentBook.getTheLoai() != null) {
                                nhaXB.setText("The loai: " + currentBook.getTheLoai());
                            }else {
                                nhaXB.setText("");

                            }
                            txtDescription.setText("Một cuốn sách với nội dung hấp dẫn, được trình bày khoa học và dễ hiểu," +
                                    " phù hợp với nhiều đối tượng độc giả. Sách không chỉ mang đến kiến thức hữu ích mà còn giúp " +
                                    "người đọc giải trí, phát triển tư duy và nuôi dưỡng niềm đam mê đọc sách.");
                            if (currentBook.getTG() != null) {
                                detailTG.setText("Tác Giả: " + currentBook.getTG());
                            }else {
                                detailTG.setText("");
                            }
                            txtPrice.setText(String.format("%,.0fđ", currentBook.getGia_Ban()));
                            String linkAnh = currentBook.getImg();
                            if (linkAnh != null && !linkAnh.isEmpty()) {
                                Glide.with(ProductDetailActivity.this)
                                        .load(linkAnh)
                                        .into(imgBook);
                            }
                        }
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Thất bại: Firebase không có mã số " + maSanPhamCachTim, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi mạng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReview() {

        DatabaseReference reviewRef =
                FirebaseDatabase.getInstance().getReference("review");

        reviewRef.orderByChild("book_id")
                .equalTo(maSachHienTai)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        layoutReviewList.removeAllViews();

                        for (DataSnapshot data : snapshot.getChildren()) {

                            Review review = data.getValue(Review.class);

                            if (review == null) continue;
                            if (!review.isVisible()) continue;

                            LinearLayout reviewItem =
                                    new LinearLayout(ProductDetailActivity.this);

                            reviewItem.setOrientation(LinearLayout.VERTICAL);
                            reviewItem.setPadding(20,20,20,20);

                            LinearLayout.LayoutParams params =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);

                            params.setMargins(0,20,0,0);

                            reviewItem.setLayoutParams(params);

                            TextView txtUser = new TextView(ProductDetailActivity.this);
                            txtUser.setText(review.getUsername());
                            txtUser.setTextSize(16);
                            txtUser.setTypeface(null,
                                    android.graphics.Typeface.BOLD);

                            RatingBar rb = new RatingBar(ProductDetailActivity.this, null, android.R.attr.ratingBarStyleSmall);
                            LinearLayout.LayoutParams lp =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );

                            rb.setLayoutParams(lp);
                            rb.setNumStars(5);
                            rb.setMax(5);
                            rb.setStepSize(0.5f);
                            rb.setIsIndicator(true);

                            float r = review.getRating();
                            if (r > 5f) r = 5f;
                            if (r < 0f) r = 0f;

                            rb.setRating(r);

                            TextView txtContent = new TextView(ProductDetailActivity.this);

                            txtContent.setText(review.getContent());

                            TextView txtTime = new TextView(ProductDetailActivity.this);

                            txtTime.setText(review.getTime());
                            txtTime.setTextSize(12);

                            reviewItem.addView(txtUser);
                            reviewItem.addView(rb);
                            reviewItem.addView(txtContent);
                            reviewItem.addView(txtTime);

                            layoutReviewList.addView(reviewItem);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }
}