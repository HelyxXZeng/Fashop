package com.example.fashop.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.fashop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

import fragment.HomeFragment;
import fragment.MeFragment;
import fragment.NotificationFragment;
import fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

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
                searchLauncher.launch(new Intent(MainActivity.this, SearchActivity.class));
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