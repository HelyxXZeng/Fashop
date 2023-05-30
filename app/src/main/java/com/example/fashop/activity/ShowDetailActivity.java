package com.example.fashop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.fashop.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.viewPage2Adapter;
import Model.ProductModel;
import MyClass.ManagementCart;
import MyClass.ClothingDomain;
import MyClass.Photo;
import me.relex.circleindicator.CircleIndicator3;

public class ShowDetailActivity extends AppCompatActivity {
    private TextView addToCartBtn;
    private TextView titleTxt, feeTxt, descriptionTxt, numberOrderTxt;
    private ImageView plusBtn, minusBtn, picFood;
    private ProductModel object;
    int numberOrder = 1;

    private ManagementCart managementCart;


    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;
    private List<String> mListPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);



        managementCart = new ManagementCart(this);

        initView();
        getBundle();
    }

    private void getBundle(){
        object = (ProductModel)  getIntent().getSerializableExtra("object");
//        int drawableResourceId = this.getResources().getIdentifier(object.getPic(), "drawable", this.getPackageName() );
//        Glide.with(this)
//                .load(drawableResourceId)
//                .into(picFood);

        mListPhoto = object.getImages();

        titleTxt.setText(object.getName());
        feeTxt.setText("$" + object.getPrice());
        descriptionTxt.setText(object.getDescription());
        numberOrderTxt.setText(String.valueOf(numberOrder));

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

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                object.setNumberInCart(numberOrder);
                managementCart.insertFood(object);
            }
        });
    }

    private void initView() {
        addToCartBtn=findViewById(R.id.addToCartBtn);
        titleTxt=findViewById(R.id.titleTxt);
        feeTxt=findViewById(R.id.priceTxt);
        descriptionTxt=findViewById(R.id.descriptionTxt);
        numberOrderTxt=findViewById(R.id.numberOrderTxt);
        plusBtn=findViewById(R.id.plusBtn);
        minusBtn=findViewById(R.id.minusBtn);
//        picFood=findViewById(R.id.picFood);

        mViewPager2 = findViewById(R.id.view_pager_2);
        mCircleIndicator3 = findViewById(R.id.circle_indicator_3);
        mListPhoto = getListPhoto();

        viewPage2Adapter adapter = new viewPage2Adapter(mListPhoto);
        mViewPager2.setAdapter(adapter);

        mCircleIndicator3.setViewPager(mViewPager2);
    }

    private List<String> getListPhoto() {
        List<String> list = new ArrayList<>();
        object = (ProductModel)  getIntent().getSerializableExtra("object");
        list = object.getImages();
        return list;
    }
}