package com.example.fashop.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.fashop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
                startActivity(new Intent(MainActivity.this, CartListActivity.class));
                //return true;
            }
            else {
                replaceFragment(itemIdToFragment(item.getItemId()));
            }
            return true;
        });



        //
//        floatingActionButton = findViewById(R.id.cartBtn);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, CartListActivity.class));
//            }
//        });
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
}