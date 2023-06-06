package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.ModelAdapter;
import Fragment.ProductVariantFragment;
import Model.ModelImage;
import Model.ProductCategory;
import Model.ProductModel;

public class ListModelOfCategoryActivity extends AppCompatActivity {
    private RecyclerView rcModel;
    private ModelAdapter modelAdapter;
    private List<ProductModel> modelList = new ArrayList<>();
    private ProductCategory category;
    private TextView tvCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_model_of_category);

        initUI();
        getBundle();
        getData();
        loadModel();
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference modelsRef = database.getReference("Model");
        Query query = modelsRef.orderByChild("categoryID").equalTo(category.getID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate over the children of the "Model" node
                for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                    ProductModel model = modelSnapshot.getValue(ProductModel.class);
                    // Do something with each model
                    DatabaseReference imgRef = database.getReference("ModelImage");
                    Query query2 = imgRef.orderByChild("modelID").equalTo(model.getID());
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<String> urls = new ArrayList<>();
                            for (DataSnapshot modelImageSnapshot : snapshot.getChildren()){
                                ModelImage modelImage = modelImageSnapshot.getValue(ModelImage.class);
                                urls.add(modelImage.getUrl());
                            }
                            model.setImages(urls);
                            modelList.add(model);
                            modelAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void loadModel() {
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 2);

        // Adapter Category
        rcModel.setLayoutManager(manager);
        modelAdapter = new ModelAdapter(modelList);
        rcModel.setAdapter(modelAdapter);
    }

    private void initUI() {
        rcModel = findViewById(R.id.rcModel);
        tvCategoryName = findViewById(R.id.tvCategoryName);
    }

    private void getBundle(){
        category = (ProductCategory)  getIntent().getSerializableExtra("category");
        tvCategoryName.setText(category.getName());

    }
}