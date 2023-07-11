package com.example.fashop.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fashop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapter.ReviewAdapter;
import Fragment.HomeFragment;
import Model.OrderItem;
import Model.ProductModel;
import Model.ProductVariant;
import Fragment.OneStarFragment;
import Fragment.TwoStarFragment;
import Fragment.ThreeStarFragment;
import Fragment.FourStarFragment;
import Fragment.FiveStarFragment;

public class RatingListActivity extends AppCompatActivity {


    private ProductModel object;
    private List<OrderItem> orderItemList = new ArrayList<>();

    private List<Integer> variantIDList = new ArrayList<>();

    private int position = 4;

    private LinearLayout star5Btn, star4Btn, star3Btn, star2Btn, star1Btn;

    private List<LinearLayout> btnList;

    private ImageButton backBtn;

    private TextView tvQuantity1, tvQuantity2, tvQuantity3, tvQuantity4, tvQuantity5;

    private int quantity1Star = 0, quantity2Star = 0, quantity3Star = 0, quantity4Star = 0, quantity5Star = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_list);

        initView();
        btnList = new ArrayList<>(Arrays.asList(star1Btn, star2Btn, star3Btn, star4Btn, star5Btn));
        initListener();
        object = (ProductModel)  getIntent().getSerializableExtra("object");
        loadFeedback_Stats();
    }

    private void defaultRatingList(int selectedBtn) {
        position = selectedBtn;
        btnList.get(selectedBtn).setBackgroundResource(R.drawable.selected_size_bg);
        switch (selectedBtn){
            case 4:
                FiveStarFragment fiveStarFragment = FiveStarFragment.newInstance(object.getID());
                replaceFragment(fiveStarFragment);
                break;
            case 3:
                FourStarFragment fourStarFragment = FourStarFragment.newInstance(object.getID());
                replaceFragment(fourStarFragment);
                break;
            case 2:
                ThreeStarFragment threeStarFragment = ThreeStarFragment.newInstance(object.getID());
                replaceFragment(threeStarFragment);
                break;
            case 1:
                TwoStarFragment twoStarFragment = TwoStarFragment.newInstance(object.getID());
                replaceFragment(twoStarFragment);
                break;
            case 0:
                OneStarFragment oneStarFragment = OneStarFragment.newInstance(object.getID());
                replaceFragment(oneStarFragment);
                break;
        }

    }

    private void changeBorderBtn(int selectedBtn, int oldBtn){
        btnList.get(oldBtn).setBackgroundResource(R.drawable.size_bg);
        btnList.get(selectedBtn).setBackgroundResource(R.drawable.selected_size_bg);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void initView() {
        star5Btn = findViewById(R.id.star5Btn);
        star4Btn = findViewById(R.id.star4Btn);
        star3Btn = findViewById(R.id.star3Btn);
        star2Btn = findViewById(R.id.star2Btn);
        star1Btn = findViewById(R.id.star1Btn);
        backBtn = findViewById(R.id.backBtn);
        tvQuantity1 = findViewById(R.id.tvQuantity1);
        tvQuantity2 = findViewById(R.id.tvQuantity2);
        tvQuantity3 = findViewById(R.id.tvQuantity3);
        tvQuantity4 = findViewById(R.id.tvQuantity4);
        tvQuantity5 = findViewById(R.id.tvQuantity5);
    }

    private void initListener(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        star5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != 4)
                {
                    int oldPosition = position;
                    position = 4;
                    changeBorderBtn(position, oldPosition);
                    FiveStarFragment fiveStarFragment = FiveStarFragment.newInstance(object.getID());
                    replaceFragment(fiveStarFragment);
                }
            }
        });

        star4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != 3)
                {
                    int oldPosition = position;
                    position = 3;
                    changeBorderBtn(position, oldPosition);
                    FourStarFragment fourStarFragment = FourStarFragment.newInstance(object.getID());
                    replaceFragment(fourStarFragment);
                }
            }
        });

        star3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != 2)
                {
                    int oldPosition = position;
                    position = 2;
                    changeBorderBtn(position, oldPosition);
                    ThreeStarFragment threeStarFragment = ThreeStarFragment.newInstance(object.getID());
                    replaceFragment(threeStarFragment);
                }
            }
        });

        star2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != 1)
                {
                    int oldPosition = position;
                    position = 1;
                    changeBorderBtn(position, oldPosition);
                    TwoStarFragment twoStarFragment = TwoStarFragment.newInstance(object.getID());
                    replaceFragment(twoStarFragment);
                }
            }
        });
        star1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != 0)
                {
                    int oldPosition = position;
                    position = 0;
                    changeBorderBtn(position, oldPosition);
                    OneStarFragment oneStarFragment = OneStarFragment.newInstance(object.getID());
                    replaceFragment(oneStarFragment);
                }
            }
        });
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
                                    float ratingScore = orderItem.getRate();
                                    if (ratingScore != 0){
                                        switch ((int)ratingScore){
                                            case 1:
                                                quantity1Star++;
                                                break;
                                            case 2:
                                                quantity2Star++;
                                                break;
                                            case 3:
                                                quantity3Star++;
                                                break;
                                            case 4:
                                                quantity4Star++;
                                                break;
                                            case 5:
                                                quantity5Star++;
                                                break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (quantity1Star == 0)
                        {
                            star1Btn.setEnabled(false);
                        }
                        if (quantity2Star == 0)
                        {
                            star2Btn.setEnabled(false);
                        }
                        if (quantity3Star == 0)
                        {
                            star3Btn.setEnabled(false);
                        }
                        if (quantity4Star == 0)
                        {
                            star4Btn.setEnabled(false);
                        }
                        if (quantity5Star == 0)
                        {
                            star5Btn.setEnabled(false);
                        }
                        tvQuantity1.setText('(' + String.valueOf(quantity1Star) + ')');
                        tvQuantity2.setText('(' +String.valueOf(quantity2Star)+ ')');
                        tvQuantity3.setText('(' +String.valueOf(quantity3Star)+ ')');
                        tvQuantity4.setText('(' +String.valueOf(quantity4Star)+ ')');
                        tvQuantity5.setText('(' +String.valueOf(quantity5Star)+ ')');

                        List<Integer> temptList = new ArrayList<>(Arrays.asList(quantity1Star, quantity2Star, quantity3Star, quantity4Star, quantity5Star));
                        for (int i = 4; i >= 0; i--){
                            if (temptList.get(i) != 0){
                                defaultRatingList(i);
                                break;
                            }
                        }
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