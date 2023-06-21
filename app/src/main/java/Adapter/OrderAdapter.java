package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.example.fashop.activity.OrderDetailActivity;
import com.example.fashop.activity.OrderDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.ModelImage;
import Model.Order;
import Model.OrderItem;
import Model.ProductModel;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        OrderItem item;

        holder.orderNumberTextView.setText("Order #" + order.getID());
        holder.orderDateTextView.setText(order.getDate());
        holder.totalTxt.setText(Double.toString(order.getTotal()));

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("OrderItem");
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Variant");
        ref2.orderByChild("orderID").equalTo(order.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (holder.orderItems != null){
                    holder.orderItems.clear();
                }
                for(DataSnapshot ds: snapshot.getChildren()){
                    OrderItem orderItem =  ds.getValue(OrderItem.class);

                    ref3.orderByChild("id").equalTo(orderItem.getVariantID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot hm: snapshot.getChildren())
                            {
                                orderItem.setColor(hm.child("color").getValue().toString());
                                orderItem.setSize(hm.child("size").getValue().toString());
                                holder.adapter.notifyDataSetChanged();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ModelImage");


                                Query query2 = reference.orderByChild("modelID").equalTo(Integer.parseInt(hm.child("modelID").getValue().toString()));
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot modelSnapshot : dataSnapshot.getChildren()) {
                                            ModelImage image = modelSnapshot.getValue(ModelImage.class);
                                            orderItem.setImage(image.getUrl());
                                            holder.adapter.notifyDataSetChanged();
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
                                            orderItem.setPrice(m.getPrice());
                                            holder.adapter.notifyDataSetChanged();
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
                    holder.orderItems.add(orderItem);
                    holder.adapter.notifyDataSetChanged();
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.orderStatusTextView.setText(order.getStatus());
        if (order.getStatus().equals("PENDING")) {

        }
        else if (order.getStatus().equals("CONFIRMED")) {

        }
        else if (order.getStatus().equals("SHIPPING")) {

        }
        else if (order.getStatus().equals("COMPLETED")) {
            holder.button_layout.setVisibility(View.VISIBLE);
        }
        else {

        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumberTextView;
        TextView orderDateTextView;
        TextView orderStatusTextView;
        TextView totalTxt;
        RecyclerView OrderItemList;
        OrderItemAdapter adapter;
        List<OrderItem> orderItems = new ArrayList<>();
        CardView card;
        LinearLayout button_layout;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            OrderItemList = itemView.findViewById(R.id.cart_item_rcv);
            orderNumberTextView = itemView.findViewById(R.id.order_number_text_view);
            orderDateTextView = itemView.findViewById(R.id.order_date_text_view);
            orderStatusTextView = itemView.findViewById(R.id.order_status_text_view);
            totalTxt = itemView.findViewById(R.id.totalTxt);
            button_layout = itemView.findViewById(R.id.button_layout);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            OrderItemList.setLayoutManager(linearLayoutManager);
            adapter = new OrderItemAdapter(orderItems);
            OrderItemList.setAdapter(adapter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // Check if the position is valid (i.e. not -1)
                    if (position != RecyclerView.NO_POSITION) {
                        // Perform any action on item click here
                        Order clickedItem = orders.get(position);

                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        intent.putExtra("order", clickedItem);
                        context.startActivity(intent);
                    }
                }
            });
            card = itemView.findViewById(R.id.card);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    // Check if the position is valid (i.e. not -1)
                    if (position != RecyclerView.NO_POSITION) {
                        // Perform any action on item click here
                        Order clickedItem = orders.get(position);

                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        intent.putExtra("order", clickedItem);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}