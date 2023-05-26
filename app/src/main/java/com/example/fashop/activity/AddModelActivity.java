package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fashop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import Adapter.SimpleImageAdapter;
import Adapter.SimpleStringAdapter;
import Model.ModelImage;
import Model.ProductCategory;
import Model.ProductModel;
import Model.ProductVariant;
import MyClass.ClothingDomain;

public class AddModelActivity extends AppCompatActivity {
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private EditText edtColor, edtSize, edtModelName, edtModelPrice, edtDescription;
    private Button btnAddColor, btnAddSize, btnAddModel, btnAddImage, btnDeleteModel;
    private RecyclerView rcColor, rcSize, rcImage;
    private Spinner spinnerCategory, spinnerSex;
    private ProductModel editModel = null;

    private List<ProductCategory> categoryList = new ArrayList<>();
    private SimpleStringAdapter colorAdapter, sizeAdapter;
    private ArrayAdapter<String> categoryAdapter, sexAdapter;
    private SimpleImageAdapter imageAdapter;
    private ProductCategory selectedCategory;
    private List<String> colorList = new ArrayList<>(), sizeList = new ArrayList<>(), categoryStringList = new ArrayList<>();
    private List<String> sexList = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();
    private int maxVariantId = 0;
    private int maxModelImageId = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_model);

        initUI();
