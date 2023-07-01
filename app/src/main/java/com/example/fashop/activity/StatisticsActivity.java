package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Adapter.EditModelAdapter;
import Adapter.ModelStatisticsAdapter;
import Model.ModelImage;
import Model.Order;
import Model.OrderItem;
import Model.ProductCategory;
import Model.ProductModel;
import Model.ProductVariant;

public class StatisticsActivity extends AppCompatActivity {
    private List<ProductModel> modelList = new ArrayList<>();
    private List<OrderItem> orderItemList = new ArrayList<>();
    private RecyclerView rcStatistics;
    private ModelStatisticsAdapter adapter;
    private TextView tvNumberOfCustomers;
    private TextView tvNumberOfModels;
    private TextView tvRevenue;
    private boolean isDoneOrderItem = false;
    private Button btnGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initView();
        getData();
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref2 = database.getReference("OrderItem");


        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderItemList.clear();
                for (DataSnapshot orderItemSnapshot : dataSnapshot.getChildren()) {
                    OrderItem orderItem = orderItemSnapshot.getValue(OrderItem.class);

                    // Do something with the retrieved OrderItem objects
                    orderItemList.add(orderItem);
                }
                isDoneOrderItem = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        //-----------

        DatabaseReference ref = database.getReference("Model");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                modelList.clear();
                for (DataSnapshot child1: snapshot1.getChildren())
                {
                    ProductModel model = child1.getValue(ProductModel.class);

                    // get quantity and rate
                    DatabaseReference variantRef = database.getReference("Variant");
                    Query variantQuery = variantRef.orderByChild("modelID").equalTo(model.getID());

                    List<Integer> variantIds = new ArrayList<>();
                    variantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            variantIds.clear();
                            for (DataSnapshot variantSnapshot : dataSnapshot.getChildren()) {
                                ProductVariant variant = variantSnapshot.getValue(ProductVariant.class);
                                variantIds.add(variant.getID());
                            }

                            long quantity = 0;
                            float rate = 0;
                            long count = 0;

                            while (isDoneOrderItem == false);
                            for (OrderItem orderItem : orderItemList){
                                if (variantIds.contains(orderItem.getVariantID())){
                                    quantity += orderItem.getQuantity();
                                    if (orderItem.getRate() != 0){
                                        rate += orderItem.getRate();
                                        ++count;
                                    }
                                }
                            }
                            model.setQuantity(quantity);
                            model.setRate(rate/count);
                            adapter.notifyDataSetChanged();

//                            // Step 3: Query all OrderItem objects that reference any of the retrieved Variant objects.
//                            DatabaseReference ref = database.getReference("OrderItem");
//
//
//                            ref.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    long quantity = 0;
//                                    float rate = 0;
//                                    long count = 0;
//                                    for (DataSnapshot orderItemSnapshot : dataSnapshot.getChildren()) {
//                                        OrderItem orderItem = orderItemSnapshot.getValue(OrderItem.class);
//
//                                        // Do something with the retrieved OrderItem objects
//                                        for (int variantId : variantIds){
//                                            if (variantId == orderItem.getVariantID()){
//                                                quantity += orderItem.getQuantity();
//                                                if (orderItem.getRate() != 0){
//                                                    rate += orderItem.getRate();
//                                                    ++count;
//                                                }
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    model.setQuantity(quantity);
//                                    model.setRate(rate/count);
////                                    totalRevenue += model.getQuantity() * model.getPrice();
////                                    String formattedNumber = String.format("%.1f", totalRevenue);
////                                    tvRevenue.setText(formattedNumber);
//                                    adapter.notifyDataSetChanged();
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    // Handle error
//                                }
//                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });

                    DatabaseReference imgRef = database.getReference("ModelImage");
                    Query query2 = imgRef.orderByChild("modelID").equalTo(model.getID());
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                ModelImage image = modelSnapshot.getValue(ModelImage.class);
                                List<String> urls = new ArrayList<>();
                                urls.add(image.getUrl());
                                model.setImages(urls);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors here
                        }
                    });

                    DatabaseReference categoryRef = database.getReference("Category");
                    Query query = categoryRef.orderByChild("id").equalTo(model.getCategoryID());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                ProductCategory category = modelSnapshot.getValue(ProductCategory.class);
                                model.setCategory(category.getName());
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors here
                        }
                    });
                    modelList.add(model);
                    adapter.notifyDataSetChanged();
                }

                if (modelList.size() > 0){
                    tvNumberOfModels.setText(String.valueOf(modelList.size()));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference variantRef = database.getReference("Users");
        Query query = variantRef.orderByChild("accountType").equalTo("User");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvNumberOfCustomers.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference orderRef = database.getReference("Order");
        Query queryOrder = orderRef.orderByChild("status").equalTo("COMPLETED");
        queryOrder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalRevenue = 0;
                for (DataSnapshot orderSnapshot : snapshot.getChildren()){
                    Order order = orderSnapshot.getValue(Order.class);
                    totalRevenue += order.getTotal() - 1;
                }
                String formattedNumber = String.format("%.1f", totalRevenue);
                tvRevenue.setText(formattedNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        rcStatistics = findViewById(R.id.rcStatistics);
        tvNumberOfCustomers = findViewById(R.id.tvNumberOfCustomers);
        tvNumberOfModels = findViewById(R.id.tvNumberOfModels);
        tvRevenue = findViewById(R.id.tvRevenue);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        rcStatistics.setLayoutManager(manager);
        adapter = new ModelStatisticsAdapter(this, modelList);
        rcStatistics.setAdapter(adapter);
        btnGraph = findViewById(R.id.btnGraph);
        btnGraph.setOnClickListener(v->{
            Intent intent = new Intent(StatisticsActivity.this, StatisticsGraphActivity.class);
            startActivity(intent);
        });
    }
}