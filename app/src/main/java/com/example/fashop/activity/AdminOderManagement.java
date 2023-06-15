package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.OrderListAdapter;
import Model.Order;

public class AdminOderManagement extends AppCompatActivity {
    ImageButton backbtn;
    RecyclerView rcOrerList;
    OrderListAdapter orderListAdapter;
    List<Order> orderList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_oder_management);

        rcOrerList = findViewById(R.id.rcOrderManament);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        backbtn = findViewById(R.id.backBtn);
        rcOrerList.setLayoutManager(llm);
        orderListAdapter = new OrderListAdapter(this, orderList);
        rcOrerList.setAdapter(orderListAdapter);
        getOrdersData();

        backbtn.setOnClickListener(v -> { finish();});
    }
    void getOrdersData()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Order");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order dataModel = snapshot.getValue(Order.class);
                    orderList.add(dataModel);
                }

                orderListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}







