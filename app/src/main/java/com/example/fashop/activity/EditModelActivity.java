package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.EditModelAdapter;
import Adapter.ModelAdapter;
import Model.ModelImage;
import Model.ProductCategory;
import Model.ProductModel;
import Model.ProductVariant;

public class EditModelActivity extends AppCompatActivity {
    private RecyclerView rcEditModel;
    private EditModelAdapter editModelAdapter;
    private List<ProductModel> modelList = new ArrayList<>();
    private List<ModelImage> modelImageList = new ArrayList<>();
    private List<ProductVariant> variantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_model);

        initUI();
    }

    private void initUI() {
        rcEditModel = findViewById(R.id.rcEditModel);
        loadModel();
    }

    private void loadModel() {
        GridLayoutManager manager = new GridLayoutManager(this, 2);

        // Adapter Category
        rcEditModel.setLayoutManager(manager);
        editModelAdapter = new EditModelAdapter(this, modelList);
        rcEditModel.setAdapter(editModelAdapter);
        getModelData();
    }

    private void getModelData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Model");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                modelList.clear();
                for (DataSnapshot child1: snapshot1.getChildren())
                {
                    ProductModel model = child1.getValue(ProductModel.class);
                    modelList.add(model);
                }
                joinData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ModelImage");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                modelImageList.clear();
                for (DataSnapshot child1: snapshot1.getChildren())
                {
                    ModelImage modelImage = child1.getValue(ModelImage.class);
                    modelImageList.add(modelImage);
                }
                joinData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Variant");
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                variantList.clear();
                for (DataSnapshot child1: snapshot1.getChildren())
                {
                    ProductVariant variant = child1.getValue(ProductVariant.class);
                    variantList.add(variant);
                }
                joinData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinData()
    {
        for (ProductModel model : modelList){
            int id = model.getID();
            List<String> urls = new ArrayList<>();
            for (ModelImage modelImage : modelImageList){
                if (modelImage.getModelID() == id)
                {
                    urls.add(modelImage.getUrl());
                }
            }

            List<String> colors = new ArrayList<>();
            List<String> sizes = new ArrayList<>();
            for (ProductVariant variant : variantList){
                if (variant.getModelID() == id)
                {
                    if (colors.indexOf(variant.getColor()) == -1){
                        colors.add(variant.getColor());
                    }
                    if (sizes.indexOf(variant.getSize()) == -1){
                        sizes.add(variant.getSize());
                    }
                }
            }
            model.setImages(urls);
            model.setColorList(colors);
            model.setSizeList(sizes);
        }
        editModelAdapter.notifyDataSetChanged();
    }
}