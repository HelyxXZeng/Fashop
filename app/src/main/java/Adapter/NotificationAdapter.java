package Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.example.fashop.activity.OrderDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Model.NotificationModel;
import Model.Order;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    public NotificationAdapter(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    private List<NotificationModel> notificationList;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_notification, parent, false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notif = notificationList.get(position);

        holder.notifContent.setText(notif.getContent());
        holder.notifTitle.setText(notif.getTitle());
        holder.createdDateTv.setText(notif.getDate());

        if (notif.getStatus().equals("Read")){
            holder.orderDetailBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }
        else {
            holder.orderDetailBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.notifOrange2));
        }

        holder.orderDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notif.getStatus().equals("Unread")) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notification");
                    ref.child(String.valueOf(notif.getID())).child("status").setValue("Read");
                    holder.orderDetailBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                }

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Order");
                Query variantQuery = ref1.orderByChild("id").equalTo(notif.getOrderID());
                variantQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Order orderItem = dataSnapshot.getValue(Order.class);
                            Intent intent = new Intent(holder.itemView.getContext(), OrderDetailActivity.class);
                            intent.putExtra("order", orderItem);
                            holder.itemView.getContext().startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView notifTitle, notifContent, createdDateTv;
        LinearLayout orderDetailBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notifTitle = itemView.findViewById(R.id.notifTitle);
            createdDateTv = itemView.findViewById(R.id.createdDateTv);
            notifContent = itemView.findViewById(R.id.notifContent);
            orderDetailBtn = itemView.findViewById(R.id.orderDetailBtn);
        }
    }
}
