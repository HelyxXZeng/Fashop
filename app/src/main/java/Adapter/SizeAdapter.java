package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.example.fashop.activity.OnSizeClickListener;
import com.example.fashop.activity.ShowDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.ProductModel;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {
    List<String> sizes;

    private ColorAdapter colorAdapter;
    int selectedSizePosition;

    private Context context;

    private OnSizeClickListener onSizeClickListener;

    public void setOnSizeClickListener(OnSizeClickListener listener) {
        this.onSizeClickListener = listener;
    }




    public SizeAdapter(Context context, List<String> sizes, int selectedPosition, ColorAdapter colorAdapter) {
        this.context = context;
        this.sizes = sizes;
        this.selectedSizePosition = selectedPosition;
        this.colorAdapter = colorAdapter;
    }

    @NonNull
    @Override
    public SizeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_size, parent, false);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_list_model, parent, false);

        SizeAdapter.ViewHolder viewHolder = new SizeAdapter.ViewHolder(view);

        return viewHolder;
        //return new SizeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeAdapter.ViewHolder holder, int position) {
        String size = sizes.get(position);
        if (size == null)
            return;

        holder.tvSize.setText(size);

        if (selectedSizePosition == position) {
            // Phần tử được chọn, thay đổi giao diện tại đây
            holder.itemView.setBackgroundResource(R.drawable.selected_size_bg);
        } else {
            // Phần tử không được chọn, sử dụng giao diện mặc định
            holder.itemView.setBackgroundResource(R.drawable.size_bg);
        }

        holder.sizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSizePosition == position) {
                    // Phần tử đã được chọn, hủy chọn nếu nhấp lại
                    selectedSizePosition = -1;
                } else {
                    // Chọn phần tử mới
                    //colorAdapter.setSelectedColorPosition(-1);
                    selectedSizePosition = position;
                }
                notifyDataSetChanged(); // Cập nhật lại giao diện

                // Check the selectedSizePosition and update the colorAdapter accordingly
                if (selectedSizePosition != -1) {
                    // Enable the color list
                    colorAdapter.setEnabled(true);
                } else {
                    // Disable the color list
                    colorAdapter.setEnabled(false);
                    colorAdapter.setSelectedColorPosition(-1);
                }
                colorAdapter.notifyDataSetChanged();

                if (onSizeClickListener != null) {
                    onSizeClickListener.onSizeClicked(size, position);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        if (sizes == null)
            return 0;
        return sizes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout sizeBtn;
        private TextView tvSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sizeBtn = itemView.findViewById(R.id.sizeBtn);
            tvSize = itemView.findViewById(R.id.tvSize);
        }
    }
}
