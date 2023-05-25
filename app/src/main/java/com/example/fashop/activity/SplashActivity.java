package com.example.fashop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //disable darkmode of device
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //setContentView(R.layout.activity_splash);

        //make fullscreen
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        //start login activity after 2sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null){
                    //user not logged in start login activity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
                else{
                    //user is logged in, check user type
                    checkUserType();
                }
            }
        }, 300);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(homeIntent);
//                finish();
//            }
//        }, 500); // 500 là thời gian đợi tính bằng mili giây (0.5 giây)
    }

    private void checkUserType() {
        // if user is seller, start seller main screen
        // if user is buyer, start user mai screen

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String accountType = ""+ds.child("accountType");
                            if (accountType.contains("Admin")){
                                //user is seller

                                startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
//                                Intent intent = new Intent(SplashActivity.this, MainUserActivity.class);
//                                intent.putExtra("navigateToHomeFragment", true);
//                                startActivity(intent);
                                finish();
                            }
                            else{
                                //user is buyer
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                                Intent intent = new Intent(SplashActivity.this, MainUserActivity.class);
//                                intent.putExtra("navigateToHomeFragment", true);
//                                startActivity(intent);
                                finish();
                            }
                        }
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}