package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Model.Order;

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

        holder.orderNumberTextView.setText("Order #" + order.getID());
        holder.orderDateTextView.setText(order.getDate());

        if (order.getStatus().equals("PENDING")) {
            holder.orderStatusTextView.setText(order.getStatus());
            holder.orderStatusTextView.setTextColor(Color.GREEN);
        } else {
            holder.orderStatusTextView.setText(order.getStatus());
            holder.orderStatusTextView.setTextColor(Color.RED);
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

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderNumberTextView = itemView.findViewById(R.id.order_number_text_view);
            orderDateTextView = itemView.findViewById(R.id.order_date_text_view);
            orderStatusTextView = itemView.findViewById(R.id.order_status_text_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    // Check if the position is valid (i.e. not -1)
                    if (position != RecyclerView.NO_POSITION) {
                        // Perform any action on item click here
                        Order clickedItem = orders.get(position);
                    }
                }
            });
        }
    }
}