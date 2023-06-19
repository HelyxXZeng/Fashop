package com.example.fashop.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import Adapter.CategoryAdapter;
import Adapter.CheckboxItemAdapter;
import Adapter.ModelAdapter;
import Model.ModelImage;
import Model.ProductCategory;
import Model.ProductModel;


public class SearchActivity extends AppCompatActivity {
    EditText edtSearch2;
    private List<ProductModel> modelList2 = new ArrayList<>();
    private List<ModelImage> modelImageList = new ArrayList<>();
    private List<ProductModel> searchModel2 = new ArrayList<>();
    private List<ProductModel> loadModel = new ArrayList<>();
    private List<ProductCategory> categories = new ArrayList<>();
    private RecyclerView rcModels2;
    private ModelAdapter modelAdapter2;
    private ListView listView;
    CheckboxItemAdapter adapter;
    boolean isListViewVisible = false;
    private int page;
    private int size;
    boolean checked[];
    int id[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button showListViewButton = findViewById(R.id.filter);

        showListViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListViewVisible) {
                    // hide the ListView if it is currently visible
                    listView.setVisibility(View.GONE);
                    isListViewVisible = false;
                } else {
                    // show the ListView if it is currently hidden
                    listView.setVisibility(View.VISIBLE);
                    isListViewVisible = true;
                }
            }
        });
        /*// Load Model
        String modelListJson = getIntent().getStringExtra("model_list_key");
        modelList2 = new Gson().fromJson(modelListJson, new TypeToken<List<ProductModel>>(){}.getType());

        // Load categories
        String categoriesListJson = getIntent().getStringExtra("categories_list_key");
        categories = new Gson().fromJson(categoriesListJson, new TypeToken<List<ProductCategory>>(){}.getType());*/

        SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);
        /*String json = sharedPreferences.getString("model_list_key", "");
        modelList2 = new ArrayList<>();
        if (!json.isEmpty()) {
            Type type = new TypeToken<List<ProductModel>>(){}.getType();
            modelList2 = new Gson().fromJson(json, type);
        }
        String json2 = sharedPreferences.getString("categories_list_key", "");
        categories = new ArrayList<>();
        if (!json2.isEmpty()) {
            Type type = new TypeToken<List<ProductCategory>>(){}.getType();
            categories = new Gson().fromJson(json2, type);
        }*/
        getCategoryData();
        getModelData();


        //make listview
        listView = (ListView) findViewById(R.id.filter_list);
        listView.setBackgroundColor(Color.WHITE);

        rcModels2 = findViewById(R.id.rcModels2);

        edtSearch2 = findViewById(R.id.edtSearch2);
        edtSearch2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // This method will be invoked before the text is changed.
                id = adapter.getIDItems();
                checked = adapter.getCheckedItems();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method will be invoked whenever the text is changed.
                if (charSequence.toString() == "") loadModel();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method will be invoked after the text has been changed.
                String[] searchText = editable.toString().split(" ");
                searchModel2.clear();
                for (ProductModel models : modelList2)
                {
                    for (String text : searchText)
                    {
                        if(models.getName().toLowerCase().contains(text.toLowerCase()))
                        {
                            searchModel2.add(models);
                        }
                    }
                }

                loadModel.clear();

                for (ProductModel model : searchModel2) {
                    for (int i = 0; i < id.length; i++){
                        if (model.getCategoryID() == id[i]){
                            if (checked[i]) loadModel.add(model);
                        }
                    }
                }
                loadModelList();
            }
        });
    }
    private void loadModel() {
        GridLayoutManager manager = new GridLayoutManager(SearchActivity.this, 2);

        // Adapter Category
        rcModels2.setLayoutManager(manager);
        modelAdapter2 = new ModelAdapter(modelList2);
        rcModels2.setAdapter(modelAdapter2);
    }

    private void loadModelList()
    {
        GridLayoutManager manager = new GridLayoutManager(SearchActivity.this, 2);

        // Adapter Category
        rcModels2.setLayoutManager(manager);
        modelAdapter2 = new ModelAdapter(loadModel);
        rcModels2.setAdapter(modelAdapter2);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();

    }

    private void getModelData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Model");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null)
                {
                    modelList2.add(model);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null && modelList2 != null && !modelList2.isEmpty())
                {
                    int len = modelList2.size();

                    for (int i = 0; i < len; ++i)
                    {
                        if (modelList2.get(i).getID() == model.getID())
                        {
                            model.setImages(modelList2.get(i).getImages());
                            modelList2.set(i, model);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null && modelList2 != null && !modelList2.isEmpty())
                {
                    int len = modelList2.size();
                    for (int i = 0; i < len; ++i)
                    {
                        if (modelList2.get(i).getID() == model.getID())
                        {
                            modelList2.remove(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ModelImage");
        ChildEventListener childEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ModelImage modelImage = dataSnapshot.getValue(ModelImage.class);
                if (modelImage != null)
                {
                    modelImageList.add(modelImage);
                    joinModelWithImage();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                ModelImage modelImage = dataSnapshot.getValue(ModelImage.class);
                if (modelImage != null && modelImageList != null && !modelImageList.isEmpty())
                {
                    int len = modelImageList.size();
                    for (int i = 0; i < len; ++i)
                    {
                        if (modelImageList.get(i).getID() == modelImage.getID())
                        {
                            modelImageList.set(i, modelImage);
                            joinModelWithImage();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ModelImage modelImage = dataSnapshot.getValue(ModelImage.class);
                if (modelImage != null && modelImageList != null && !modelImageList.isEmpty())
                {
                    int len = modelImageList.size();
                    for (int i = 0; i < len; ++i)
                    {
                        if (modelImageList.get(i).getID() == modelImage.getID())
                        {
                            modelImageList.remove(i);
                            joinModelWithImage();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        ref.addChildEventListener(childEventListener);
        ref2.addChildEventListener(childEventListener2);
    }

    private void joinModelWithImage() {
        for (ProductModel model : modelList2){
            int id = model.getID();
            List<String> urls = new ArrayList<>();
            for (ModelImage modelImage : modelImageList){
                if (modelImage.getModelID() == id)
                {
                    urls.add(modelImage.getUrl());
                }
            }
            model.setImages(urls);
        }
        loadModel();
    }

    private void getCategoryData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                categories.clear();
                for (DataSnapshot child1: snapshot1.getChildren())
                {
                    ProductCategory category = child1.getValue(ProductCategory.class);
                    categories.add(category);
                }
                adapter = new CheckboxItemAdapter(SearchActivity.this, R.layout.viewholder_checkbox_item, categories);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(SearchActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
