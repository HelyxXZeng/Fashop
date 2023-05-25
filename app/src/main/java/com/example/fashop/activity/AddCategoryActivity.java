package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Model.ProductCategory;
import Model.ProductModel;

public class AddCategoryActivity extends AppCompatActivity {

    private Uri image_uri;
    private ImageView imgCategory;
    private EditText edtName;
    Button btnUpload, btnAddCategory;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        initUI();
    }

    private void initUI() {
        edtName = findViewById(R.id.edtCategoryName);
        imgCategory = findViewById(R.id.imgCategory);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnUpload = findViewById(R.id.btnUploadImage);

        btnUpload.setOnClickListener(view -> pickFromGallery());

        btnAddCategory.setOnClickListener(view -> {
            addCategory();
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (resultCode == RESULT_OK){
                //get picked image
                image_uri = data.getData();
                //set to imageview
                imgCategory.setImageURI(image_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addCategory(){
        progressDialog.show();
        String uri = image_uri.toString();
        //name and path of image
        String filePathAndName = "category_images/" + uri.substring( uri.lastIndexOf('/')+1);
        //upload image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");
                        // Retrieve current data from the database
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                int maxId = 0;
                                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                                    ProductCategory category = categorySnapshot.getValue(ProductCategory.class);
                                    if (category != null && category.getID() > maxId) {
                                        maxId = category.getID();
                                    }
                                }

                                while(!uriTask.isSuccessful());
                                Uri downloadImageUri = uriTask.getResult();

                                // Add the new category with the incremented ID
                                ProductCategory category = new ProductCategory();
                                category.setID(maxId + 1);
                                category.setName(edtName.getText().toString().trim());
                                category.setImg(downloadImageUri.toString());
                                ref.child(String.valueOf(category.getID())).setValue(category,
                                        new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddCategoryActivity.this, "Add category successfully!", Toast.LENGTH_SHORT).show();
                                                edtName.setText("");
                                                Picasso.get().load(R.drawable.error).placeholder(R.drawable.error).into(imgCategory);
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(AddCategoryActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddCategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}