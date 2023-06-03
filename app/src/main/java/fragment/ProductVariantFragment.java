package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.example.fashop.activity.OnColorClickListener;
import com.example.fashop.activity.OnSizeClickListener;
import com.example.fashop.activity.SearchActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Adapter.ColorAdapter;
import Adapter.ModelAdapter;
import Adapter.SizeAdapter;
import Model.ProductModel;
import Model.ProductVariant;
import MyClass.GridSpacingItemDecoration;
import MyClass.ManagementCart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductVariantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductVariantFragment extends BottomSheetDialogFragment implements OnSizeClickListener, OnColorClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context context;

    private ImageView imgProduct;

    private ProductModel object;

    private ProductVariant selectedProductVariant;

    private TextView priceTxt, numberOrderTxt, addtoCartBtn;

    private ImageButton minusBtn, plusBtn, closeBtn;

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

    private SizeAdapter sizeAdapter;

    private ColorAdapter colorAdapter;

    private ManagementCart managementCart;

    private int selectedSizePosition = -1;

    private int selectedColorPosition = -1;

    public ProductVariantFragment() {
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
    public static ProductVariantFragment newInstance(String param1, String param2) {
        ProductVariantFragment fragment = new ProductVariantFragment();
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

        numberOrderTxt.setText(String.valueOf(numberOrder));

        // Lấy đối tượng Bundle từ Fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            object = (ProductModel) bundle.getSerializable("object");
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

//        addtoCartBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // đây là chỉ mới thay đổi ở tinyDB
////                object.setNumberInCart(numberOrder);
////                managementCart.insertFood(object);
//                // bỏ đoạn ở trên này và push lên thẳng firebase
//                // your source code
//                if (selectedProductVariant == null)
//                {
//                    Toast.makeText(context, "Please select your size and color", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        LoadImage();
        LoadColors();
        LoadSizes();
    }

    private void LoadImage(){
        try {
            Picasso.get().load(object.getImages().get(0)).placeholder(R.drawable.error).into(imgProduct);
        }
        catch (Exception e){
            imgProduct.setImageResource(R.drawable.error);
        }
        priceTxt.setText(String.valueOf(object.getPrice()));
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListSize != null){
                        mListSize.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ProductVariant productVariant = dataSnapshot.getValue(ProductVariant.class);
                    if (productVariant != null && productVariant.getModelID() == object.getID()){
                        mListSize.add(productVariant.getSize());
                    }
                }

                Set<String> uniqueSizes = new HashSet<>(mListSize);
                mListSize.clear();
                mListSize.addAll(uniqueSizes);
                sizeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "get size list failed", Toast.LENGTH_SHORT).show();
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListColor != null){
                    mListColor.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ProductVariant productVariant = dataSnapshot.getValue(ProductVariant.class);
                    if (productVariant != null && productVariant.getModelID() == object.getID() && productVariant.getSize().equals(String.valueOf(selectedSize))){
                        mListColor.add(productVariant.getColor());
                    }
                }

                Set<String> uniqueColors = new HashSet<>(mListColor);
                mListColor.clear();
                mListColor.addAll(uniqueColors);
                colorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "get color list failed", Toast.LENGTH_SHORT).show();
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
        managementCart = new ManagementCart(context);
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
        getSelectedVariantProduct();
    }

    @Override
    public void onColorClicked(String color, int position) {
        selectedColor = color;
        getSelectedVariantProduct();
    }


    private void getSelectedVariantProduct() {
        if (selectedSize != null && selectedColor != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ProductVariant productVariant = dataSnapshot.getValue(ProductVariant.class);
                        if (productVariant != null
                                && productVariant.getModelID() == object.getID()
                                && productVariant.getSize().equals(String.valueOf(selectedSize))
                                && productVariant.getColor().equals(String.valueOf(selectedColor))){
                            selectedProductVariant = productVariant;
                            setAddToCartClickListener();
                            Log.e("ProductVariantFragment", "Selected variant: " + selectedProductVariant.getID());
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

    private void setAddToCartClickListener() {
        addtoCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    // đây là chỉ mới thay đổi ở tinyDB
                    object.setNumberInCart(numberOrder);
                    managementCart.insertFood(object);
                    // bỏ đoạn ở trên này và push lên thẳng firebase
                    // your source code

            }
        });
    }
}