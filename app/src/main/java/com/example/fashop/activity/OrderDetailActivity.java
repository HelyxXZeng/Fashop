package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import Adapter.OrderItemAdapter;
import Model.CartItem;
import Model.ModelImage;
import Model.Order;
import Model.OrderItem;
import Model.ProductModel;
import Model.ProductVariant;

public class OrderDetailActivity extends AppCompatActivity{

    TextView UserName, Address, totalTxt, shippingCost, totalCost;
    EditText note;
    RecyclerView.Adapter adapter;
    RecyclerView OrderItemList;
    List<OrderItem> orderItems = new ArrayList<>();
    Order currentOrder = null;
    Button ConfirmBtn;
    LinearLayout button_layout;
    private int orderID;
    private int maxOrderID;
    private int maxOrderItemID;
    private double total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        String cartItemsListKey = getIntent().getStringExtra("order");
        currentOrder = (Order) getIntent().getSerializableExtra("order");

        total = currentOrder.getTotal();

        initView();
        ConfirmEvent();
    }
    private void initView(){
        OrderItemList = findViewById(R.id.cart_item_rcv);

        orderItems.clear();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        OrderItemList.setLayoutManager(linearLayoutManager);
        adapter = new OrderItemAdapter(orderItems);
        OrderItemList.setAdapter(adapter);


        UserName = findViewById(R.id.tvUserName);
        Address = findViewById(R.id.address);

        totalTxt = findViewById(R.id.totalTxt);
        totalTxt.setText(Double.toString(total));

        shippingCost = findViewById(R.id.shippingCost);

        totalCost = findViewById(R.id.totalCost);
        totalCost.setText("$" + Double.toString(total - 1));

        FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(currentOrder.getCustomerID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String city = "" + ds.child("city").getValue();
                    String district = "" + ds.child("district").getValue();
                    String ward = "" + ds.child("ward").getValue();
                    String street = "" + ds.child("streetAddress").getValue();
                    Address.setText("Address: " + street + ", " + ward + ", " + district + ", " + city);

                    String name = "User Name: " + ds.child("name").getValue();
                    UserName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailActivity.this,"Failed to load user data. Try again later", Toast.LENGTH_LONG).show();
            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("OrderItem");
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Variant");
        ref2.orderByChild("orderID").equalTo(currentOrder.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    OrderItem orderItem =  ds.getValue(OrderItem.class);

                    ref3.orderByChild("id").equalTo(orderItem.getVariantID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot hm: snapshot.getChildren())
                            {
                                orderItem.setColor(hm.child("color").getValue().toString());
                                orderItem.setSize(hm.child("size").getValue().toString());
                                adapter.notifyDataSetChanged();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ModelImage");


                                Query query2 = reference.orderByChild("modelID").equalTo(Integer.parseInt(hm.child("modelID").getValue().toString()));
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                            ModelImage image = modelSnapshot.getValue(ModelImage.class);
                                            orderItem.setImage(image.getUrl());
                                            adapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle errors here
                                    }
                                });

                                DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference("Model");

                                Query query3 = ref4.orderByChild("id").equalTo(Integer.parseInt(hm.child("modelID").getValue().toString()));
                                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                            ProductModel m = modelSnapshot.getValue(ProductModel.class);
                                            orderItem.setPrice(m.getPrice());
                                            adapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle errors here
                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    orderItems.add(orderItem);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ConfirmEvent(){
        ConfirmBtn = findViewById(R.id.checkoutBtn);
        ConfirmBtn.setText("RECEIVED");
        button_layout = findViewById(R.id.button_layout);

        if(!currentOrder.getStatus().equals("SHIPPING")) {
            ConfirmBtn.setEnabled(false);
            button_layout.setBackgroundColor(Color.GRAY);
        }

        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentOrder.setStatus("COMPLETED");
            }
        });
    }
}