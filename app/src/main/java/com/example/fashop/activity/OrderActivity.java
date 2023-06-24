package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import Adapter.OrderItemAdapter;
import Model.CartItem;
import Model.Order;
import Model.OrderItem;
import MyClass.Constants;

public class OrderActivity extends AppCompatActivity{

    TextView UserName, Address, totalTxt, shippingCost, totalCost;
    EditText note;
    RecyclerView.Adapter adapter;
    RecyclerView OrderItemList;
    List<CartItem> cartItems = new ArrayList<>();
    List<OrderItem> orderItems = new ArrayList<>();
    Order currentOrder = null;
    Button ConfirmBtn;
    private int orderID;
    private int maxOrderID;
    private int maxOrderItemID;
    private double total;

    String shopAccountType = "AdminAndStaff";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        String cartItemsListKey = getIntent().getStringExtra("cart_items_list_key");
        cartItems = new Gson().fromJson(cartItemsListKey, new TypeToken<List<CartItem>>(){}.getType());

        total = getIntent().getDoubleExtra("total_key", 0);

        initView();
        ConfirmEvent();
    }
    private void initView(){
        OrderItemList = findViewById(R.id.cart_item_rcv);

        orderItems.clear();
        for (CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItem(cartItem);
            orderItems.add(orderItem);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        OrderItemList.setLayoutManager(linearLayoutManager);
        adapter = new OrderItemAdapter(orderItems);
        OrderItemList.setAdapter(adapter);

        UserName = findViewById(R.id.tvUserName);
        Address = findViewById(R.id.address);
        totalTxt = findViewById(R.id.totalTxt);
        totalTxt.setText(Double.toString(total + 1));
        shippingCost = findViewById(R.id.shippingCost);
        totalCost = findViewById(R.id.totalCost);
        totalCost.setText("$" + Double.toString(total));
        FirebaseAuth.getInstance().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            String city = ""+ds.child("city").getValue();
                            String district = ""+ds.child("district").getValue();
                            String ward = ""+ds.child("ward").getValue();
                            String street = ""+ds.child("streetAddress").getValue();
                            Address.setText(street + ", " + ward + ", " + district + ", " + city);

                            String name = ""+ds.child("name").getValue();
                            UserName.setText(name);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void ConfirmEvent(){
        ConfirmBtn = findViewById(R.id.checkoutBtn);

        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushOrder();
                pushOrderItems();
                Toast.makeText(getApplicationContext(), "Create Order Completed", Toast.LENGTH_LONG).show();
                onBackPressed();

            }
        });
    }
    private void pushOrder(){
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Order");
        // Retrieve current data from the database
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Order order = categorySnapshot.getValue(Order.class);
                    if (order != null && order.getID() > maxOrderID) {
                        maxOrderID = order.getID();
                    }
                }

                Order order = new Order();

                order.setID(++maxOrderID);

                order.setCustomerID(FirebaseAuth.getInstance().getUid());

                note = findViewById(R.id.note);
                order.setNote(String.valueOf(note.getText()));

                order.setStatus("PENDING");

                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // format of the string
                String formattedDate = currentDate.format(formatter);
                order.setDate(formattedDate);

                order.setTotal(total + 1);

                currentOrder = order;
                ref2.child(String.valueOf(order.getID())).setValue(order,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Log.v("OrderItem", "completed");
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void pushOrderItems(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("OrderItem");
        // Retrieve current data from the database
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                /*Query query = ref.orderByChild("orderID").equalTo(currentOrder.getID());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot variantSnapshot : snapshot.getChildren()) {
                            variantSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    OrderItem item = categorySnapshot.getValue(OrderItem.class);
                    if (item != null && item.getID() > maxOrderItemID) {
                        maxOrderItemID = item.getID();
                    }
                }

                for (OrderItem item : orderItems) {
                    item.setOrderID(currentOrder.getID());
                    item.setID(++maxOrderItemID);

                    OrderItem newOrderItem = new OrderItem(item);

                    ref.child(String.valueOf(newOrderItem.getID())).setValue(newOrderItem,
                            new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Log.v("OrderItem", "completed");
                                }
                            });
                }

                //send notification to admin

                prepareNotificationMessage(String.valueOf(currentOrder.getID()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void prepareNotificationMessage(String orderId){
        //send notification to admin

        //data
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String  NOTIFICATION_TITLE = "New Order " + orderId;
        String NOTIFICATION_MESSAGE = "Congratulations...! You have new order.";
        String NOTIFICATION_TYPE = "NewOrder";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();


        String currentCustomerUid = FirebaseAuth.getInstance().getUid();

        try {
            //content
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", currentCustomerUid);
            notificationBodyJo.put("shopAccountType", shopAccountType);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);


        } catch (JSONException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo, orderId);
    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send",
                notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start order details activity
                Intent intent = new Intent(OrderActivity.this, UserOrderDetailActivity.class);
                intent.putExtra("orderTo", shopAccountType);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if failed sending fcm, still start order details activity
                Intent intent = new Intent(OrderActivity.this, UserOrderDetailActivity.class);
                intent.putExtra("orderTo", shopAccountType);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
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