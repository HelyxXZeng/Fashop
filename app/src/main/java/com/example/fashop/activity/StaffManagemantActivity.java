package com.example.fashop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import Adapter.StaffAdapter;
import Model.UserModel;

public class StaffManagemantActivity extends AppCompatActivity {

    private RecyclerView recycleViewStaffList;

    private List<UserModel> userList = new ArrayList<>();

    private StaffAdapter staffAdapter;

    private TextView quantityTv, addNewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_managemant);

        recycleViewStaffList = findViewById(R.id.rcStaffMana);
        loadStaff();

    }

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//
//        UserModel delUser = userList.get(item.getGroupId());
//        switch (item.getItemId()) {
//            case R.id.delete_item: {
//                // handle delete action
//                DatabaseReference Ref = FirebaseDatabase.getInstance().getReference();
//                Ref.child("Users").child(String.valueOf(delUser.getUid())).removeValue(new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                        Toast.makeText(StaffManagemantActivity.this, "Staff deleted", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                //delete Authentication
//
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                user.delete()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    // delete RealtimeDatabse
//                                }
//                            }
//                        });
//                return true;
//            }
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    private void loadStaff() {
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycleViewStaffList.setLayoutManager(manager);
        staffAdapter = new StaffAdapter(userList);
        recycleViewStaffList.setAdapter(staffAdapter);
        quantityTv = findViewById(R.id.tvQuantity);
        addNewBtn = findViewById(R.id.addNewBtn);

        initListener();
        getStaffData();

    }

    private void initListener() {
        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffManagemantActivity.this, RegisterUserActivity.class);
                intent.putExtra("userType", "Staff");
                startActivity(intent);
            }
        });
    }

    private void getStaffData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null && user.getAccountType().equals("Staff"))
                {
                    userList.add(user);
                    staffAdapter.notifyDataSetChanged();
                }
                if (userList != null && !userList.isEmpty())
                    quantityTv.setText(String.valueOf(userList.size()));
                else
                    quantityTv.setText("0");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null && user.getAccountType().equals("Staff")
                        && userList != null && !userList.isEmpty())
                {
                    int len = userList.size();

                    for (int i = 0; i < len; ++i)
                    {
                        if (userList.get(i).getUid() == user.getUid())
                        {
                            userList.set(i, user);
                            staffAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (user != null && user.getAccountType().equals("Staff")
                        && userList != null && !userList.isEmpty())
                {
                    int len = userList.size();
                    for (int i = 0; i < len; ++i)
                    {
                        if (userList.get(i).getUid() == user.getUid())
                        {
                            userList.remove(i);
                            staffAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                if (userList != null && !userList.isEmpty())
                    quantityTv.setText(String.valueOf(userList.size()));
                else
                    quantityTv.setText("0");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        ref.addChildEventListener(childEventListener);
    }
}