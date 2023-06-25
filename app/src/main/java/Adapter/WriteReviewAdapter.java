package Adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Interface.OnReviewListener;
import Model.ModelImage;
import Model.OrderItem;
import Model.ProductModel;

public class WriteReviewAdapter extends RecyclerView.Adapter<WriteReviewAdapter.ViewHolder>{
    public void setOnReviewListener(OnReviewListener onReviewListener) {
        this.onReviewListener = onReviewListener;
    }

    private OnReviewListener onReviewListener;

    public WriteReviewAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    private List<OrderItem> orderItems;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_writereview, parent, false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                String ratingText;
                int textColor;

                switch ((int) rating) {
                    case 1:
                        ratingText = "Bad";
                        textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.black); // Màu sắc tương ứng với Bad
                        break;
                    case 2:
                        ratingText = "Poor";
                        textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.black); // Màu sắc tương ứng với Bad
                        break;
                    case 3:
                        ratingText = "Average";
                        textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow);
                        break;
                    case 4:
                        ratingText = "Good";
                        textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow);
                        break;
                    case 5:
                        ratingText = "Amazing";
                        textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow);
                        break;
                    default:
                    {
                        ratingText = "Rate this product * ";
                        textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.redpink);
                        break;
                    }



                }

                holder.ratingTv.setText(ratingText);
                holder.ratingTv.setTextColor(textColor);

                if (onReviewListener != null) {
                    onReviewListener.onRatingChanged(position, rating);
                }
            }
        });

        holder.commentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (onReviewListener != null) {
                    onReviewListener.onCommentChanged(position, charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");

        ref.orderByChild("id").equalTo(orderItem.getVariantID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot hm: snapshot.getChildren())
                {
                    orderItem.setColor(hm.child("color").getValue().toString());
                    orderItem.setSize(hm.child("size").getValue().toString());
                    String sizeColor = orderItem.getSize() + ", " + orderItem.getColor();
                    holder.sizeColorTxt.setText(sizeColor);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ModelImage");


                    Query query2 = reference.orderByChild("modelID").equalTo(Integer.parseInt(hm.child("modelID").getValue().toString()));
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                ModelImage image = modelSnapshot.getValue(ModelImage.class);
                                orderItem.setImage(image.getUrl());
                                try {
                                    Picasso.get().load(orderItem.getImage()).placeholder(R.drawable.error).into(holder.imgProduct);

                                }
                                catch (Exception e){
                                    holder.imgProduct.setImageResource(R.drawable.error);
                                }
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors here
                        }
                    });

                    DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference("Model");

                    Query query3 = ref4.orderByChild("id").equalTo(Integer.parseInt(hm.child("modelID").getValue().toString()));
                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                ProductModel m = modelSnapshot.getValue(ProductModel.class);
                                orderItem.setProductName(m.getName());
                                holder.nameProduct.setText(orderItem.getProductName());
                                break;
                            }
                        }

                        @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle errors here
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
        return orderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameProduct, sizeColorTxt, ratingTv;
        ImageView imgProduct;

        EditText commentEdt;

        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameProduct = itemView.findViewById(R.id.nameProduct);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            sizeColorTxt = itemView.findViewById(R.id.sizeColorTxt);
            ratingTv = itemView.findViewById(R.id.ratingTv);
            commentEdt = itemView.findViewById(R.id.commentEdt);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
