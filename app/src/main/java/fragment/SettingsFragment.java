package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.fashop.R;
import com.example.fashop.activity.CommunityRulesActivity;
import com.example.fashop.activity.FashopPoliciesActivity;
import com.example.fashop.activity.HelpCenterActivity;
import com.example.fashop.activity.MainActivity;

public class SettingsFragment extends Fragment {

    Context context;
    private Switch darkModeBtn;
    TextView tvHelpCenter;
    TextView tvCommunityRules;
    TextView tvFashopPolicies;

    public SettingsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initUI(view);


        darkModeBtn.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        return view;
    }

    private void initUI(View view)
    {
        darkModeBtn = view.findViewById(R.id.darkModeBtn);

        tvHelpCenter = view.findViewById(R.id.tvHelpCenter);

        tvCommunityRules = view.findViewById(R.id.tvCommunityRules);

        tvFashopPolicies = view.findViewById(R.id.tvFashopPolicies);

        initListener();
    }

    void initListener()
    {
        darkModeBtn.setOnClickListener(v -> {
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        tvHelpCenter.setOnClickListener(v -> {
            Intent intent = new Intent(context, HelpCenterActivity.class);
            startActivity(intent);
        });

        tvCommunityRules.setOnClickListener(v ->{
            Intent intent = new Intent(context, CommunityRulesActivity.class);
            startActivity(intent);
        });

        tvFashopPolicies.setOnClickListener(v->{
            Intent intent = new Intent(context, FashopPoliciesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onPause() {
        super.onPause();

    }

}