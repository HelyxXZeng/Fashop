package Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Adapter.ColorAdapter;
import Adapter.SizeAdapter;
import Interface.OnColorClickListener;
import Interface.OnSizeClickListener;
import Model.CartItem;
import Model.ModelImage;
import Model.ProductModel;
import Model.ProductVariant;
//import MyClass.ManagementCart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProductVariantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProductVariantFragment extends BottomSheetDialogFragment implements OnSizeClickListener, OnColorClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context context;

    private ImageView imgProduct;

    //private ProductModel object;

    private CartItem object;

    private ProductVariant selectedProductVariant;

    private TextView priceTxt, numberOrderTxt, addtoCartBtn;

    private ImageButton minusBtn, plusBtn, closeBtn;

    private boolean activatedAddcartBtn = false;

    int numberOrder = 1;

    String selectedSize, selectedColor;

    private RecyclerView recyclerViewSize, recyclerViewColor;

    private List<String> mListSize = new ArrayList<>();
    private List<String> mListColor = new ArrayList<>(Arrays.asList(
            "Red",
            "Green",
            "Blue",
            "Yellow",
            "Orange",
            "Purple",
            "Pink",
            "Brown",
            "Gray",
            "Black"
    ));
    private String size, color;

    private SizeAdapter sizeAdapter;

    private ColorAdapter colorAdapter;

    private ProgressDialog progressDialog;

