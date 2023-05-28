package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SimpleStringAdapter extends RecyclerView.Adapter<SimpleStringAdapter.ViewHolder> {
    private Context context;

    public SimpleStringAdapter(Context context, List<String> stringList) {
        this.context = context;
        this.stringList = stringList;
    }

    private List<String> stringList = new ArrayList<>();
    @NonNull
    @Override
    public SimpleStringAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_simple_string, parent, false);
        return new SimpleStringAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleStringAdapter.ViewHolder holder, int position) {
        String str = stringList.get(position);
        holder.tvSimpleString.setText(str);
        holder.tvSimpleString.setOnClickListener(v->{
            if (stringList.size() == 1){
                Toast.makeText(context, "Can't remove the last item!", Toast.LENGTH_SHORT).show();
            }
            else {
                stringList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSimpleString;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSimpleString = itemView.findViewById(R.id.tvSimpleString);
        }
    }
}
