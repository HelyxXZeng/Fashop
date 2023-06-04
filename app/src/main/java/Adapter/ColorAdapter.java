package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;

import java.util.List;

import Interface.OnColorClickListener;


public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    private OnColorClickListener onColorClickListener;

    public void setOnColorClickListener(OnColorClickListener listener) {
        this.onColorClickListener = listener;
    }
    List<String> colors;

    public void setSelectedColorPosition(int selectedColorPosition) {
        this.selectedColorPosition = selectedColorPosition;
    }

    int selectedColorPosition;

    private Context context;

    private boolean enabled = false;


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public ColorAdapter(Context context, List<String> colors, int selectedPosition) {
        this.context = context;
        this.colors = colors;
        this.selectedColorPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_color, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String color = colors.get(position);
        if (color == null)
            return;

        holder.tvSize.setText(color);

        // Check and apply the enabled state
        if (enabled) {
            holder.itemView.setEnabled(true);
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.itemView.setEnabled(false);
            holder.itemView.setAlpha(0.3f);
        }

        if (selectedColorPosition == position) {
            // Phần tử được chọn, thay đổi giao diện tại đây
            holder.itemView.setBackgroundResource(R.drawable.selected_size_bg);
        } else {
            // Phần tử không được chọn, sử dụng giao diện mặc định
            holder.itemView.setBackgroundResource(R.drawable.size_bg);
        }



        holder.sizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedColorPosition == position) {
                    // Phần tử đã được chọn, hủy chọn nếu nhấp lại
                    selectedColorPosition = -1;
                } else {
                    // Chọn phần tử mới
                    selectedColorPosition = position;
                }
                notifyDataSetChanged(); // Cập nhật lại giao diện

                if (onColorClickListener != null) {
                    onColorClickListener.onColorClicked(color, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (colors == null)
            return 0;
        return colors.size();
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
