package Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.ProductCategory;

public class EditCategoryAdapter extends RecyclerView.Adapter<EditCategoryAdapter.ViewHolder>{
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private Context context;
    private List<ProductCategory> categoryList;
    public static int currentPosition = -1;

    public EditCategoryAdapter(Context context, List<ProductCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public EditCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_edit_category, parent, false);
        return new EditCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductCategory category = categoryList.get(position);
        holder.edtCategoryName.setText(category.getName());
        Picasso.get().load(category.getImg()).placeholder(R.drawable.error).into(holder.imgCategory);

        holder.btnAddImage.setOnClickListener(v->{
            currentPosition = position;
            pickFromGallery();
        });

        holder.btnDelete.setOnClickListener(v->{
            currentPosition = position;
            // Create a dialog builder.
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Set the title of the dialog.
            builder.setTitle("Delete Category");

            // Set the message of the dialog.
            builder.setMessage("Are you sure you want to delete this?");

            // Add a positive button to the dialog.
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Delete the item.
                    removeCategory();

                }
            });

            // Add a negative button to the dialog.
            builder.setNegativeButton("Cancel", null);

            // Create the dialog and show it.
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        holder.btnUpdateCategory.setOnClickListener(v->{
            currentPosition = position;
            updateCategory(holder);
        });
    }

    private void removeCategory() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");
        ref.child(String.valueOf(categoryList.get(currentPosition).getID())).removeValue();
//        categoryList.remove(currentPosition);
//        notifyDataSetChanged();
    }

    private void updateCategory(ViewHolder holder){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String uri = categoryList.get(currentPosition).getImg();
        //name and path of image
        String filePathAndName = "category_images/" + uri.substring( uri.lastIndexOf('/')+1);
        //upload image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(Uri.parse(uri))
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");

                    while(!uriTask.isSuccessful());
                    Uri downloadImageUri = uriTask.getResult();

                    // Update the category
                    ProductCategory category = new ProductCategory();
                    category.setID(categoryList.get(currentPosition).getID());
                    category.setName(holder.edtCategoryName.getText().toString().trim());
                    category.setImg(downloadImageUri.toString());
                    ref.child(String.valueOf(category.getID())).setValue(category,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Update Category Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });
                }

            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ((Activity) context).startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button btnAddImage, btnUpdateCategory, btnDelete;
        private EditText edtCategoryName;
        private ImageView imgCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            btnAddImage = itemView.findViewById(R.id.btnAddImage);
            btnUpdateCategory = itemView.findViewById(R.id.btnUpdateCategory);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            edtCategoryName = itemView.findViewById(R.id.edtCategoryName);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }
}
