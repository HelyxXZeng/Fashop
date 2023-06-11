package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import Model.Order;
import Model.OrderItem;
import Model.ProductVariant;

public class OrderActivity extends AppCompatActivity{

    TextView UserName, Address, totalTxt;
    EditText note;
    RecyclerView.Adapter adapter;
    RecyclerView OrderItemList;
    List<CartItem> cartItems = new ArrayList<>();
    List<OrderItem> orderItems = new ArrayList<>();
    Order currentOrder = null;
    Button ConfirmBtn;
    private int orderID;
    private int maxOrderID;
    private int maxOrderItemID;
    private double total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        String cartItemsListKey = getIntent().getStringExtra("cart_items_list_key");
        cartItems = new Gson().fromJson(cartItemsListKey, new TypeToken<List<CartItem>>(){}.getType());

        total = getIntent().getDoubleExtra("total_key", 0);

        initView();
        ConfirmEvent();
    }
    private void initView(){
        OrderItemList = findViewById(R.id.cart_item_rcv);

        orderItems.clear();
        for (CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItem(cartItem);
            orderItems.add(orderItem);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        OrderItemList.setLayoutManager(linearLayoutManager);
        adapter = new OrderItemAdapter(orderItems);
        OrderItemList.setAdapter(adapter);

        UserName = findViewById(R.id.tvUserName);
        Address = findViewById(R.id.address);
        totalTxt = findViewById(R.id.totalTxt);
        totalTxt.setText(Double.toString(total));
        FirebaseAuth.getInstance().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String city = ""+ds.child("city").getValue();
                            String district = ""+ds.child("district").getValue();
                            String ward = ""+ds.child("ward").getValue();
                            String street = ""+ds.child("streetAddress").getValue();
                            Address.setText(street + ", " + ward + ", " + district + ", " + city);

                            String name = ""+ds.child("name").getValue();
                            UserName.setText(name);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void ConfirmEvent(){
        ConfirmBtn = findViewById(R.id.checkoutBtn);

        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushOrder();
                pushOrderItems();
                Toast.makeText(getApplicationContext(), "Create Order Completed", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }
    private void pushOrder(){
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Order");
        // Retrieve current data from the database
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Order order = categorySnapshot.getValue(Order.class);
                    if (order != null && order.getID() > maxOrderID) {
                        maxOrderID = order.getID();
                    }
                }

                Order order = new Order();

                order.setID(++maxOrderID);

                order.setCustomerID(FirebaseAuth.getInstance().getUid());

                note = findViewById(R.id.note);
                order.setNote(String.valueOf(note.getText()));

                order.setStatus("PENDING");

                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // format of the string
                String formattedDate = currentDate.format(formatter);
                order.setDate(formattedDate);

                order.setTotal(total);

                currentOrder = order;
                ref2.child(String.valueOf(order.getID())).setValue(order,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Log.v("OrderItem", "completed");
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void pushOrderItems(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("OrderItem");
        // Retrieve current data from the database
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                /*Query query = ref.orderByChild("orderID").equalTo(currentOrder.getID());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot variantSnapshot : snapshot.getChildren()) {
                            variantSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    OrderItem item = categorySnapshot.getValue(OrderItem.class);
                    if (item != null && item.getID() > maxOrderItemID) {
                        maxOrderItemID = item.getID();
                    }
                }

                for (OrderItem item : orderItems){
                    item.setOrderID(currentOrder.getID());
                    item.setID(++maxOrderItemID);

                    ref.child(String.valueOf(item.getID())).setValue(item,
                            new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Log.v("OrderItem", "completed");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}