package com.example.fashop.activity;

import static Adapter.EditCategoryAdapter.currentPosition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.EditCategoryAdapter;
import Model.ProductCategory;

public class EditCategoryActivity extends AppCompatActivity {
    private EditCategoryAdapter editCategoryAdapter;
    private List<ProductCategory> categoryList = new ArrayList<>();
    private RecyclerView rcEditCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        initUI();
    }

    private void initUI() {
        getCategoryData();
        editCategoryAdapter = new EditCategoryAdapter(this, categoryList);
        rcEditCategory = findViewById(R.id.rcEditCategory);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        rcEditCategory.setLayoutManager(manager);
        rcEditCategory.setAdapter(editCategoryAdapter);
    }

    private void getCategoryData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                categoryList.clear();
                for (DataSnapshot child1: snapshot1.getChildren())
                {
                    ProductCategory category = child1.getValue(ProductCategory.class);
                    categoryList.add(category);
                }
                editCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            //get picked image
            Uri uri = data.getData();
            //set to imageview
            categoryList.get(currentPosition).setImg(uri.toString());
            editCategoryAdapter.notifyItemChanged(currentPosition);
        }
    }
}