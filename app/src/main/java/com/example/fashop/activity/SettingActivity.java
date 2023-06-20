package com.example.fashop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.fashop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import MyClass.Constants;
//import com.google.firebase.messaging.FirebaseMessaging;
//
//import MyClass.Constants;

public class SettingActivity extends AppCompatActivity {


    private SwitchCompat fcmSwitch;
    TextView notificationStatusTv, delBtn;

    private static final String enableMessage = "Notification are enabled";
    private static final String disabledMessage = "Notification are disable";

    private boolean isChecked = false;

    private FirebaseAuth firebaseAuth;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        fcmSwitch = findViewById(R.id.fcmSwitch);
        notificationStatusTv = findViewById(R.id.notificationStatusTv);
        delBtn = findViewById(R.id.delBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        //init shared preferences
        sp = getSharedPreferences("SETTINGS_SP", MODE_PRIVATE);
        //check last selected option; true/false
        isChecked = sp.getBoolean("FCM_ENABLED", false);
        fcmSwitch.setChecked(isChecked);
        if (isChecked){
            notificationStatusTv.setText(enableMessage);
        }
        else {
            notificationStatusTv.setText(disabledMessage);
        }

        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //checked, enable notifications
                    subscribeToTopic();
                }
                else {
                    //uncheck, disable notifications
                    unSubscribeToTopic();
                }
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });
    }

    private void deleteAccount(){


                // Create a dialog builder.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Set the title of the dialog.
                builder.setTitle("Delete Account");

                // Set the message of the dialog.
                builder.setMessage("Are you sure you want to delete this account?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();
                        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        if (Ref.child(uid) != null){
                            Ref.child(uid).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                }
                            });
                        }

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingActivity.this, "Staff account deleted", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                        else {
                                            showReauthenticationDialog();
                                        }
                                    }
                                });

                    }
                });

                // Add a negative button to the dialog.
                builder.setNegativeButton("Cancel", null);

                // Create the dialog and show it.
                AlertDialog dialog = builder.create();
                dialog.show();
    }

    private void showReauthenticationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ReAuthentication");

        // Inflate the layout for the dialog
        View view = getLayoutInflater().inflate(R.layout.reauthentication_dialog, null);
        builder.setView(view);

        // Get references to the email and password EditText fields in the dialog layout
        EditText emailEditText = view.findViewById(R.id.emailEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate and use the entered email and password to create the credential
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    reauthenticateUser(credential);
                } else {
                    // Handle case when email or password is empty
                    Toast.makeText(SettingActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private void reauthenticateUser(AuthCredential credential){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deleteAccount();
                    }
                });

    }


    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //save setting in shared preferences
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", true);
                        spEditor.apply();

                        Toast.makeText(SettingActivity.this, ""+enableMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(enableMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unSubscribeToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //save setting in shared preferences
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", false);
                        spEditor.apply();

                        Toast.makeText(SettingActivity.this, ""+disabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(disabledMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}