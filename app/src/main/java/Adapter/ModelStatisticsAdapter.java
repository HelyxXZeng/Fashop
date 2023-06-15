package Adapter;

import android.content.Context;
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

public class ModelStatisticsAdapter extends RecyclerView.Adapter<ModelStatisticsAdapter.ViewHolder>{
    private Context context;
    private List<ProductModel> modelList;

    public ModelStatisticsAdapter(Context context, List<ProductModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ModelStatisticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_model_statistics, parent, false);
        return new ModelStatisticsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelStatisticsAdapter.ViewHolder holder, int position) {
        ProductModel model = modelList.get(position);
        try {
            Picasso.get().load(model.getImages().get(0)).placeholder(R.drawable.error).into(holder.imgModel);
        }
        catch (Exception e){
            holder.imgModel.setImageResource(R.drawable.error);
        }

        holder.tvModelName.setText(model.getName());
        holder.tvCategoryName.setText(model.getCategory());
        holder.tvPrice.setText(String.valueOf(model.getPrice()));
        if (model.getQuantity() == 0){
            holder.tvQuantity.setText("NaN");
        } else {
            holder.tvQuantity.setText(String.valueOf(model.getQuantity()));
        }

        holder.tvSex.setText(model.getSex());
        if (model.getRate() == 0){
            holder.tvRate.setText("NaN");
        } else {
            String formattedNumber = String.format("%.1f", model.getRate());
            holder.tvRate.setText(String.valueOf(formattedNumber));
        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvModelName;
        private TextView tvPrice;
        private TextView tvCategoryName;
        private TextView tvSex;
        private TextView tvQuantity;
        private TextView tvRate;
        private ImageView imgModel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvModelName = itemView.findViewById(R.id.tvModelName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvSex = itemView.findViewById(R.id.tvSex);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);

            tvRate = itemView.findViewById(R.id.tvRate);
            imgModel = itemView.findViewById(R.id.imgModel);
        }
    }
}
