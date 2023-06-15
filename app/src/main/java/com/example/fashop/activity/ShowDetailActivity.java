package com.example.fashop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fashop.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.viewPage2Adapter;
import Fragment.ReturnPolicyFragment;
import Model.ProductModel;
import Fragment.ProductVariantFragment;
import me.relex.circleindicator.CircleIndicator3;

public class ShowDetailActivity extends AppCompatActivity {
    private TextView addToCartBtn, buyNow;
    private TextView titleTxt, feeTxt, descriptionTxt;
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

                ProductVariantFragment bottomSheetFragment = new ProductVariantFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ShowDetailActivity.this, ProductVariantActivity.class));
//                ProductVariantFragment bottomSheetFragment = new ProductVariantFragment();
//                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
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