package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import MyClass.Photo;

public class viewPage2Adapter extends RecyclerView.Adapter<viewPage2Adapter.PhotoViewHolder> {

    private List<String> mListPhoto;

    public viewPage2Adapter(List<String> mListPhoto) {
        this.mListPhoto = mListPhoto;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String photo = mListPhoto.get(position);
        if (photo == null) {
            return;
        }
//        holder.imgPhoto.setImageResource(photo.getResourceId());

        try {
            Picasso.get().load(photo).placeholder(R.drawable.error).into(holder.imgPhoto);
        }
        catch (Exception e){
            holder.imgPhoto.setImageResource(R.drawable.error);
        }
    }

    @Override
    public int getItemCount() {
        if (mListPhoto != null)
        {
            return mListPhoto.size();
        }
        return 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgPhoto;



        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPhoto = itemView.findViewById(R.id.img_photo);
        }
    }
}
