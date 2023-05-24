package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;

import Adapter.CategoryAdapter;
import Adapter.ModelAdapter;
import MyClass.ClothingDomain;

import com.example.fashop.activity.LoginActivity;
import Adapter.PopularAdapter;
import Model.ModelImage;
import Model.ProductCategory;
import Model.ProductModel;

import com.example.fashop.activity.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
    private List<ProductCategory> categories = new ArrayList<>();
    private List<ProductModel> modelList = new ArrayList<>();
    private List<ProductModel> searchModel = new ArrayList<>();
    private List<ModelImage> modelImageList = new ArrayList<>();
    private RecyclerView rcCategories;
    private CategoryAdapter categoryAdapter;
    private RecyclerView rcModels;
    private ModelAdapter modelAdapter;


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
        rcCategories = view.findViewById(R.id.rcCategories);
        rcModels = view.findViewById(R.id.rcModel);

        EditText edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("model_list_key", new Gson().toJson(modelList));
                startActivity(intent);
                //Log.v("Hi", "Works perfectly");
            }

        });

        /*edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // This method will be invoked before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method will be invoked whenever the text is changed.
                if (charSequence.toString() == "") loadModel();
                // startActivity(new Intent(context, SearchActivity.class));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method will be invoked after the text has been changed.
                String[] searchText = editable.toString().split(" ");
                searchModel.clear();
                for (ProductModel models : modelList)
                {
                    for (String text : searchText)
                    {
                        if(models.getName().contains(text))
                        {
                            searchModel.add(models);
                        }
                    }
                }
                GridLayoutManager manager = new GridLayoutManager(context, 2);

                // Adapter Category
                rcModels.setLayoutManager(manager);
                modelAdapter = new ModelAdapter(searchModel);
                rcModels.setAdapter(modelAdapter);
            }
        });*/

        checkUser();
        loadCategory();
        recyclerViewPopular();
        loadModel();
    }

    private void loadModel() {
        GridLayoutManager manager = new GridLayoutManager(context, 2);

        // Adapter Category
        rcModels.setLayoutManager(manager);
        modelAdapter = new ModelAdapter(modelList);
        rcModels.setAdapter(modelAdapter);
        getModelData();
    }

    private void getModelData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Model");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null)
                {
                    modelList.add(model);
                    modelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null && modelList != null && !modelList.isEmpty())
                {
                    int len = modelList.size();

                    for (int i = 0; i < len; ++i)
                    {
                        if (modelList.get(i).getID() == model.getID())
                        {
                            model.setImages(modelList.get(i).getImages());
                            modelList.set(i, model);
                            modelAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ProductModel model = dataSnapshot.getValue(ProductModel.class);
                if (model != null && modelList != null && !modelList.isEmpty())
                {
                    int len = modelList.size();
                    for (int i = 0; i < len; ++i)
                    {
                        if (modelList.get(i).getID() == model.getID())
                        {
                            modelList.remove(i);
                            modelAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ModelImage");
        ChildEventListener childEventListener2 = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ModelImage modelImage = dataSnapshot.getValue(ModelImage.class);
                if (modelImage != null)
                {
                    modelImageList.add(modelImage);
                    joinModelWithImage();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                ModelImage modelImage = dataSnapshot.getValue(ModelImage.class);
                if (modelImage != null && modelImageList != null && !modelImageList.isEmpty())
                {
                    int len = modelImageList.size();
                    for (int i = 0; i < len; ++i)
                    {
                        if (modelImageList.get(i).getID() == modelImage.getID())
                        {
                            modelImageList.set(i, modelImage);
                            joinModelWithImage();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ModelImage modelImage = dataSnapshot.getValue(ModelImage.class);
                if (modelImage != null && modelImageList != null && !modelImageList.isEmpty())
                {
                    int len = modelImageList.size();
                    for (int i = 0; i < len; ++i)
                    {
                        if (modelImageList.get(i).getID() == modelImage.getID())
                        {
                            modelImageList.remove(i);
                            joinModelWithImage();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        ref.addChildEventListener(childEventListener);
        ref2.addChildEventListener(childEventListener2);
    }

    private void joinModelWithImage()
    {
        for (ProductModel model : modelList){
            int id = model.getID();
            List<String> urls = new ArrayList<>();
            for (ModelImage modelImage : modelImageList){
                if (modelImage.getModelID() == id)
                {
                    urls.add(modelImage.getUrl());
                }
            }
            model.setImages(urls);
        }
        modelAdapter.notifyDataSetChanged();
    }
    private void loadCategory() {

        getCategoryData();

        LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);

        // Adapter Category
        rcCategories.setLayoutManager(manager);
        rcCategories.addItemDecoration(dividerItemDecoration);
        categoryAdapter = new CategoryAdapter(categories);
        rcCategories.setAdapter(categoryAdapter);
    }


    private void getCategoryData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                categories.clear();
                for (DataSnapshot child1: snapshot1.getChildren())
                {
                    ProductCategory category = child1.getValue(ProductCategory.class);
                    categories.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });
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


        adapter = new PopularAdapter(foodList);
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