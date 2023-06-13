package com.example.fashop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.example.fashop.R;

import java.util.ArrayList;
import java.util.List;

import Model.Order;

public class AdminOderManagement extends AppCompatActivity {
    Button backbtn;
    RecyclerView rcOrerList;
    List<Order> orderList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_oder_management);

        rcOrerList = findViewById(R.id.rcOrderManament);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        
    }
}