package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.ProductModel;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {
    List<ProductModel> modelList;

    public ModelAdapter(List<ProductModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ModelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_model, parent, false);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_model, parent, false);
        return new ModelAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelAdapter.ViewHolder holder, int position) {
        ProductModel model = modelList.get(position);
        if (model == null)
            return;

        try {
            Picasso.get().load(model.getImages().get(0)).placeholder(R.drawable.error).into(holder.imgModel);
        }
        catch (Exception e){
            holder.imgModel.setImageResource(R.drawable.error);
        }

        holder.tvModelName.setText(model.getName());
        holder.tvModelPrice.setText(String.valueOf(model.getPrice()));
    }

    @Override
    public int getItemCount() {
        if (modelList == null)
            return 0;
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgModel;
        private TextView tvModelName;
        private TextView tvModelPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgModel = itemView.findViewById(R.id.imgModel);
            tvModelName = itemView.findViewById(R.id.tvModelName);
            tvModelPrice = itemView.findViewById(R.id.tvModelPrice);
        }
    }
}
