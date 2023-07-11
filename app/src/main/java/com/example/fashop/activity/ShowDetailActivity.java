package com.example.fashop.activity;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fashop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import Adapter.ReviewAdapter;
import Adapter.viewPage2Adapter;
import Fragment.ReturnPolicyFragment;
import Model.OrderItem;
import Model.ProductModel;
import Fragment.ProductVariantFragment;
import Model.ProductVariant;
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

    private TextView rating1Tv, rating2Tv, soldTv, reviewQuantityTv, seeMoreReviewsBtn;
    private RecyclerView reviewRecycler;
    private ReviewAdapter reviewAdapter;

    private List<OrderItem> orderItemList = new ArrayList<>();

    private List<Integer> variantIDList = new ArrayList<>();

    private float ratingScore = 0;
    private int soldProductQuantity = 0;
    private int reviewCount = 0;

    private LinearLayout ratingListBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);



//        managementCart = new ManagementCart(this);

        initView();
        initListener();
        getBundle();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewRecycler.setLayoutManager(linearLayoutManager);
        reviewAdapter = new ReviewAdapter(orderItemList);
        reviewRecycler.setAdapter(reviewAdapter);
        loadFeedback_Stats();
    }

    private void initListener() {
        ratingListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowDetailActivity.this, RatingListActivity.class);
                intent.putExtra("object", object);
                startActivity(intent);
            }
        });
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

        rating1Tv = findViewById(R.id.rating1Tv);
        rating2Tv = findViewById(R.id.rating2Tv);
        soldTv = findViewById(R.id.soldTv);
        reviewQuantityTv = findViewById(R.id.reviewQuantityTv);
        ratingListBtn = findViewById(R.id.ratingListBtn);
//        seeMoreReviewsBtn = findViewById(R.id.seeMoreReviewsBtn);
        reviewRecycler = findViewById(R.id.reviewRecycler);

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


    private void loadFeedback_Stats(){
        //get orderItemList
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Variant");
        Query variantQuery = ref.orderByChild("modelID").equalTo(object.getID());
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
                                    if (orderItem.getRate() != 0){
                                        ratingScore += orderItem.getRate();
                                        reviewCount++;
                                        if (reviewCount <= 5)
                                            orderItemList.add(orderItem);
                                    }
                                    soldProductQuantity += orderItem.getQuantity();
                                    break;
                                }
                            }
                        }

                        if (reviewCount <= 5){
                            ratingListBtn.setVisibility(View.GONE);
                        }
                        else{
                            ratingListBtn.setVisibility(View.VISIBLE);
                        }

//                            Log.e("sizebeafter", String.valueOf(orderItemList.size()));
                        reviewAdapter.notifyDataSetChanged();

                        float averageRating = ratingScore / reviewCount;
                        // Round the averageRating to one decimal place

                        if (!Float.isNaN(averageRating) && !Float.isInfinite(averageRating))
                        {
                            BigDecimal roundedRating = new BigDecimal(averageRating).setScale(1, RoundingMode.HALF_UP);
                            // Convert the roundedRating to a float
                            averageRating = roundedRating.floatValue();
                        }


                        rating1Tv.setText(String.valueOf(averageRating));
                        rating2Tv.setText(String.valueOf(averageRating));
                        soldTv.setText(String.valueOf(soldProductQuantity) + " sold");
                        reviewQuantityTv.setText(" (" + String.valueOf(reviewCount) + ')');






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