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

import Model.ProductCategory;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    private List<ProductCategory> categories;

    public CategoryAdapter(List<ProductCategory> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {

        ProductCategory category = categories.get(position);
        if (category == null)
            return;

        Picasso.get().load(category.getImg()).placeholder(R.drawable.error).into(holder.imgCategory);

        holder.tvCategory.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        if (categories == null)
            return 0;
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCategory;
        private ImageView imgCategory;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tvCategory);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }
}
