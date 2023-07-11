package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NewPasswordActivity extends AppCompatActivity {

    private EditText newPassEt, confirmPassEt;

    private Button resetBtn;

    private ProgressDialog progressDialog;

    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        newPassEt = findViewById(R.id.newpassdEt);
        confirmPassEt = findViewById(R.id.confirmdEt);
        resetBtn = findViewById(R.id.resetBtn);
        backBtn = findViewById(R.id.backBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassword();

            }
        });
    }

    String newPassword, confirmPassword;
    private void updateProfile() {
        progressDialog.setMessage("Changing your password...");
        progressDialog.show();

        //auth update
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG Password", "User password updated.");
                        }
                    }
                });

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("password", ""+newPassword);
        // save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.setMessage("Handling...");
                        progressDialog.dismiss();
                        Toast.makeText(NewPasswordActivity.this, "Change password successfully", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        startActivity(new Intent(NewPasswordActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating db
                        progressDialog.dismiss();
                        Toast.makeText(NewPasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkPassword() {
        newPassword = newPassEt.getText().toString().trim();
        confirmPassword = confirmPassEt.getText().toString().trim();
        if (newPassword.length() < 6){
            Toast.makeText(NewPasswordActivity.this, "Password must be at least 6 characters long...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)){
            Toast.makeText(NewPasswordActivity.this, "Password doesn't match...", Toast.LENGTH_SHORT).show();
            return;
        }

        updateProfile();
    }
}