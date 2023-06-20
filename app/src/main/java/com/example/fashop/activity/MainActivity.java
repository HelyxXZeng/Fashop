package com.example.fashop.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.fashop.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

import Fragment.HomeFragment;
import Fragment.MeFragment;
import Fragment.NotificationFragment;
import Model.ProductCategory;
import Model.ProductModel;

public class MainActivity extends AppCompatActivity{

    private BottomNavigationView bottomNavigationView;
    Fragment currentFragment;

    //
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new HomeFragment());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.search) {
                //startActivity(new Intent(MainActivity.this, SearchActivity.class));
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                List<ProductModel> modelList = new HomeFragment().getModelList();
                String json = new Gson().toJson(modelList);
                editor.putString("model_list_key", json);

                List<ProductCategory> categories = new HomeFragment().getCategories();
                String json2 = new Gson().toJson(categories);
                editor.putString("categories_list_key", json2);

                editor.apply();
                searchLauncher.launch(intent);
                //return true;
            }
            else if (item.getItemId() == R.id.cart) {
                //startActivity(new Intent(MainActivity.this, CartListActivity.class));
                cartLauncher.launch(new Intent(MainActivity.this, CartListActivity.class));
                //return true;
            }
            else {
                replaceFragment(itemIdToFragment(item.getItemId()));
            }
            return true;
        });


        invalidateOptionsMenu();

        cartCounter();


        //
//        floatingActionButton = findViewById(R.id.cartBtn);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, CartListActivity.class));
//            }
//        });
    }

    private void cartCounter() {
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.cart);
        badgeDrawable.setVisible(true);
        badgeDrawable.setMaxCharacterCount(2);
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.redBadge));


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CartItem");
        Query cartItems = ref.orderByChild("customerID").equalTo(FirebaseAuth.getInstance().getUid());
                cartItems.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int quantityCartItem = (int) snapshot.getChildrenCount();
                        badgeDrawable.setNumber(quantityCartItem);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.bottom_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.cart);
//        View actionView = menuItem.getActionView();
//        TextView cartBadgeTextView = actionView.findViewById(R.id.cart_badge_tv);
//        cartBadgeTextView.setText("2");
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // Xử lý sự kiện khi menu được chọn
        return super.onOptionsItemSelected(item);
    }

    private Fragment itemIdToFragment(int id)
    {
//        if (id == R.id.search) {
//            return new SettingsFragment();
//        }
//        else if (id == R.id.settings) {
//            return new SettingsFragment();
//        }
//        else if (id == R.id.cart) {
//            return new SettingsFragment();
//        }
        if (id == R.id.notification) {
            return new NotificationFragment();
        }
        else if (id == R.id.me) {
            return new MeFragment();
        }
        else {
            return new HomeFragment();
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        saveState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        saveState();
    }

    private void saveState()
    {
        int id = bottomNavigationView.getSelectedItemId();
        Fragment fragment = itemIdToFragment(id);
        replaceFragment(fragment);
    }

    private ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    replaceFragment(new HomeFragment());
                }
            }
    );

    private ActivityResultLauncher<Intent> cartLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    replaceFragment(new HomeFragment());
                }
            }
    );
}