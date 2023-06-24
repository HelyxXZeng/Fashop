package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fashop.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.OrderAdapter;
import Fragment.OrdersPagerAdapter;
import Model.Order;

public class OrderHistoryActivity extends AppCompatActivity {

    private String currentStatus = "PENDING";
    private List<Order> orders = new ArrayList<>();
    private List<Order> loading = new ArrayList<>();
    private RecyclerView OrderView;
    OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        OrdersPagerAdapter adapter2 = new OrdersPagerAdapter(this);

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(adapter2);
        viewPager2.setOffscreenPageLimit(adapter2.getItemCount());

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position){
                    case 0:
                        tab.setText("Pending");
                        break;
                    case 1:
                        tab.setText("Confirmed");
                        break;
                    case 2:
                        tab.setText("Shipping");
                        break;
                    case 3:
                        tab.setText("Completed");
                        break;
                    case 4:
                        tab.setText("Declined");
                        break;
                    case 5:
                        tab.setText("Canceled");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
        /*
        OrderView = findViewById(R.id.recycler_view_order_history);

        pending = findViewById(R.id.button_pending);
        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // select current button
                pending.setSelected(true);
                selectedButton = pending;
                currentStatus = "PENDING";
                loadOrder();
            }
        });

        confirmed = findViewById(R.id.button_confirmed);
        confirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // select current button
                confirmed.setSelected(true);
                selectedButton = confirmed;
                currentStatus = "CONFIRMED";
                loadOrder();
            }
        });

        shipping = findViewById(R.id.button_shipping);
        shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // select current button
                shipping.setSelected(true);
                selectedButton = shipping;
                currentStatus = "SHIPPING";
                loadOrder();
            }
        });

        completed = findViewById(R.id.button_completed);
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // select current button
                completed.setSelected(true);
                selectedButton = completed;
                currentStatus = "COMPLETED";
                loadOrder();
            }
        });

        declined = findViewById(R.id.button_declined);
        declined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // select current button
                declined.setSelected(true);
                selectedButton = declined;
                currentStatus = "DECLINED";
                loadOrder();
            }
        });

        canceled = findViewById(R.id.button_canceled);
        canceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedButton != null) {
                    selectedButton.setSelected(false);
                }

                // select current button
                canceled.setSelected(true);
                selectedButton = canceled;
                currentStatus = "CANCELED";
                loadOrder();
            }
        });

        getOrderData();
        loadOrder();*/
    }

    private void getOrderData(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Order");

        Query query = ordersRef.orderByChild("customerID").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the Order object from Firebase
                    Order order = snapshot.getValue(Order.class);

                    // Add the Order object to the list
                    orders.add(order);
                }
                loadOrder();
                // Do something with the list of Order objects here
                // For example, update your RecyclerView adapter with the new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur while retrieving data from Firebase
            }
        });
    }
    private void loadOrder(){
        loading.clear();
        for (Order order : orders){
            if(order.getStatus().equals(currentStatus)) loading.add(order);
        }
        GridLayoutManager manager = new GridLayoutManager(OrderHistoryActivity.this, 1);
        OrderView.setLayoutManager(manager);
        adapter = new OrderAdapter(OrderHistoryActivity.this, loading);
        OrderView.setAdapter(adapter);
    }
}