package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView passwordIcon;

    private EditText passwordEt;

    private Button verifyBtn;

    private ProgressDialog progressDialog;

    private boolean showPassword = false;

    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        passwordIcon = findViewById(R.id.passwordIcon);
        passwordEt = findViewById(R.id.passwordEt);
        verifyBtn = findViewById(R.id.verifyBtn);
        backBtn = findViewById(R.id.backBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showPassword)
                {
                    showPassword = false;
                    passwordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.showpass);
                }
                else {
                    showPassword = true;
                    passwordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.hidepass);
                }

            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                progressDialog.setMessage("Verifying your account...");
//                progressDialog.show();
                String passwordEnter = passwordEt.getText().toString().trim();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            String passwordAcc = ""+ds.child("password").getValue();
                                            Log.e("passwordAcc", passwordAcc);
                                            if (passwordAcc.equals(passwordEnter)){
//                                                progressDialog.dismiss();
                                                startActivity(new Intent(ChangePasswordActivity.this, NewPasswordActivity.class));
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(ChangePasswordActivity.this, "The wrong password, please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
            }
        });
    }
}