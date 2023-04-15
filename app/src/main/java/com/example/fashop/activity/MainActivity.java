package com.example.fashop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.fashop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

import fragment.HomeFragment;
import fragment.MeFragment;
import fragment.NotificationFragment;
import fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new HomeFragment());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            replaceFragment(itemIdToFragment(item.getItemId()));
            return true;
        });
    }

    private Fragment itemIdToFragment(int id)
    {
        if (id == R.id.notification) {
            return new NotificationFragment();
        }
        else if (id == R.id.settings) {
            return new SettingsFragment();
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
}