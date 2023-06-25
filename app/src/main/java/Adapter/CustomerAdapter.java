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

import Model.UserModel;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder>{
    List<UserModel> userList;
    public CustomerAdapter(List<UserModel> userList) {
        this.userList = userList;
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_staff,parent,false);
        return new CustomerAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {

        UserModel user = userList.get(position);
        if (user == null)
            return;

        try {
            Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.error).into(holder.imgAvt);
        }
        catch (Exception e){
            holder.imgAvt.setImageResource(R.drawable.avt);
        }

        holder.tvName.setText(user.getName());
        holder.tvPhone.setText(user.getPhone());
        String address = user.getStreetAddress() + ", "
                + user.getWard() + ", "
                + user.getDistrict() + ", "
                + user.getCity();
        holder.tvAddress.setText(address);
        holder.tvEmail.setText(user.getEmail());
        holder.tvUid.setText(user.getUid());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAddress, tvEmail, tvUid;
        ImageView imgAvt;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            imgAvt = itemView.findViewById(R.id.imgAvt);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvUid = itemView.findViewById(R.id.tvUid);
        }
    }

}
