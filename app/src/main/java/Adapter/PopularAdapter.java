package Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashop.R;

import Model.ProductModel;
import MyClass.ClothingDomain;
import com.example.fashop.activity.ShowDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
    List<ProductModel> popularModel;

    public PopularAdapter(List<ProductModel> popularModel) {
        this.popularModel = popularModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProductModel model = popularModel.get(position);
        if (model == null)
            return;

        try {
            Picasso.get().load(model.getImages().get(0)).placeholder(R.drawable.error).into(holder.pic);
        }
        catch (Exception e){
            holder.pic.setImageResource(R.drawable.error);
        }

        holder.title.setText(model.getName());
        holder.fee.setText(String.valueOf(model.getPrice()));

//        int drawableResourceId = holder.itemView.getContext().getResources().getIdentifier(popularModel.get(position).getImages().get(0), "drawable", holder.itemView.getContext().getPackageName());
//        Glide.with(holder.itemView.getContext())
//                .load(drawableResourceId)
//                .into(holder.pic);

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ShowDetailActivity.class);
                intent.putExtra("object", model);
                holder.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return popularModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, fee;
        ImageView pic;
        ConstraintLayout addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            fee = itemView.findViewById(R.id.fee);
            pic = itemView.findViewById(R.id.pic);
            addBtn = itemView.findViewById(R.id.addBtn);
        }
    }
}
