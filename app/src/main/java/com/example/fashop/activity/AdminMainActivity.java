package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;

import Adapter.CustomerAdapter;

public class AdminMainActivity extends AppCompatActivity {

    private ImageButton logoutBtn;
    ImageButton buttonPedit,buttonPadd,btnAddCategory, btnEditCategory, btnOrderMngm, btnStatistics, editInforBtn, staffManaBtn, settingBtn,csmManaBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    //
    private TextView tvUserName, tvUserEmail;
    private ImageView imgAvt;

    private LinearLayout staffFunction, statisticFunction;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        initUI();
        checkUser();


    }

    private void initUI() {
        logoutBtn = findViewById(R.id.logoutBtn);
        buttonPedit = findViewById(R.id.buttoneditPro);
        buttonPadd = findViewById(R.id.buttonaddPro);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        btnAddCategory = findViewById(R.id.buttonaddCa);
        btnEditCategory = findViewById(R.id.buttoneditCa);
        btnOrderMngm = findViewById(R.id.buttonOrderMngm);
        csmManaBtn = findViewById(R.id.csmmnbtn);
        btnStatistics = findViewById(R.id.buttonStatistics);
        editInforBtn = findViewById(R.id.editInfoBtn);
        //
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        imgAvt = findViewById(R.id.imgAvt);
        staffManaBtn = findViewById(R.id.staffManaBtn);
        settingBtn = findViewById(R.id.settingBtn);
        //

        functionalAuthor();
        checkUser();
        //
        initListener();
    }

    private void functionalAuthor(){
        staffFunction = findViewById(R.id.staffFunction);
        statisticFunction = findViewById(R.id.statisticFunction);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){

                            String accountType = "" + ds.child("accountType").getValue();
                            if (accountType.equals("Staff")){
                                staffFunction.setVisibility(View.GONE);
                                statisticFunction.setVisibility(View.GONE);
                            }
                            else if (accountType.equals("Admin")){
                                staffFunction.setVisibility(View.VISIBLE);
                                statisticFunction.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initListener() {
        buttonPedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, EditModelActivity.class));
            }
        });
        buttonPadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, AddModelActivity.class));
            }
        });

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(AdminMainActivity.this, AddCategoryActivity.class)));
            }
        });

        btnEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(AdminMainActivity.this, EditCategoryActivity.class)));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                makeMeOffline();
            }
        });
        btnOrderMngm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity((new Intent(AdminMainActivity.this, AdminOderManagement.class)));
            }
        });
        btnStatistics.setOnClickListener(v->{
            startActivity(new Intent(AdminMainActivity.this, StatisticsActivity.class));
        });
        csmManaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, CustomerManagement.class));
            }
        });
        //
        editInforBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){

                                    String accountType = "" + ds.child("accountType").getValue();
                                    if (accountType.equals("Staff")){
                                        Intent intent = new Intent(AdminMainActivity.this, ProfileEditUserActivity.class);
                                        startActivity(intent);
                                    }
                                    else if (accountType.equals("Admin")){
                                        Intent intent = new Intent(AdminMainActivity.this, ProfileEditUserActivity.class);
                                        startActivity(intent);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        staffManaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, StaffManagemantActivity.class));
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, SettingActivity.class));
            }
        });

        //

    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        for (DataSnapshot ds: datasnapshot.getChildren()){
                            String accountType = "" + ds.child("accountType").getValue();
                                String name = "" + ds.child("name").getValue();
                                String email = ""+ds.child("email").getValue();
                                String profileImage = ""+ds.child("profileImage").getValue();

                            if (accountType.equals("Admin"))
                            {
                                name += " (Administrator)";
                            }

                                tvUserName.setText(name);
                                tvUserEmail.setText(email);
                                try{
                                    Picasso.get().load(profileImage).placeholder(R.drawable.avt).into(imgAvt);
                                }
                                catch (Exception e){
                                    imgAvt.setImageResource(R.drawable.person_gray);
                                }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void makeMeOffline() {
        //after logging in, make user online
        progressDialog.setMessage("Logging Out...");

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("online", "false");


        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //update successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(AdminMainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUser() {
        FirebaseUser usr = firebaseAuth.getCurrentUser();
        if (usr == null) {
            startActivity(new Intent(AdminMainActivity.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

}