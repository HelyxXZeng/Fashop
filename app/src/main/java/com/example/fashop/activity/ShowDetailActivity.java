package com.example.fashop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.viewPage2Adapter;
import Fragment.ReturnPolicyFragment;
import Model.ProductModel;
import Fragment.ProductVariantFragment;
import me.relex.circleindicator.CircleIndicator3;

public class ShowDetailActivity extends AppCompatActivity {
    private TextView addToCartBtn, buyNow;
    private TextView titleTxt, feeTxt, descriptionTxt, shopAddressTv, buyerAddressTv;
    private ProductModel object;
//    private ManagementCart managementCart;


    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;
    private List<String> mListPhoto;

    private LinearLayout openReturnPolicyBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);



//        managementCart = new ManagementCart(this);

        initView();
        getBundle();
    }

    private void getBundle(){
        object = (ProductModel)  getIntent().getSerializableExtra("object");
        mListPhoto = object.getImages();

        titleTxt.setText(object.getName());
        feeTxt.setText("$" + object.getPrice());
        descriptionTxt.setText(object.getDescription());
        //numberOrderTxt.setText(String.valueOf(numberOrder));



        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                object.setNumberInCart(numberOrder);
//                managementCart.insertFood(object);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object", object);
                bundle.putSerializable("typeButton", "addToCart");

                ProductVariantFragment bottomSheetFragment = new ProductVariantFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("object", object);
                bundle.putSerializable("typeButton", "buyNow");

                ProductVariantFragment bottomSheetFragment = new ProductVariantFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        setShippingAdress();


    }

    private void setShippingAdress(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){

                            String city = "" + ds.child("city").getValue();
                            String district = "" + ds.child("district").getValue();
                            String buyerAddress = district + ", " + city;
                            buyerAddressTv.setText("To " + buyerAddress);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        ref.orderByChild("accountType").equalTo("Admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){

                            String city = "" + ds.child("city").getValue();
                            String district = "" + ds.child("district").getValue();
                            String shopAddress = district + ", " + city;
                            shopAddressTv.setText("From " + shopAddress);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

    private void initView() {
        addToCartBtn=findViewById(R.id.addToCartBtn);
        buyNow = findViewById(R.id.buyNow);
        titleTxt=findViewById(R.id.titleTxt);
        feeTxt=findViewById(R.id.priceTxt);
        descriptionTxt=findViewById(R.id.descriptionTxt);
        openReturnPolicyBtn = findViewById(R.id.openReturnPolicyBtn);
//        numberOrderTxt=findViewById(R.id.numberOrderTxt);
//        plusBtn=findViewById(R.id.plusBtn);
//        minusBtn=findViewById(R.id.minusBtn);
//        picFood=findViewById(R.id.picFood);
        shopAddressTv = findViewById(R.id.shopAddressTv);
        buyerAddressTv = findViewById(R.id.buyerAddressTv);


        mViewPager2 = findViewById(R.id.view_pager_2);
        mCircleIndicator3 = findViewById(R.id.circle_indicator_3);
        mListPhoto = getListPhoto();

        viewPage2Adapter adapter = new viewPage2Adapter(mListPhoto);
        mViewPager2.setAdapter(adapter);

        mCircleIndicator3.setViewPager(mViewPager2);

        openReturnPolicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReturnPolicyFragment bottomSheetFragment = new ReturnPolicyFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

    }

    private List<String> getListPhoto() {
        List<String> list = new ArrayList<>();
        object = (ProductModel)  getIntent().getSerializableExtra("object");
        list = object.getImages();
        return list;
    }
}