//    private ManagementCart managementCart;

    private int selectedSizePosition = -1;

    private int selectedColorPosition = -1;

    public EditProductVariantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductVariantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProductVariantFragment newInstance(String param1, String param2) {
        EditProductVariantFragment fragment = new EditProductVariantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void initUI(View view) {
        imgProduct = view.findViewById(R.id.imgProduct);
        priceTxt = view.findViewById(R.id.priceTxt);
        minusBtn = view.findViewById(R.id.minusBtn);
        plusBtn = view.findViewById(R.id.plusBtn);
        numberOrderTxt = view.findViewById(R.id.numberOrderTxt);
        addtoCartBtn = view.findViewById(R.id.addtoCartBtn);
        recyclerViewSize = view.findViewById(R.id.sizesRv);
        recyclerViewColor = view.findViewById(R.id.colorsRv);
        closeBtn = view.findViewById(R.id.closeBtn);


        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);

        // Lấy đối tượng Bundle từ Fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            object = (CartItem) bundle.getSerializable("cartItem");
            numberOrder = object.getQuantity();
            numberOrderTxt.setText(String.valueOf(numberOrder));
            // Sử dụng đối tượng object trong Fragment
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close fragment
                dismissAllowingStateLoss();
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberOrder = numberOrder + 1;
                numberOrderTxt.setText(String.valueOf(numberOrder));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOrder > 1){
                    numberOrder = numberOrder - 1;
                }
                numberOrderTxt.setText(String.valueOf(numberOrder));
            }
        });

        addtoCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activatedAddcartBtn = true;
                getSelectedVariantProduct();
            }
        });
        LoadColors();
        LoadSizes();
    }




    private void LoadSizes(){
        GridLayoutManager manager = new GridLayoutManager(context, 8);

        // Adapter Category
        recyclerViewSize.setLayoutManager(manager);
        sizeAdapter = new SizeAdapter(context, mListSize, selectedSizePosition, colorAdapter);
        sizeAdapter.setOnSizeClickListener(this); // Set the listener
        recyclerViewSize.setAdapter(sizeAdapter);

        getSizeData();
    }
    private void getSizeData(){

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Variant" + "/" + object.getVariantID());

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductVariant productVariant = snapshot.getValue(ProductVariant.class);

                if (productVariant != null){
                    //get color size
                    size = productVariant.getSize();
                    color = productVariant.getColor();

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Model" + "/" + productVariant.getModelID());
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);

                            if (productModel != null){
                                //get price
                                Double price = productModel.getPrice();
                                priceTxt.setText(String.valueOf(price));

                                //get size list
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (mListSize != null){
                                            mListSize.clear();
                                        }
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            ProductVariant productVariant = dataSnapshot.getValue(ProductVariant.class);
                                            if (productVariant != null && productVariant.getModelID() == productModel.getID()){
                                                String sizeData = productVariant.getSize();
                                                mListSize.add(sizeData);
                                            }
                                        }

                                        Set<String> uniqueSizes = new HashSet<>(mListSize);
                                        mListSize.clear();
                                        mListSize.addAll(uniqueSizes);

                                        for (int i = 0; i < mListSize.size(); i++){
                                            if (mListSize.get(i).equals(size)){
                                                sizeAdapter.setSelectedSizePosition(i);
                                                onSizeClicked(size, i);
//                                                colorAdapter.setEnabled(true);
//                                                for (int j = 0; j < mListColor.size(); j++) {
//                                                    Log.e("aa", "color" + mListColor.get(j));
//                                                    if (mListColor.get(j).equals(color)) {
//                                                        colorAdapter.setSelectedColorPosition(j);
//                                                        onColorClicked(color, j);
//                                                        break;
//                                                    }
//                                                }
//
                                                break;
                                            }
                                        }
                                        sizeAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(context, "get size list failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //get image infor
                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("ModelImage");
                    ref3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ModelImage modelImage = dataSnapshot.getValue(ModelImage.class);
                                if (modelImage != null && modelImage.getModelID() == productVariant.getModelID()){
                                    object.setImage(modelImage.getUrl());
                                    try {
                                        Picasso.get().load(object.getImage()).placeholder(R.drawable.error).into(imgProduct);
                                    }
                                    catch (Exception e){
                                        imgProduct.setImageResource(R.drawable.error);
                                    }
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void LoadColors(){
        GridLayoutManager manager = new GridLayoutManager(context, 6);

        // Adapter Category
        recyclerViewColor.setLayoutManager(manager);
        colorAdapter = new ColorAdapter(context, mListColor, selectedColorPosition );
        colorAdapter.setOnColorClickListener(this); // Set the listener
        recyclerViewColor.setAdapter(colorAdapter);
    }

    private void getColorData(String selectedSize){


        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Variant" + "/" + object.getVariantID());

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductVariant productVariant = snapshot.getValue(ProductVariant.class);

                if (productVariant != null){
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Model" + "/" + productVariant.getModelID());
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);

                            if (productModel != null){

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (mListColor != null){
                                            mListColor.clear();
                                        }
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            ProductVariant productVariant = dataSnapshot.getValue(ProductVariant.class);
                                            if (productVariant != null && productVariant.getModelID() == productModel.getID() && productVariant.getSize().equals(String.valueOf(selectedSize))){
                                                mListColor.add(productVariant.getColor());
                                            }
                                        }

                                        Set<String> uniqueColors = new HashSet<>(mListColor);
                                        mListColor.clear();
                                        mListColor.addAll(uniqueColors);
                                        if (size != null && color != null){
                                            colorAdapter.setEnabled(true);
                                            for (int j = 0; j < mListColor.size(); j++) {
                                                Log.e("aa", "color" + mListColor.get(j));
                                                if (mListColor.get(j).equals(color)) {
                                                    colorAdapter.setSelectedColorPosition(j);
                                                    onColorClicked(color, j);
                                                    break;
                                                }
                                            }
                                        }
                                        colorAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(context, "get color list failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getContext();
//        managementCart = new ManagementCart(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_variant, container, false);
        selectedSize = null;
        selectedColor = null;

        initUI(view);
        return view;
    }

    @Override
    public void onSizeClicked(String size, int position) {
        getColorData(size);
        selectedSize = size;
        activatedAddcartBtn = false;
        getSelectedVariantProduct();
    }

    @Override
    public void onColorClicked(String color, int position) {
        selectedColor = color;
        activatedAddcartBtn = false;
        getSelectedVariantProduct();
    }


    private void getSelectedVariantProduct() {

        if (selectedSize == null && activatedAddcartBtn == true){
            Toast.makeText(context, "Please select size", Toast.LENGTH_SHORT).show();
            activatedAddcartBtn = false;
        }
        else if (selectedColor == null && activatedAddcartBtn == true){
            Toast.makeText(context, "Please select color", Toast.LENGTH_SHORT).show();
            activatedAddcartBtn = false;
        }
        else if (selectedSize != null && selectedColor != null && activatedAddcartBtn == true) {
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Variant" + "/" + object.getVariantID());

            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProductVariant productVariant = snapshot.getValue(ProductVariant.class);

                    if (productVariant != null){
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Model" + "/" + productVariant.getModelID());
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ProductModel productModel = snapshot.getValue(ProductModel.class);

                                if (productModel != null){

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");

                                    ref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                ProductVariant productVariant = dataSnapshot.getValue(ProductVariant.class);
                                                if (productVariant != null
                                                        && productVariant.getModelID() == productModel.getID()
                                                        && productVariant.getSize().equals(String.valueOf(selectedSize))
                                                        && productVariant.getColor().equals(String.valueOf(selectedColor))){
                                                    selectedProductVariant = productVariant;
                                                    handleSelectedProductVariant(selectedProductVariant); // Gọi phương thức xử lý sau khi có selectedProductVariant
                                                    activatedAddcartBtn = false;
                                                    break;
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(context, "get selected variant product failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void handleSelectedProductVariant(ProductVariant selectedProductVariant) {
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CartItem");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                CartItem newCartItem = new CartItem();

                newCartItem.setID(object.getID());
                newCartItem.setQuantity(numberOrder);
                newCartItem.setVariantID(selectedProductVariant.getID());
                newCartItem.setCustomerID(FirebaseAuth.getInstance().getUid());

                ref.child(String.valueOf(newCartItem.getID())).setValue(newCartItem)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dismissAllowingStateLoss();
                        progressDialog.dismiss();
                    }
                })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}