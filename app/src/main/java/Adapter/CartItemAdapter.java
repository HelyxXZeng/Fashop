package Adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.example.fashop.activity.CartListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Fragment.EditProductVariantFragment;
import Fragment.ProductVariantFragment;
import Interface.ChangNumberItemsListener;
import Model.CartItem;
import Model.ModelImage;
import Model.ProductModel;
import Model.ProductVariant;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private List<CartItem> cartItemList;

    private AppCompatActivity activity;


    public CartItemAdapter(List<CartItem> cartItemList, AppCompatActivity activity) {
        this.cartItemList = cartItemList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);

        return new CartItemAdapter.ViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        if (cartItem == null)
            return;
        //get quantity
        holder.numberOrderTxt.setText(String.valueOf(cartItem.getQuantity()));

        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = cartItem.getQuantity() + 1;
                holder.numberOrderTxt.setText(String.valueOf(quantity));
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CartItem");
                ref.child(String.valueOf(cartItem.getID())).child("quantity").setValue(quantity);
            }
        });

        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = cartItem.getQuantity();
                if (quantity > 1){
                    quantity = quantity - 1;
                }
                holder.numberOrderTxt.setText(String.valueOf(quantity));
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CartItem");
                ref.child(String.valueOf(cartItem.getID())).child("quantity").setValue(quantity);
            }
        });

        holder.sizeColorTxt.setText(cartItem.getSize() + ", " + cartItem.getColor());

        try {
            Picasso.get().load(cartItem.getImage()).placeholder(R.drawable.error).into(holder.productImg);

        }
        catch (Exception e){
            holder.productImg.setImageResource(R.drawable.error);
        }

        holder.titleTxt.setText(cartItem.getProductName());
        holder.priceTxt.setText(String.valueOf(cartItem.getPrice()));

        holder.editSizeColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("cartItem", cartItem);
                EditProductVariantFragment bottomSheetFragment = new EditProductVariantFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(activity.getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });


//        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Variant" + "/" + cartItem.getVariantID());
//        ref1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ProductVariant productVariant = snapshot.getValue(ProductVariant.class);
//
//                if (productVariant != null){
//                    //get color size
//                    String sizeColor = productVariant.getSize() + ", " + productVariant.getColor();
//                    holder.sizeColorTxt.setText(sizeColor);
//
//                    //get model infor
//                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Model" + "/" + String.valueOf(productVariant.getModelID()));
//                    ref2.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            ProductModel productModel = snapshot.getValue(ProductModel.class);
//                            if (productModel != null){
//                                try {
//                                    Picasso.get().load(productModel.getImages().get(0)).placeholder(R.drawable.error).into(holder.productImg);
//
//                                }
//                                catch (Exception e){
//                                    holder.productImg.setImageResource(R.drawable.error);
//                                }
//
//                                holder.titleTxt.setText(productModel.getName());
//                                holder.priceTxt.setText(String.valueOf(productModel.getPrice()));
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });




//        holder.feeEachItem.setText(String.valueOf(model.getPrice()));
//        holder.priceTxt.setText(String.valueOf(Math.round(model.getNumberInCart() * model.getPrice() * 100) / 100));

//        holder.numberOrderTxt.setText(String.valueOf(model.getNumberInCart()));

//        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                managementCart.plusNumberVariantProduct(productVariantList, position, new ChangNumberItemsListener() {
//                    @Override
//                    public void changed() {
//                        notifyDataSetChanged();
//                        changNumberItemsListener.changed();
//                    }
//                });
//            }
//        });

//        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                managementCart.minusNumberVariantProduct(productVariantList, position, new ChangNumberItemsListener() {
//                    @Override
//                    public void changed() {
//                        notifyDataSetChanged();
//                        changNumberItemsListener.changed();
//                    }
//                });
//            }
//        });


    }

    private void getOtherData(CartItem cartItem){
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Variant" + "/" + cartItem.getVariantID());
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductVariant productVariant = snapshot.getValue(ProductVariant.class);

                if (productVariant != null){
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Model" + "/" + productVariant.getModelID());
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("object", productModel);
                            EditProductVariantFragment bottomSheetFragment = new EditProductVariantFragment();
                            bottomSheetFragment.setArguments(bundle);
                            bottomSheetFragment.show(activity.getSupportFragmentManager(), bottomSheetFragment.getTag());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, priceTxt, sizeColorTxt;
        ImageView productImg;
        ImageButton plusBtn, minusBtn;
        TextView numberOrderTxt;
        LinearLayout editSizeColorBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            productImg = itemView.findViewById(R.id.productImg);
            numberOrderTxt = itemView.findViewById(R.id.numberOrderTxt);
            plusBtn = itemView.findViewById(R.id.plusBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            sizeColorTxt = itemView.findViewById(R.id.sizeColorTxt);
            editSizeColorBtn = itemView.findViewById(R.id.editSizeColorBtn);
        }
    }
}