//        getBundle();
    }

    private void initUI() {
//        getCategoryData();
        edtColor = findViewById(R.id.edtColor);
        edtSize = findViewById(R.id.edtSize);
        edtModelName = findViewById(R.id.edtModelName);
        edtModelPrice = findViewById(R.id.edtModelPrice);
        edtDescription = findViewById(R.id.edtDescription);
        btnAddColor = findViewById(R.id.btnAddColor);
        btnAddSize = findViewById(R.id.btnAddSize);
        btnAddModel = findViewById(R.id.btnAddModel);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnDeleteModel = findViewById(R.id.btnDeleteModel);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSex = findViewById(R.id.spinnerSex);
        rcColor = findViewById(R.id.rcColor);
        rcSize = findViewById(R.id.rcSize);
        rcImage = findViewById(R.id.rcModelImage);

        colorAdapter = new SimpleStringAdapter(getApplicationContext(), colorList);
        sizeAdapter = new SimpleStringAdapter(getApplicationContext(), sizeList);

        rcColor.setAdapter(colorAdapter);
        rcSize.setAdapter(sizeAdapter);

        imageAdapter = new SimpleImageAdapter(getApplicationContext(), urlList);
        rcImage.setAdapter(imageAdapter);

        sexList.add("male");
        sexList.add("female");
        sexList.add("unisex");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        LinearLayoutManager manager1 = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        LinearLayoutManager manager2 = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        rcColor.setLayoutManager(manager);
        rcSize.setLayoutManager(manager1);
        rcImage.setLayoutManager(manager2);

        categoryAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, categoryStringList);
        spinnerCategory.setAdapter(categoryAdapter);

        sexAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, sexList);
        sexAdapter.notifyDataSetChanged();
        spinnerSex.setAdapter(sexAdapter);

        getCategoryData();
        initListener();
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
                    categoryStringList.add(category.getName());
                }
                categoryAdapter.notifyDataSetChanged();
                getBundle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Failed to get category data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListener() {
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedString = spinnerCategory.getSelectedItem().toString();
                selectedCategory = categoryList.stream()
                        .filter(c -> c.getName().equals(selectedString))
                        .findFirst().get();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnAddColor.setOnClickListener(view -> {
            colorList.add(edtColor.getText().toString());
            colorAdapter.notifyDataSetChanged();
            edtColor.setText("");
        });

        btnAddSize.setOnClickListener(view -> {
            sizeList.add(edtSize.getText().toString());
            sizeAdapter.notifyDataSetChanged();
            edtSize.setText("");
        });

        btnAddImage.setOnClickListener(v->{
            pickFromGallery();
        });

        btnDeleteModel.setOnClickListener(v->{
            deleteModel();
        });

        btnAddModel.setOnClickListener(v->{
            progressDialog.show();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Model");
            if (editModel == null){
                // Retrieve current data from the database
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int maxId = 0;
                        for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                            ProductModel model = categorySnapshot.getValue(ProductModel.class);
                            if (model != null && model.getID() > maxId) {
                                maxId = model.getID();
                            }
                        }

                        // Add the new category with the incremented ID
                        ProductModel model = new ProductModel();
                        model.setID(maxId + 1);
                        model.setName(edtModelName.getText().toString().trim());
                        model.setDescription(edtDescription.getText().toString().trim());
                        model.setSex(spinnerSex.getSelectedItem().toString());
                        model.setCategoryID(selectedCategory.getID());
                        model.setPrice(Double.parseDouble(edtModelPrice.getText().toString().trim()));
                        ref.child(String.valueOf(model.getID())).setValue(model,
                                new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                        pushVariant(model);

                                        pushModelImage(model);

                                        edtModelName.setText("");
                                        edtDescription.setText("");
                                        edtModelPrice.setText("");
                                        edtColor.setText("");
                                        edtSize.setText("");
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AddModelActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                ProductModel model = new ProductModel();
                model.setID(editModel.getID());
                model.setName(edtModelName.getText().toString().trim());
                model.setDescription(edtDescription.getText().toString().trim());
                model.setSex(spinnerSex.getSelectedItem().toString());
                model.setCategoryID(selectedCategory.getID());
                model.setPrice(Double.parseDouble(edtModelPrice.getText().toString().trim()));
                ref.child(String.valueOf(model.getID())).setValue(model,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                pushVariant(model);

                                pushModelImage(model);

                                edtModelName.setText("");
                                edtDescription.setText("");
                                edtModelPrice.setText("");
                                edtColor.setText("");
                                edtSize.setText("");
                            }
                        });
            }

        });
    }

    private void deleteModel() {
        // Create a dialog builder.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title of the dialog.
        builder.setTitle("Delete Model");

        // Set the message of the dialog.
        builder.setMessage("Are you sure you want to delete this?");

        // Add a positive button to the dialog.
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the item.
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                //Delete all variants with modelID == myId
                Query variantQuery = rootRef.child("Variant").orderByChild("modelID").equalTo(editModel.getID());
                variantQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot variantSnapshot : snapshot.getChildren()) {
                            variantSnapshot.getRef().removeValue();
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle any errors here
                    }
                });

                //Delete all modelImages with modelID == myId
                Query imageQuery = rootRef.child("ModelImage").orderByChild("modelID").equalTo(editModel.getID());
                imageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot imageSnapshot : snapshot.getChildren()) {
                            imageSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle any errors here
                    }
                });

                //Delete the model with id == myId
                rootRef.child("Model").child(String.valueOf(editModel.getID())).removeValue();

            }
        });

        // Add a negative button to the dialog.
        builder.setNegativeButton("Cancel", null);

        // Create the dialog and show it.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pushModelImage(ProductModel model) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ModelImage");
        // Retrieve current data from the database
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (editModel != null) {
                    Query query = ref.orderByChild("modelID").equalTo(model.getID());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                data.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    ModelImage modelImage = categorySnapshot.getValue(ModelImage.class);
                    if (modelImage != null && modelImage.getID() > maxModelImageId) {
                        maxModelImageId = modelImage.getID();
                    }
                }

                for (String url : urlList){
                    //name and path of image
                    String filePathAndName = "category_images/" + url.substring( url.lastIndexOf('/')+1);
                    //upload image
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                    storageReference.putFile(Uri.parse(url))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while(!uriTask.isSuccessful());
                                    Uri downloadImageUri = uriTask.getResult();
                                    // Add the new model image with the incremented ID
                                    ModelImage modelImage = new ModelImage();
                                    modelImage.setID(++maxModelImageId);
                                    modelImage.setModelID(model.getID());
                                    modelImage.setUrl(downloadImageUri.toString());
                                    ref.child(String.valueOf(modelImage.getID())).setValue(modelImage,
                                            new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(AddModelActivity.this, "Update Model Successfully!", Toast.LENGTH_SHORT).show();

                                                    sizeList.clear();
                                                    sizeAdapter.notifyDataSetChanged();
                                                    colorList.clear();
                                                    colorAdapter.notifyDataSetChanged();
                                                    urlList.clear();
                                                    imageAdapter.notifyDataSetChanged();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddModelActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void pushVariant(ProductModel model) {
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Variant");
            // Retrieve current data from the database
            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (editModel != null) {
                        Query query = ref2.orderByChild("modelID").equalTo(model.getID());
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
                        });
                    }

                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        ProductVariant variant = categorySnapshot.getValue(ProductVariant.class);
                        if (variant != null && variant.getID() > maxVariantId) {
                            maxVariantId = variant.getID();
                        }
                    }

                    int colorCount = colorList.size();
                    for (int i = 0; i < colorCount; ++i) {
                        int sizeCount = sizeList.size();
                        for (int j = 0; j < sizeCount; ++j) {
                            ProductVariant variant = new ProductVariant();
                            variant.setID(++maxVariantId);
                            variant.setModelID(model.getID());
                            variant.setColor(colorList.get(i));
                            variant.setSize(sizeList.get(j));

                            ref2.child(String.valueOf(variant.getID())).setValue(variant,
                                    new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
    }

    private void pickFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    urlList.add(imageurl.toString());
                }

            } else {
                Uri imageurl = data.getData();
                urlList.add(imageurl.toString());
            }
            imageAdapter.notifyDataSetChanged();
        } else {
            // show this if no image is selected
            Toast.makeText(AddModelActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getBundle(){
        editModel = (ProductModel)  getIntent().getSerializableExtra("model");
        if (editModel == null)
            return;

        btnDeleteModel.setVisibility(View.VISIBLE);

        selectedCategory = categoryList.stream()
                .filter(c -> c.getID() == editModel.getCategoryID())
                .findFirst().get();
        spinnerCategory.setSelection(categoryList.indexOf(selectedCategory));

        spinnerSex.setSelection(sexList.indexOf(editModel.getSex()));

        edtModelName.setText(editModel.getName());
        edtModelPrice.setText(String.valueOf(editModel.getPrice()));
        edtDescription.setText(editModel.getDescription());
//        colorList = editModel.getColorList();
        colorList.clear();
        colorList.addAll(editModel.getColorList());
        colorAdapter.notifyDataSetChanged();
//        sizeList = editModel.getSizeList();
        sizeList.clear();
        sizeList.addAll(editModel.getSizeList());
        sizeAdapter.notifyDataSetChanged();
//        urlList = editModel.getImages();
        urlList.clear();
        urlList.addAll(editModel.getImages());
        imageAdapter.notifyDataSetChanged();
        btnAddModel.setText("Update Model");
    }
}