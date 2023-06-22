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

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
    List<UserModel> userList;

    public StaffAdapter(List<UserModel> userList) {
        this.userList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_staff,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvPhone, tvAddress, tvEmail, tvUid;
        ImageView imgAvt;

//        LinearLayout staff_viewholder; implements View.OnCreateContextMenuListener

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            imgAvt = itemView.findViewById(R.id.imgAvt);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvUid = itemView.findViewById(R.id.tvUid);
//            staff_viewholder = itemView.findViewById(R.id.staff_viewholder);
//            staff_viewholder.setOnCreateContextMenuListener(this);
        }

//        @Override
//        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
////            MenuItem deleteMenuItem = contextMenu.add(this.getAdapterPosition(), 121, 0, "Delete");
////            deleteMenuItem.setIcon(R.drawable.delete_user);
//            MenuInflater inflater = new MenuInflater(view.getContext());
//            inflater.inflate(R.menu.user_menu, contextMenu);
//        }
    }
}
