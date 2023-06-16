package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.OrderItemAdapter;
import Model.CartItem;
import Model.Order;
import Model.OrderItem;

public class OrderDetailsActivity extends AppCompatActivity {
    TextView un, orderid, cd, address, nob, ordervalue;
    OrderItemAdapter adapter;
    RecyclerView OrderItemList;
    List<OrderItem> orderItems = new ArrayList<>();
    Button confirmbtn,denybtn;
    ImageButton backbtn;
    Order order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        un = findViewById(R.id.usernameTv);
        orderid = findViewById(R.id.orderidTv);
        cd = findViewById(R.id.createdDateTv);
        address =findViewById(R.id.address);
        nob = findViewById(R.id.note);
        ordervalue = findViewById(R.id.totalCost);
        OrderItemList = findViewById(R.id.order_item_rcv);

        confirmbtn = findViewById(R.id.confirm_button);
        denybtn = findViewById(R.id.deny_button);
        backbtn = findViewById(R.id.backBtn);

        backbtn.setOnClickListener(v -> { finish();});

        getDetailOrderData();
    }
    void getDetailOrderData()
    {
        order = (Order) getIntent().getSerializableExtra("order");
        orderItems.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(order.getCustomerID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String city = "" + ds.child("city").getValue();
                    String district = "" + ds.child("district").getValue();
                    String ward = "" + ds.child("ward").getValue();
                    String street = "" + ds.child("streetAddress").getValue();
                    address.setText("Address: " + street + ", " + ward + ", " + district + ", " + city);

                    String name = "User Name: " + ds.child("name").getValue();
                    un.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailsActivity.this,"Failed to load user data. Try again later", Toast.LENGTH_LONG).show();
            }
        });

        String nt = "Note of buyer: " + order.getNote();
        nob.setText(nt);
        orderid.setText("Order ID: " + order.getID());
        cd.setText("Created Date: "+order.getDate());
        ordervalue.setText("$" + order.getTotal());

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("OrderItem");
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Variant");
        ref2.orderByChild("orderID").equalTo(order.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    OrderItem orderItem =  new OrderItem(ds.getValue(OrderItem.class));

                    ref3.orderByChild("id").equalTo(orderItem.getVariantID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot hm: snapshot.getChildren())
                            {
                                orderItem.setColor(hm.child("color").getValue().toString());
                                orderItem.setSize(hm.child("size").getValue().toString());

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ModelImage");
                                reference.orderByChild("modelID").equalTo(hm.child("modelID").getValue().toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot e: snapshot.getChildren()) {
                                            Log.v("test", e.child("url").getValue().toString());
                                            orderItem.setImage(e.child("url").getValue().toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    orderItems.add(orderItem);
                }

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
                OrderItemList.setLayoutManager(linearLayoutManager);
                adapter = new OrderItemAdapter(orderItems);
                OrderItemList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}