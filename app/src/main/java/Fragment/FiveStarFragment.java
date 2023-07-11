package Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.ReviewAdapter;
import Model.OrderItem;
import Model.ProductVariant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FiveStarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiveStarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int modelID;

    private RecyclerView reviewRecycler;
    private ReviewAdapter reviewAdapter;

    private List<OrderItem> orderItemList = new ArrayList<>();

    private List<Integer> variantIDList = new ArrayList<>();

    public FiveStarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FiveStarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FiveStarFragment newInstance(int number) {
        FiveStarFragment fragment = new FiveStarFragment();
        Bundle args = new Bundle();
        args.putInt("number", number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modelID = getArguments().getInt("number");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_five_star, container, false);
        initUI(view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        reviewRecycler.setLayoutManager(linearLayoutManager);
        reviewAdapter = new ReviewAdapter(orderItemList);
        reviewRecycler.setAdapter(reviewAdapter);

        loadFeedback_Stats();
        return view;
    }

    private void initUI(View view) {
        reviewRecycler= view.findViewById(R.id.reviewRecycler);
    }

    private void loadFeedback_Stats(){
        //get orderItemList
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");
        Query variantQuery = ref.orderByChild("modelID").equalTo(modelID);
        variantQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (variantIDList != null){
                    variantIDList.clear();
                }
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ProductVariant variantItem = dataSnapshot.getValue(ProductVariant.class);
                    variantIDList.add(variantItem.getID());
                }

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("OrderItem");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (orderItemList != null)
                        {
                            orderItemList.clear();
                        }
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            OrderItem orderItem = dataSnapshot.getValue(OrderItem.class);
                            for (Integer variantId : variantIDList){
                                if (orderItem.getVariantID() == variantId)
                                {
                                    float ratingScore = orderItem.getRate();
                                    if (ratingScore == 5){
                                        orderItemList.add(orderItem);
                                    }
                                    break;
                                }
                            }
                        }

                        reviewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Load sold


        //
    }
}