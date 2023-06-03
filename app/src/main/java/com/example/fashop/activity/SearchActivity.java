package com.example.fashop.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import Adapter.CheckboxItemAdapter;
import Adapter.ModelAdapter;
import Model.ModelImage;
import Model.ProductModel;


public class SearchActivity extends AppCompatActivity {
    EditText edtSearch2;
    private List<ProductModel> modelList2;
    private List<ProductModel> searchModel2 = new ArrayList<>();
    private List<ModelImage> modelImageList = new ArrayList<>();
    private RecyclerView rcModels2;
    private ModelAdapter modelAdapter2;

    private ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.filter_list);
        String[] items = {"Item 1", "Item 2", "Item 3", "Item 4"};
        CheckboxItemAdapter adapter = new CheckboxItemAdapter(this, R.layout.viewholder_checkbox_item, items);
        listView.setAdapter(adapter);

        String modelListJson = getIntent().getStringExtra("model_list_key");
        modelList2 = new Gson().fromJson(modelListJson, new TypeToken<List<ProductModel>>(){}.getType());

        rcModels2 = findViewById(R.id.rcModels2);

        edtSearch2 = findViewById(R.id.edtSearch2);
        edtSearch2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // This method will be invoked before the text is changed.
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
                GridLayoutManager manager = new GridLayoutManager(SearchActivity.this, 2);

                // Adapter Category
                rcModels2.setLayoutManager(manager);
                modelAdapter2 = new ModelAdapter(searchModel2);
                rcModels2.setAdapter(modelAdapter2);
            }
        });
        loadModel();
    }
    private void loadModel() {
        GridLayoutManager manager = new GridLayoutManager(SearchActivity.this, 2);

        // Adapter Category
        rcModels2.setLayoutManager(manager);
        modelAdapter2 = new ModelAdapter(modelList2);
        rcModels2.setAdapter(modelAdapter2);
        //getModelData();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }


    /*private void getModelData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Model");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null)
                {
                    modelList.add(model);
                    modelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null && modelList != null && !modelList.isEmpty())
                {
                    int len = modelList.size();

                    for (int i = 0; i < len; ++i)
                    {
                        if (modelList.get(i).getID() == model.getID())
                        {
                            model.setImages(modelList.get(i).getImages());
                            modelList.set(i, model);
                            modelAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null && modelList != null && !modelList.isEmpty())
                {
                    int len = modelList.size();
                    for (int i = 0; i < len; ++i)
                    {
                        if (modelList.get(i).getID() == model.getID())
                        {
                            modelList.remove(i);
                            modelAdapter.notifyDataSetChanged();
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
    }*/

    /*private void joinModelWithImage() {
        for (ProductModel model : modelList){
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
        modelAdapter.notifyDataSetChanged();
    }*/
}
