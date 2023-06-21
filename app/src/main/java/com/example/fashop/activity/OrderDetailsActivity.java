package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.OrderItemAdapter;
import Model.CartItem;
import Model.ModelImage;
import Model.Order;
import Model.OrderItem;
import Model.ProductModel;
import MyClass.Constants;

public class OrderDetailsActivity extends AppCompatActivity {
    TextView un, orderid, cd, address, nob, ordervalue;
    OrderItemAdapter adapter;
    RecyclerView OrderItemList;
    List<OrderItem> orderItems = new ArrayList<>();
    Button confirmbtn;
    ImageButton backbtn;
    Spinner spinner;
    ProgressDialog progressDialog;
    String selectedItem;
    Order order;

    String shopAccountType = "AdminAndStaff";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        un = findViewById(R.id.usernameTv);
        orderid = findViewById(R.id.orderidTv);
        cd = findViewById(R.id.createdDateTv);
        address =findViewById(R.id.address);
        nob = findViewById(R.id.note);
        ordervalue = findViewById(R.id.totalCost);
        OrderItemList = findViewById(R.id.order_item_rcv);
        spinner = findViewById(R.id.statusSpinner);
        confirmbtn = findViewById(R.id.confirm_button);
        backbtn = findViewById(R.id.backBtn);

        backbtn.setOnClickListener(v -> { finish();});

        getDetailOrderData();
    }
    void getDetailOrderData()
    {
        order = (Order) getIntent().getSerializableExtra("order");
        orderItems.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        OrderItemList.setLayoutManager(linearLayoutManager);
        adapter = new OrderItemAdapter(orderItems);
        OrderItemList.setAdapter(adapter);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(order.getCustomerID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String city = "" + ds.child("city").getValue();
                    String district = "" + ds.child("district").getValue();
                    String ward = "" + ds.child("ward").getValue();
                    String street = "" + ds.child("streetAddress").getValue();
                    address.setText("Address: " + street + ", " + ward + ", " + district + ", " + city);

                    String name = "User Name: " + ds.child("name").getValue();
                    un.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderDetailsActivity.this,"Failed to load user data. Try again later", Toast.LENGTH_LONG).show();
            }
        });

        String nt = "Note of buyer: " + order.getNote();
        nob.setText(nt);
        orderid.setText("Order ID: " + order.getID());
        cd.setText("Created Date: "+order.getDate());
        ordervalue.setText("$" + order.getTotal());

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("OrderItem");
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Variant");
        ref2.orderByChild("orderID").equalTo(order.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    OrderItem orderItem =  ds.getValue(OrderItem.class);

                    ref3.orderByChild("id").equalTo(orderItem.getVariantID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot hm: snapshot.getChildren())
                            {
                                orderItem.setColor(hm.child("color").getValue().toString());
                                orderItem.setSize(hm.child("size").getValue().toString());
                                adapter.notifyDataSetChanged();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ModelImage");


                                Query query2 = reference.orderByChild("modelID").equalTo(Integer.parseInt(hm.child("modelID").getValue().toString()));
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                            ModelImage image = modelSnapshot.getValue(ModelImage.class);
                                            orderItem.setImage(image.getUrl());
                                            adapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle errors here
                                    }
                                });

                                DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference("Model");

                                Query query3 = ref4.orderByChild("id").equalTo(Integer.parseInt(hm.child("modelID").getValue().toString()));
                                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                            ProductModel m = modelSnapshot.getValue(ProductModel.class);
                                            orderItem.setPrice(m.getPrice());
                                            adapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle errors here
                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    orderItems.add(orderItem);
                    adapter.notifyDataSetChanged();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<String> status = new ArrayList<>();
        status.add("PENDING");
        status.add("CONFIRMED");
        status.add("SHIPPING");
        status.add("COMPLETED");
        status.add("DECLINED");
        status.add("CANCELLED");
      
        ArrayAdapter<String> statusadapter = new ArrayAdapter<>(OrderDetailsActivity.this,
                R.layout.spinner_gray_layout, status);
        spinner.setAdapter(statusadapter);
        spinner.setSelection(statusadapter.getPosition(order.getStatus()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = spinner.getSelectedItem().toString();
                statusadapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference setref = FirebaseDatabase.getInstance().getReference("Order");
                order.setStatus(selectedItem);
                setref.child(String.valueOf(order.getID())).setValue(order,
                        new DatabaseReference.CompletionListener(){
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref)
                            {
                                Toast.makeText(getApplicationContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                                //send notification to customer
                                String message = "Order " + selectedItem;
                                prepareNotificationMessage(String.valueOf(order.getID()), message);
                                finish();
                            }

                        });
            }
        });
    }

    private void prepareNotificationMessage(String orderId, String message){


        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String  NOTIFICATION_TITLE = "Your Order " + orderId;
        String NOTIFICATION_MESSAGE = "" + message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            //content
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", order.getCustomerID());
            Log.e("buyerUid", order.getCustomerID());
            notificationBodyJo.put("shopAccountType", shopAccountType);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC); // to all
            notificationJo.put("data", notificationBodyJo);


        } catch (JSONException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send",
                notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //notification sent

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //notification failed

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + Constants.FCM_KEY);

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }
}