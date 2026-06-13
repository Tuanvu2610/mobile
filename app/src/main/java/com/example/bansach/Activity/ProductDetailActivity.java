package com.example.bansach.Activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ProductDetailActivity extends AppCompatActivity {

    // =========================
    // VIEW
    // =========================

    ImageView imgBook;

    TextView txtDescription;
    TextView txtReadMore;
    TextView txtQuantity;

    Button btnMinus;
    Button btnPlus;
    Button btnAddToCart;

    // review

    Button btnWriteReview;
    Button btnSendReview;

    LinearLayout layoutReviewForm;
    LinearLayout layoutReviewList;

    EditText edtReview;

    RatingBar ratingInput;

    // =========================
    // DATA
    // =========================

    boolean isExpanded = false;

    int quantity = 1;

    // =========================
    // ZOOM IMAGE
    // =========================

    private ScaleGestureDetector scaleGestureDetector;

    private float scaleFactor = 1.0f;

    // =========================
    // ON CREATE
    // =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_detail);

        // =========================
        // FIND VIEW
        // =========================

        imgBook = findViewById(R.id.imgBook);

        txtDescription = findViewById(R.id.txtDescription);

        txtReadMore = findViewById(R.id.txtReadMore);

        txtQuantity = findViewById(R.id.txtQuantity);

        btnMinus = findViewById(R.id.btnMinus);

        btnPlus = findViewById(R.id.btnPlus);

        btnAddToCart = findViewById(R.id.btnAddToCart);

        // =========================
        // REVIEW
        // =========================

        btnWriteReview = findViewById(R.id.btnWriteReview);

        btnSendReview = findViewById(R.id.btnSendReview);

        layoutReviewForm = findViewById(R.id.layoutReviewForm);
        layoutReviewList = findViewById(R.id.layoutReviewList);

        edtReview = findViewById(R.id.edtReview);

        ratingInput = findViewById(R.id.ratingInput);
        ratingInput.setStepSize(0.5f);

        // =========================
        // XEM THÊM / THU GỌN
        // =========================

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
//  gửi id
        int maSachHienTai = getIntent().getIntExtra("masp", -1);

        if (maSachHienTai != -1) {
            fetchBookDetailFromFirebase(maSachHienTai);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sách!", Toast.LENGTH_SHORT).show();
        }

        // =========================
        // TĂNG SỐ LƯỢNG
        // =========================

        btnPlus.setOnClickListener(v -> {

            quantity++;

            txtQuantity.setText(String.valueOf(quantity));
        });

        // =========================
        // GIẢM SỐ LƯỢNG
        // =========================

        btnMinus.setOnClickListener(v -> {

            if (quantity > 1) {

                quantity--;

                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        // =========================
        // THÊM GIỎ HÀNG
        // =========================

        btnAddToCart.setOnClickListener(v -> {

            Toast.makeText(
                    ProductDetailActivity.this,
                    "Đã thêm " + quantity + " sản phẩm vào giỏ hàng",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // =========================
        // HIỆN / ẨN FORM REVIEW
        // =========================

        btnWriteReview.setOnClickListener(v -> {

            if (layoutReviewForm.getVisibility() == View.GONE) {

                layoutReviewForm.setVisibility(View.VISIBLE);

                btnWriteReview.setText("Ẩn đánh giá");

            } else {

                layoutReviewForm.setVisibility(View.GONE);

                btnWriteReview.setText("Viết đánh giá");
            }
        });

        // =========================
        // GỬI REVIEW
        // =========================

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

            // =========================
            // TẠO REVIEW MỚI
            // =========================

            LinearLayout reviewItem = new LinearLayout(this);

            reviewItem.setOrientation(LinearLayout.VERTICAL);

            reviewItem.setPadding(20,20,20,20);

            reviewItem.setBackgroundColor(0xFFF5F5F5);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(0,20,0,0);

            reviewItem.setLayoutParams(params);

            // tên user

            TextView txtUser = new TextView(this);

            txtUser.setText("Người dùng");

            txtUser.setTextSize(16);

            txtUser.setTypeface(null, android.graphics.Typeface.BOLD);

            // rating

            RatingBar newRating = new RatingBar(
                    this,
                    null,
                    android.R.attr.ratingBarStyleSmall
            );

            newRating.setNumStars(5);

            newRating.setStepSize(0.5f);

            newRating.setRating(rating);

            newRating.setScaleX(0.7f);

            newRating.setScaleY(0.7f);
            LinearLayout.LayoutParams ratingParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            newRating.setLayoutParams(ratingParams);

            // nội dung review

            TextView txtReview = new TextView(this);

            txtReview.setText(review);

            txtReview.setTextSize(15);

            txtReview.setPadding(0,10,0,0);

            // add view

            reviewItem.addView(txtUser);

            reviewItem.addView(newRating);

            reviewItem.addView(txtReview);

            // add vào layout

            layoutReviewList.addView(reviewItem);

            // =========================
            // RESET FORM
            // =========================

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

        // =========================
        // ZOOM IMAGE
        // =========================

        scaleGestureDetector =
                new ScaleGestureDetector(
                        this,
                        new ScaleListener()
                );

        imgBook.setOnTouchListener((v, event) -> {

            scaleGestureDetector.onTouchEvent(event);

            return true;
        });
    }

    // =========================
    // SCALE LISTENER
    // =========================

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            scaleFactor *= detector.getScaleFactor();

            scaleFactor = Math.max(
                    0.5f,
                    Math.min(scaleFactor, 3.0f)
            );

            imgBook.setScaleX(scaleFactor);

            imgBook.setScaleY(scaleFactor);

            return true;
        }
    }

    // =========================
    // TOUCH EVENT
    // =========================

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleGestureDetector.onTouchEvent(event);

        return true;
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
                            String tenSach = currentBook.getTenSP();
                            txtBookName.setText(tenSach);
                            txtAuthor.setText(currentBook.getTG());
                            txtPrice.setText(String.valueOf(currentBook.getGia_Ban()) + " đ");
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
}