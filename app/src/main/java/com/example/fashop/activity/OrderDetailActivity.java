package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    ImageButton backBtn;
    LinearLayout button_layout;
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
        BackEvent();
    }
    private void initView(){
        OrderItemList = findViewById(R.id.cart_item_rcv);

        orderItems.clear();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        OrderItemList.setLayoutManager(linearLayoutManager);
        adapter = new OrderItemAdapter(orderItems);
        OrderItemList.setAdapter(adapter);

        note = findViewById(R.id.note);
        note.setText(currentOrder.getNote());

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
                Toast.makeText(OrderDetailActivity.this,"Failed to load order data. Try again later", Toast.LENGTH_LONG).show();
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
    private void BackEvent(){
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void ConfirmEvent(){
        ConfirmBtn = findViewById(R.id.checkoutBtn);
        ConfirmBtn.setText("CANCEL");
        button_layout = findViewById(R.id.button_layout);

        if(!currentOrder.getStatus().equals("PENDING")) {
            ConfirmBtn.setEnabled(false);
            ConfirmBtn.setClickable(false);
            button_layout.setBackgroundColor(Color.GRAY);
        }
        if(currentOrder.getStatus().equals("PENDING")) {
            button_layout.setVisibility(View.VISIBLE);
            ConfirmBtn.setText("CANCEL");
        }

        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentOrder.getStatus().equals("PENDING")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                    builder.setMessage("Are you sure you want to cancel this order?")
                            .setTitle("Cancel Order");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User confirmed - cancel the order
                            Order updatedOrder = currentOrder;
                            updatedOrder.setStatus("CANCELLED");
                            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Order");
                            ordersRef.child(String.valueOf(currentOrder.getID())).child("status").setValue("CANCELLED")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // The order was successfully updated
                                            // Do something here (e.g. show a success message)
                                            Log.v("Status Updated", "Completed");
                                            Toast.makeText(OrderDetailActivity.this, "Order Cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // There was an error updating the order
                                            // Handle the error here (e.g. show an error message)
                                        }
                                    });
                            Intent intent = new Intent(OrderDetailActivity.this, OrderHistoryActivity.class);
                            Bundle args = new Bundle();
                            args.putInt("tabIndex", 0);
                            intent.putExtras(args);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled - do nothing
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}