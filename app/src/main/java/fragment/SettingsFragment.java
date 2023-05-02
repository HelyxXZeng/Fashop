package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.example.fashop.activity.CommunityRulesActivity;
import com.example.fashop.activity.FashopPoliciesActivity;
import com.example.fashop.activity.HelpCenterActivity;
import com.example.fashop.activity.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsFragment extends Fragment {

    Context context;
    private Switch darkModeBtn;
    TextView tvHelpCenter;
    TextView tvCommunityRules;
    TextView tvFashopPolicies;

    //

    Button logoutBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    //
    public SettingsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
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

        logoutBtn = view.findViewById(R.id.logoutBtn);

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

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make offline
                //sign out
                // go to login activity
                makeMeOffline();
            }
        });
    }

    private void makeMeOffline() {
        //after logging in, make user online
        progressDialog.setMessage("Logging Out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //update successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
        {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
        else{
            loadMyInfo();
        }

    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        for (DataSnapshot ds: datasnapshot.getChildren()){
                            String name = "" + ds.child("name").getValue();
                            String accountType = "" + ds.child("accountType").getValue();

//                            nameTv.setText(name + "("+accountType+")");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();

    }

}