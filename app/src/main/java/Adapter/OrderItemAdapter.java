package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.CartItem;
import Model.OrderItem;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private List<OrderItem> orderItemList;

    public OrderItemAdapter(List<OrderItem> orderItems) {
        this.orderItemList = orderItems;
    }

    @NonNull
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_item, parent, false);

        return new OrderItemAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.ViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);

        if (orderItem == null)
            return;
        //get quantity
        holder.numberOrderTxt.setText(String.valueOf(orderItem.getQuantity()));

        holder.sizeColorTxt.setText(orderItem.getSize() + ", " + orderItem.getColor());

        try {
            Picasso.get().load(orderItem.getImage()).placeholder(R.drawable.error).into(holder.productImg);

        }
        catch (Exception e){
            holder.productImg.setImageResource(R.drawable.error);
        }

        holder.titleTxt.setText(orderItem.getProductName());
        holder.priceTxt.setText(String.valueOf(orderItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, priceTxt, sizeColorTxt;
        ImageView productImg;
        TextView numberOrderTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            productImg = itemView.findViewById(R.id.productImg);
            numberOrderTxt = itemView.findViewById(R.id.numberOrderTxt);
            sizeColorTxt = itemView.findViewById(R.id.sizeColorTxt);
        }
    }
}
