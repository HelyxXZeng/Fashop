package Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.OrderAdapter;
import Model.Order;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfirmedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Order> orders = new ArrayList<>();
    private List<Order> loading = new ArrayList<>();
    private RecyclerView OrderView;
    private OrderAdapter adapter;

    public ConfirmedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfirmedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmedFragment newInstance(String param1, String param2) {
        ConfirmedFragment fragment = new ConfirmedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmed, container, false);
        initUI(view);
        return view;
    }
    public void initUI(View view){
        OrderView = view.findViewById(R.id.recycler_view_order_history);
        adapter = new OrderAdapter(getContext(), loading);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        OrderView.setLayoutManager(manager);
        OrderView.setAdapter(adapter);
        getOrderData();
        loadOrder();
    }
    private void getOrderData(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Order");

        Query query = ordersRef.orderByChild("customerID").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the Order object from Firebase
                    Order order = snapshot.getValue(Order.class);

                    // Add the Order object to the list
                    orders.add(order);
                }
                loadOrder();
                // Do something with the list of Order objects here
                // For example, update your RecyclerView adapter with the new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur while retrieving data from Firebase
            }
        });
    }
    private void loadOrder(){
        loading.clear();
        for (Order order : orders){
            if(order.getStatus().equals("CONFIRMED")) loading.add(order);
        }
        adapter.notifyDataSetChanged();

    }
}