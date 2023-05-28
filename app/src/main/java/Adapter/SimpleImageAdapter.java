package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SimpleImageAdapter extends RecyclerView.Adapter<SimpleImageAdapter.ViewHolder> {
    private Context context;
    private List<String> urlList;

    public SimpleImageAdapter(Context context, List<String> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    @NonNull
    @Override
    public SimpleImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_simple_image, parent, false);
        return new SimpleImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleImageAdapter.ViewHolder holder, int position) {
        String url = urlList.get(position);
        Picasso.get().load(url).placeholder(R.drawable.error).into(holder.imgModel);
        holder.imgModel.setOnClickListener(v->{
            if (urlList.size() == 1){
                Toast.makeText(context, "Can't remove the last image!", Toast.LENGTH_SHORT).show();
            }
            else {
                urlList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgModel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgModel = itemView.findViewById(R.id.imgModel);
        }
    }
}
