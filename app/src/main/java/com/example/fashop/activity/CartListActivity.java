package com.example.fashop.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Adapter.CartItemAdapter;
//import Adapter.CartListAdapter;
import Interface.ChangNumberItemsListener;
import Model.CartItem;
import Model.ModelImage;
import Model.ProductModel;
import Model.ProductVariant;
//import MyClass.ManagementCart;

public class CartListActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewCartItemList;
//    private ManagementCart managementCart;

    TextView totalTxt, emptyTxt;

    ImageButton backBtn;
    private ScrollView scrollView;

    private double totalOrder = 0;

    List<CartItem> cartItemList = new ArrayList<>();

    List<ModelImage> modelImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

//        managementCart = new ManagementCart(this);
        initView();
        LoadCartItems();
        BackEvent();
        //bottomNavigation();
    }

    private void BackEvent(){
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initView(){
        recyclerViewCartItemList=findViewById(R.id.cart_item_rcv);
        totalTxt=findViewById(R.id.totalTxt);
//        taxTxt=findViewById(R.id.taxTxt);
//        deliveryTxt=findViewById(R.id.deliveryTxt);
//        totalTxt=findViewById(R.id.totalTxt);
        emptyTxt=findViewById(R.id.emptyTxt);
        scrollView=findViewById(R.id.scrollView3);
//        recyclerViewList=findViewById(R.id.cartView);
    }


    private void LoadCartItems(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewCartItemList.setLayoutManager(linearLayoutManager);
        adapter = new CartItemAdapter(cartItemList);
        recyclerViewCartItemList.setAdapter(adapter);
        getCartItemData();
    }

    private void getCartItemData(){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CartItem");
            String uidUser = FirebaseAuth.getInstance().getUid();
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                    if (cartItem != null && cartItem.getCustomerID().equals(uidUser))
                    {
                        cartItemList.add(cartItem);
                        getOtherData(cartItem);
                        adapter.notifyDataSetChanged();
                    }
                    if (cartItemList.isEmpty()){
                        emptyTxt.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                    } else {
                        emptyTxt.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                    if (cartItem.getCustomerID().equals(uidUser)) {

                        if (cartItem != null && cartItemList != null && !cartItemList.isEmpty())
                        {

                            int len = cartItemList.size();

                            for (int i = 0; i < len; ++i)
                            {
                                if (cartItemList.get(i).getID() == cartItem.getID())
                                {

                                    CartItem currentData = cartItemList.get(i);
                                    totalOrder = Math.round((totalOrder - currentData.getPrice() * currentData.getQuantity()) * 100.0) / 100.0;
                                    cartItem.setProductName(currentData.getProductName());
                                    cartItem.setPrice(currentData.getPrice());
                                    cartItem.setColor(currentData.getColor());
                                    cartItem.setSize(currentData.getSize());
                                    cartItem.setImage(currentData.getImage());
                                    cartItemList.set(i, cartItem);
                                    adapter.notifyDataSetChanged();
                                    totalOrder = Math.round((totalOrder + cartItem.getPrice() * cartItem.getQuantity())  * 100.0) / 100.0;
                                    totalTxt.setText(String.format("%.2f", totalOrder));
                                    break;
                                }
                            }
                        }

                    }


                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                    ProductModel model = dataSnapshot.getValue(ProductModel.class);
//                    if (model != null && cartItemList != null && !cartItemList.isEmpty())
//                    {
//                        int len = cartItemList.size();
//                        for (int i = 0; i < len; ++i)
//                        {
//                            if (cartItemList.get(i).getID() == model.getID())
//                            {
//                                cartItemList.remove(i);
//                                adapter.notifyDataSetChanged();
//                                break;
//                            }
//                        }
//                    }
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

    private void getOtherData(CartItem cartItem){
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Variant" + "/" + cartItem.getVariantID());
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductVariant productVariant = snapshot.getValue(ProductVariant.class);

                if (productVariant != null){
                    //get color size
                    String size = productVariant.getSize();
                    String color = productVariant.getColor();
                    for (CartItem item : cartItemList){
                        if (item.getID() == cartItem.getID())
                        {
                            item.setSize(size);
                            item.setColor(color);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();

                    //

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Model" + "/" + productVariant.getModelID());
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);

                            if (productModel != null){
                                //get color size
                                Double price = productModel.getPrice();
                                totalOrder = Math.round((totalOrder + price * cartItem.getQuantity()) * 100.0) / 100.0;
                                totalTxt.setText(String.format("%.2f", totalOrder));

                                String title = productModel.getName();
                                for (CartItem item : cartItemList){
                                    if (item.getID() == cartItem.getID())
                                    {
                                        item.setPrice(price);
                                        item.setProductName(title);
                                        break;
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(CartListActivity.this, "get size list failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //get image infor
                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("ModelImage");
                    ref3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ModelImage modelImage = dataSnapshot.getValue(ModelImage.class);
                                if (modelImage != null && modelImage.getModelID() == productVariant.getModelID()){
                                    cartItem.setImage(modelImage.getUrl());
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(CartListActivity.this, "get size list failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}