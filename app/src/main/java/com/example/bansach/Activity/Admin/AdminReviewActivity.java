package com.example.bansach.Activity.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.model.Review;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class AdminReviewActivity extends AppCompatActivity {

    ListView listReview;
    ArrayList<Review> reviewList = new ArrayList<>();
    DatabaseReference reviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review);

        listReview = findViewById(R.id.rvReview);
        reviewRef = FirebaseDatabase.getInstance().getReference("review");

        loadReview();
    }

    private void loadReview(){

        reviewRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                reviewList.clear();

                for(DataSnapshot data : snapshot.getChildren()){
                    Review review = data.getValue(Review.class);
                    if(review!=null){
                        reviewList.add(review);
                    }
                }

                listReview.setAdapter(new ReviewAdapter());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    class ReviewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return reviewList.size();
        }

        @Override
        public Object getItem(int position) {
            return reviewList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {

            if(convertView==null){
                convertView=getLayoutInflater().inflate(R.layout.admin_item_review,parent,false);
            }

            TextView txtUser=convertView.findViewById(R.id.txtUser);
            TextView txtBookId=convertView.findViewById(R.id.txtBook);
            TextView txtContent=convertView.findViewById(R.id.txtContent);
            TextView txtTime=convertView.findViewById(R.id.txtTime);

            RatingBar ratingBar=convertView.findViewById(R.id.ratingBar);

            Button btnHide=convertView.findViewById(R.id.btnHide);
            Button btnDelete=convertView.findViewById(R.id.btnDelete);

            Review review=reviewList.get(position);

            txtUser.setText(review.getUsername());
            txtBookId.setText("Book ID: "+review.getBook_id());
            txtContent.setText(review.getContent());
            txtTime.setText(review.getTime());
            ratingBar.setRating(review.getRating());

            btnDelete.setOnClickListener(v->{
                reviewRef.child(review.getId()).removeValue();
            });

            btnHide.setOnClickListener(v->{
                reviewRef.child(review.getId()).child("visible").setValue(false);
            });

            return convertView;
        }
    }

}