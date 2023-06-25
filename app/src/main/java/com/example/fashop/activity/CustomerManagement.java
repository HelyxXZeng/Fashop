package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.CustomerAdapter;
import Model.Order;
import Model.UserModel;

public class CustomerManagement extends AppCompatActivity {
    private RecyclerView rcCSM;
    private List<UserModel> userList = new ArrayList<>();
    private CustomerAdapter csAdapter;
    private TextView qtv;
    ImageButton backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_management);

        rcCSM = findViewById(R.id.rcCtmMana);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcCSM.setLayoutManager(manager);
        csAdapter = new CustomerAdapter(userList);
        rcCSM.setAdapter(csAdapter);
        qtv = findViewById(R.id.tvQuantity);
        backbtn = findViewById(R.id.backBtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadCSData();
    }

    private void loadCSData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(userList!=null) userList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (user != null && user.getAccountType().equals("User")) {
                            userList.add(user);
                            csAdapter.notifyDataSetChanged();
                        }
                        if (userList != null && !userList.isEmpty())
                            qtv.setText(String.valueOf(userList.size()));
                        else
                            qtv.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
        });
    }
}