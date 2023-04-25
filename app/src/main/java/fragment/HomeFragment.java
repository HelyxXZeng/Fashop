package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fashop.R;
import com.example.fashop.activity.ClothingDomain;
import com.example.fashop.activity.LoginActivity;
import com.example.fashop.activity.PopularAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //

    Context context;
    private TextView tvHi;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ImageView imgAvt;
    //

    private RecyclerView.Adapter adapter;
    private RecyclerView recycleViewPopularList;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void initUI(View view) {
        tvHi = view.findViewById(R.id.tvHi);
        imgAvt = view.findViewById(R.id.imgAvt);
        recycleViewPopularList= view.findViewById(R.id.rcPopular);
        checkUser();
        recyclerViewPopular();
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
        {
            startActivity(new Intent(context, LoginActivity.class));
        }
        else{
            loadMyInfo();
        }

    }


    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        for (DataSnapshot ds: datasnapshot.getChildren()){
                            String name = "" + ds.child("name").getValue();
                            tvHi.setText("Hi " + name);

                            String profileImage = ""+ds.child("profileImage").getValue();

                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.avt).into(imgAvt);
                            }
                            catch (Exception e){
                                imgAvt.setImageResource(R.drawable.person_gray);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void recyclerViewPopular(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recycleViewPopularList.setLayoutManager(linearLayoutManager);

        ArrayList<ClothingDomain> foodList = new ArrayList<>();
        foodList.add(new ClothingDomain("clothing1", "clothing1", "UNIQUE DESIGN - The mens suits features single breasted, one button closure, notched collar lapel, welted pocket at left chest.", 10.0 ));
        foodList.add(new ClothingDomain("clothing2", "clothing2", "UNIQUE DESIGN - The mens suits features single breasted, one button closure, notched collar lapel, welted pocket at left chest, 2 front flap pockets and 4 sleeve buttons on each side.", 12.0));
        foodList.add(new ClothingDomain("clothing3", "clothing3", "UNIQUE DESIGN - The mens suits features single breasted, one button closure, notched collar lapel, welted pocket at left chest, 2 front flap pockets and 4 sleeve buttons on each side.", 20.0 ));


        adapter = new PopularAdaptor(foodList);
        recycleViewPopularList.setAdapter(adapter);
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(view);

        return view;
    }
}