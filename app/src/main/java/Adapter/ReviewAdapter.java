package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import Model.Order;
import Model.OrderItem;
import Model.ProductVariant;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
//    public void setOnReviewListener(OnReviewListener onReviewListener) {
//        this.onReviewListener = onReviewListener;
//    }

//    private OnReviewListener onReviewListener;

    public ReviewAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    private List<OrderItem> orderItems;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_review, parent, false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        float rating = orderItem.getRate();
        String cmt = orderItem.getComment();
        if (cmt.isEmpty()){
            String defaultCmt = "The product gets a " + String.valueOf(rating) + "-star rating from the customer";
            holder.commentTv.setText(defaultCmt);
        }
        else {
            holder.commentTv.setText(cmt);
        }

        holder.ratingBar.setRating(rating);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");

        ref.orderByChild("id").equalTo(orderItem.getVariantID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    ProductVariant variant = dataSnapshot.getValue(ProductVariant.class);
                    String sizeColor = variant.getSize() + ", " + variant.getColor();
                    holder.sizeColorTxt.setText(sizeColor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Order");
        ref1.orderByChild("id").equalTo(orderItem.getOrderID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Order order = dataSnapshot.getValue(Order.class);
                    String buyerUid = order.getCustomerID();

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users");
                    Query customerQuery = ref2.orderByChild("uid").equalTo(buyerUid);
                    customerQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                String userName = dataSnapshot.child("name").getValue().toString();
                                String profileImage = ""+dataSnapshot.child("profileImage").getValue();

                                holder.userNameTv.setText(userName);

//                                Log.e("username", userName);
//                                Log.e("profileImg", profileImage);

                                try {
                                    Picasso.get().load(profileImage).placeholder(R.drawable.avt).into(holder.imgAvt);

                                }
                                catch (Exception e){
                                    holder.imgAvt.setImageResource(R.drawable.avt);
                                }
                            }
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
        return orderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userNameTv, sizeColorTxt, commentTv;
        ImageView imgAvt;

        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.tvUserName);
            commentTv = itemView.findViewById(R.id.commentTv);
            sizeColorTxt = itemView.findViewById(R.id.sizeColorTxt);
            imgAvt = itemView.findViewById(R.id.imgAvt);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
