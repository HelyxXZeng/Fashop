package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashop.R;

import com.squareup.picasso.Picasso;

import Interface.ChangNumberItemsListener;
import Model.ProductModel;

import MyClass.ClothingDomain;
import MyClass.ManagementCart;

import java.util.ArrayList;
import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {
    private List<ProductModel> clothingDomains;
    private ManagementCart managementCart;
    private ChangNumberItemsListener changNumberItemsListener;

    public CartListAdapter(List<ProductModel> clothingDomains, Context context, ChangNumberItemsListener changNumberItemsListener) {
        this.clothingDomains = clothingDomains;
        this.managementCart = new ManagementCart(context);
        this.changNumberItemsListener = changNumberItemsListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        ProductModel model = clothingDomains.get(position);
        if (model == null)
            return;

        try {
            Picasso.get().load(model.getImages().get(0)).placeholder(R.drawable.error).into(holder.pic);
        }
        catch (Exception e){
            holder.pic.setImageResource(R.drawable.error);
        }

        holder.title.setText(model.getName());
        holder.feeEachItem.setText(String.valueOf(model.getPrice()));
        holder.totalEachItem.setText(String.valueOf(Math.round(model.getNumberInCart() * model.getPrice() * 100) / 100));
        holder.num.setText(String.valueOf(model.getNumberInCart()));

        holder.plusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managementCart.plusNumberFood(clothingDomains, position, new ChangNumberItemsListener() {
                    @Override
                    public void changed() {
                        notifyDataSetChanged();
                        changNumberItemsListener.changed();
                    }
                });
            }
        });

        holder.minusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managementCart.minusNumberFood(clothingDomains, position, new ChangNumberItemsListener() {
                    @Override
                    public void changed() {
                        notifyDataSetChanged();
                        changNumberItemsListener.changed();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return clothingDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, feeEachItem;
        ImageView pic, plusItem, minusItem;
        TextView totalEachItem, num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            pic = itemView.findViewById(R.id.picCart);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            num = itemView.findViewById(R.id.numberItemTxt);
            plusItem = itemView.findViewById(R.id.plusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);

        }
    }

}
