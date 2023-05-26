package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.example.fashop.activity.AddModelActivity;
import com.example.fashop.activity.EditModelFormActivity;
import com.example.fashop.activity.ShowDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.ProductCategory;
import Model.ProductModel;

public class EditModelAdapter extends RecyclerView.Adapter<EditModelAdapter.ViewHolder>{
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private Context context;
    private List<ProductModel> modelList;
    public static int currentPosition = -1;

    public EditModelAdapter(Context context, List<ProductModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public EditModelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_model, parent, false);
        return new EditModelAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditModelAdapter.ViewHolder holder, int position) {
        ProductModel model = modelList.get(position);
        holder.tvModelName.setText(model.getName());
        holder.tvModelPrice.setText(String.valueOf(model.getPrice()));
        try {
            Picasso.get().load(model.getImages().get(0)).placeholder(R.drawable.error).into(holder.imgModel);
        }
        catch (Exception e){
            holder.imgModel.setImageResource(R.drawable.error);
        }
        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, AddModelActivity.class);
            intent.putExtra("model", modelList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvModelName, tvModelPrice;
        private ImageView imgModel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModelName = itemView.findViewById(R.id.tvModelName);
            tvModelPrice = itemView.findViewById(R.id.tvModelPrice);
            imgModel = itemView.findViewById(R.id.imgModel);
        }
    }
}
