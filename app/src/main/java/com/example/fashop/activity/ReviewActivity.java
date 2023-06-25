package com.example.fashop.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.WriteReviewAdapter;
import Interface.OnReviewListener;
import Model.Order;
import Model.OrderItem;

public class ReviewActivity extends AppCompatActivity implements Interface.OnReviewListener {

    private WriteReviewAdapter writeReviewAdapter;

    private List<OrderItem> orderItemList = new ArrayList<>();

    private Order order;

    private ImageButton backBtn;

    private TextView submitBtn;

    private RecyclerView writeReview_rcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        order = (Order)  getIntent().getSerializableExtra("order");
        backBtn = findViewById(R.id.backBtn);
        submitBtn = findViewById(R.id.submitBtn);
        writeReview_rcView = findViewById(R.id.review_rcv);
        initListner();
        loadWriteReviews();
    }

    private void initListner() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (OrderItem item : orderItemList)
                {
                    Log.e("rate", String.valueOf(item.getRate()));
                    if (item.getRate() == 0){
                        Toast.makeText(ReviewActivity.this, "To submit, add rating", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                updateReviewToDB();
            }
        });
    }

    private void updateReviewToDB(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("OrderItem");
        for (OrderItem item : orderItemList)
        {
            ref.child(String.valueOf(item.getID())).setValue(item, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Toast.makeText(ReviewActivity.this, "Rate product successfully", Toast.LENGTH_SHORT).show();

                    // Notify the adapter about the data change
                    onBackPressed();

                }
            });
        }

    }

    private void loadWriteReviews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        writeReview_rcView.setLayoutManager(linearLayoutManager);
        writeReviewAdapter = new WriteReviewAdapter(orderItemList);
        writeReview_rcView.setAdapter(writeReviewAdapter);
        writeReviewAdapter.setOnReviewListener(this);

        getData();
    }

    private void getData() {
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("OrderItem");
        ref2.orderByChild("orderID").equalTo(order.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (orderItemList != null){
                    orderItemList.clear();
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OrderItem orderItem = dataSnapshot.getValue(OrderItem.class);
                    orderItemList.add(orderItem);
                }

                writeReviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCommentChanged(int position, String comment) {
        OrderItem orderItem = orderItemList.get(position);
        orderItem.setComment(comment);
        Log.e("rating", String.valueOf(comment));
    }

    @Override
    public void onRatingChanged(int position, float rating) {
        OrderItem orderItem = orderItemList.get(position);
        orderItem.setRate(rating);
        Log.e("comment", String.valueOf(rating));
    }
}