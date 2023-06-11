//package Adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.fashop.R;
//
//import com.squareup.picasso.Picasso;
//
//import Interface.ChangNumberItemsListener;
//import Model.ProductModel;
//
//import MyClass.ClothingDomain;
////import MyClass.ManagementCart;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {
//    private List<ProductModel> productVariantList;
//    private ManagementCart managementCart;
//    private ChangNumberItemsListener changNumberItemsListener;
//
//    public CartListAdapter(List<ProductModel> productVariantList, Context context, ChangNumberItemsListener changNumberItemsListener) {
//        this.productVariantList = productVariantList;
//        this.managementCart = new ManagementCart(context);
//        this.changNumberItemsListener = changNumberItemsListener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
//
//        return new ViewHolder(inflate);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//
//        ProductModel model = productVariantList.get(position);
//        if (model == null)
//            return;
//
//        try {
//            Picasso.get().load(model.getImages().get(0)).placeholder(R.drawable.error).into(holder.productImg);
//        }
//        catch (Exception e){
//            holder.productImg.setImageResource(R.drawable.error);
//        }
//
//        holder.titleTxt.setText(model.getName());
////        holder.feeEachItem.setText(String.valueOf(model.getPrice()));
////        holder.priceTxt.setText(String.valueOf(Math.round(model.getNumberInCart() * model.getPrice() * 100) / 100));
//        holder.priceTxt.setText(String.valueOf(model.getPrice()));
//        holder.numberOrderTxt.setText(String.valueOf(model.getNumberInCart()));
//
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
//
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
//    }
//
//    @Override
//    public int getItemCount() {
//        return productVariantList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//        TextView titleTxt, priceTxt;
//        ImageView productImg;
//        ImageButton plusBtn, minusBtn;
//        TextView numberOrderTxt;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            titleTxt = itemView.findViewById(R.id.titleTxt);
//            priceTxt = itemView.findViewById(R.id.priceTxt);
//            productImg = itemView.findViewById(R.id.productImg);
////            totalEachItem = itemView.findViewById(R.id.totalEachItem);
//            numberOrderTxt = itemView.findViewById(R.id.numberOrderTxt);
//            plusBtn = itemView.findViewById(R.id.plusBtn);
//            minusBtn = itemView.findViewById(R.id.minusBtn);
//
//        }
//    }
//
//}
