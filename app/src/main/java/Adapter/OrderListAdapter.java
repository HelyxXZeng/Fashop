package Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fashop.R;
import com.example.fashop.activity.OrderDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Order;
import Model.OrderItem;

public class OrderListAdapter  extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    public List<Order> orderList;
    Context context;
    public OrderListAdapter(Context con, List<Order> orders) { this.orderList=orders; this.context=con;}

    @NonNull
    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_admin_order_list, parent, false);

        return new OrderListAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, int position) {
        Order oder = orderList.get(position);

        if (oder == null)
            return;
        //get quantity
        holder.id.setText(String.valueOf(oder.getID()));
        holder.datecreated.setText(oder.getDate());
        holder.priceTxt.setText(String.valueOf(oder.getTotal()));
        holder.status.setText(oder.getStatus());

        switch (holder.status.getText().toString())
        {
            case "PENDING":
                holder.status.setTextColor(Color.BLACK);
                holder.status.setBackgroundColor(Color.YELLOW);
                break;
            case "SHIPPING":
                holder.status.setTextColor(Color.WHITE);
                holder.status.setBackgroundColor(Color.BLUE);
                break;
            case "COMPLETED":
                holder.status.setTextColor(Color.WHITE);
                holder.status.setBackgroundColor(Color.GREEN);
                break;
            case "DECLINED":
                holder.status.setTextColor(Color.RED);
                holder.status.setBackgroundColor(Color.WHITE);
                break;
            case "CANCELLED":
                holder.status.setTextColor(Color.WHITE);
                holder.status.setBackgroundColor(Color.RED);
                break;
        }

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("order",orderList.get(position));
            context.startActivity(intent);
        });

    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id, datecreated, priceTxt, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.idText);
            datecreated = itemView.findViewById(R.id.tvcreated);
            priceTxt = itemView.findViewById(R.id.tvOrderPrice);
            status = itemView.findViewById(R.id.statusText);
        }
    }
}
