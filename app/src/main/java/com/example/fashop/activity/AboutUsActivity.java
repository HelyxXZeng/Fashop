package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.fashop.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.UserModel;

public class AboutUsActivity extends AppCompatActivity {
    private TextView tvEmail, tvPhone, tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initUI();
    }

    private void initUI() {
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
//        tvContent2 = findViewById(R.id.tvSecondHelpContent);
//
//        tvTitle1.setOnClickListener(view -> {
//            if (tvContent1.getVisibility() == View.GONE)
//            {
//                tvContent1.setVisibility(View.VISIBLE);
//            }
//            else
//            {
//                tvContent1.setVisibility(View.GONE);
//            }
//        });
//
//        tvTitle2.setOnClickListener(view -> {
//            if (tvContent2.getVisibility() == View.GONE)
//            {
//                tvContent2.setVisibility(View.VISIBLE);
//            }
//            else
//            {
//                tvContent2.setVisibility(View.GONE);
//            }
//        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    UserModel userModel = ds.getValue(UserModel.class);
                    if (userModel != null && userModel.getAccountType().equals("Admin")){
                        tvEmail.setText("Email: " + userModel.getEmail());
                        tvPhone.setText("Hotline: " + userModel.getPhone());
                        tvAddress.setText("Address: " + userModel.getWard() + ", "
                                + userModel.getDistrict() + ", "
                                + userModel.getCity());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}