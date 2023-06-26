package Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fashop.R;
import com.example.fashop.activity.AboutUsActivity;
import com.example.fashop.activity.LegalInformationActivity;
import com.example.fashop.activity.LoginActivity;
import com.example.fashop.activity.MainActivity;
import com.example.fashop.activity.OrderHistoryActivity;
import com.example.fashop.activity.PrivacyPolicyActivity;
import com.example.fashop.activity.ProfileEditUserActivity;
import com.example.fashop.activity.QuestionsActivity;
import com.example.fashop.activity.SettingActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import MyClass.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {

    Context context;
    private LinearLayout tvPrivacyPolicy;
    private LinearLayout tvAboutUs;
    private LinearLayout tvLegalInformation;
    private LinearLayout tvQuestions;
    private LinearLayout orderHistory;

    //
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ImageView imgAvt;
    private TextView tvName, tvPassword, tvAddress, tvEmail, tvPhone, tvUserName, tvUserEmail;
    private LinearLayout PendingLayout, ConfirmedLayout, ShippingLayout, CompletedLayout;
    TextView pendingBadge, confirmedBadge, shippingBadge, completedBadge;

    private ImageButton editBtn;

    private LinearLayout logoutBtn;
    //

    private SwitchCompat fcmSwitch;
    TextView notificationStatusTv;

    private static final String enableMessage = "Notification are enabled";
    private static final String disabledMessage = "Notification are disable";

    private boolean isChecked = false;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private LinearLayout hotlineBtn;
    private TextView hotlineNumber;

    private String hotline;

    public MeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void initUI(View view) {
        tvPrivacyPolicy = view.findViewById(R.id.tvPrivacyPolicy);
        tvQuestions = view.findViewById(R.id.tvQuestions);
        tvLegalInformation = view.findViewById(R.id.tvLegalInformation);
        tvAboutUs = view.findViewById(R.id.tvAboutUs);

        //
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);

        imgAvt = view.findViewById(R.id.imgAvt);
        tvName = view.findViewById(R.id.tvName);
        tvPassword = view.findViewById(R.id.tvPassword);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        editBtn = view.findViewById(R.id.editBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        orderHistory = view.findViewById(R.id.order_history);

        fcmSwitch = view.findViewById(R.id.fcmSwitch);
        notificationStatusTv = view.findViewById(R.id.notificationStatusTv);
        hotlineBtn = view.findViewById(R.id.hotlineBtn);
        hotlineNumber = view.findViewById(R.id.hotlineNumber);
        //init shared preferences
        sp = context.getSharedPreferences("SETTINGS_SP", context.MODE_PRIVATE);
        //check last selected option; true/false
        isChecked = sp.getBoolean("FCM_ENABLED", false);
        fcmSwitch.setChecked(isChecked);
        if (isChecked){
            notificationStatusTv.setText(enableMessage);
        }
        else {
            notificationStatusTv.setText(disabledMessage);
        }

        PendingLayout = view.findViewById(R.id.PendingLayout);
        ConfirmedLayout = view.findViewById(R.id.ConfirmedLayout);
        CompletedLayout = view.findViewById(R.id.CompletedLayout);
        ShippingLayout = view.findViewById(R.id.ShippingLayout);

        pendingBadge = view.findViewById(R.id.pendingBadge);
        shippingBadge = view.findViewById(R.id.shippingBadge);
        confirmedBadge = view.findViewById(R.id.confirmedBadge);
        completedBadge = view.findViewById(R.id.completedBadge);

        //

        checkUser();
        loadHotLine();
        loadBadger();
        initListener();
    }

    private void loadBadger(){

// Set the badge on the LinearLayout
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Order");
        Query cartItems = ref.orderByChild("customerID").equalTo(FirebaseAuth.getInstance().getUid());
        cartItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int pendingOrder = 0;
                int shippingOrder = 0;
                int confirmedOrder = 0;
                int completedOrder = 0;
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String status = orderSnapshot.child("status").getValue(String.class);
                    if (status == null) continue;
                    if (status.equals("PENDING")) {
                        pendingOrder++;
                    } else if (status.equals("SHIPPING")) {
                        shippingOrder++;
                    } else if (status.equals("CONFIRMED")) {
                        confirmedOrder++;
                    } else if (status.equals("COMPLETED")) {
                        completedOrder++;
                    }
                }
                pendingBadge.setText(Integer.toString(pendingOrder));
                shippingBadge.setText(Integer.toString(shippingOrder));
                completedBadge.setText(Integer.toString(completedOrder));
                confirmedBadge.setText(Integer.toString(confirmedOrder));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadHotLine() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){

                            hotline = "" + ds.child("phone").getValue();
                            hotlineNumber.setText(hotline);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
        {
            startActivity(new Intent(context, LoginActivity.class));
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
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();

                            String address = ""+ds.child("streetAddress").getValue() +", "
                                    + ds.child("ward").getValue()  +", "
                                    + ds.child("district").getValue()  +", "
                                    + ds.child("city").getValue();
                            //don't show password
//                            String password = ""+ds.child("password").getValue();

                            String profileImage = ""+ds.child("profileImage").getValue();

                            tvUserName.setText(name);
                            tvName.setText(name);
                            tvUserEmail.setText(email);
                            tvEmail.setText(email);
//                            tvPassword.setText(password);
                            tvAddress.setText(address);
                            tvPhone.setText(phone);


                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.avt).into(imgAvt);
                            }
                            catch (Exception e){
                                imgAvt.setImageResource(R.drawable.person_gray);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                        //checkUser();
                        startActivity(new Intent(context, LoginActivity.class));
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
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

    private void initListener() {
        PendingLayout.setOnClickListener(v->{
            Intent intent = new Intent(context, OrderHistoryActivity.class);
            Bundle args = new Bundle();
            args.putInt("tabIndex", 0);
            intent.putExtras(args);
            startActivity(intent);
        });

        CompletedLayout.setOnClickListener(v->{
            Intent intent = new Intent(context, OrderHistoryActivity.class);
            Bundle args = new Bundle();
            args.putInt("tabIndex", 3);
            intent.putExtras(args);
            startActivity(intent);
        });

        ConfirmedLayout.setOnClickListener(v->{
            Intent intent = new Intent(context, OrderHistoryActivity.class);
            Bundle args = new Bundle();
            args.putInt("tabIndex", 1);
            intent.putExtras(args);
            startActivity(intent);
        });

        ShippingLayout.setOnClickListener(v->{
            Intent intent = new Intent(context, OrderHistoryActivity.class);
            Bundle args = new Bundle();
            args.putInt("tabIndex", 2);
            intent.putExtras(args);
            startActivity(intent);
        });

        tvPrivacyPolicy.setOnClickListener(v->{
            Intent intent = new Intent(context, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        tvQuestions.setOnClickListener(v->{
            Intent intent = new Intent(context, QuestionsActivity.class);
            startActivity(intent);
        });

        tvLegalInformation.setOnClickListener(v->{
            Intent intent = new Intent(context, LegalInformationActivity.class);
            startActivity(intent);
        });

        tvAboutUs.setOnClickListener(v->{
            Intent intent = new Intent(context, AboutUsActivity.class);
            startActivity(intent);
        });
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileEditUserActivity.class);
            startActivity(intent);
        });
        orderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderHistoryActivity.class);
            Bundle args = new Bundle();
            args.putInt("tabIndex", 0);
            intent.putExtras(args);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make offline
                //sign out
                // go to login activity
                makeMeOffline();

                //getActivity().finish();
            }
        });

        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //checked, enable notifications
                    subscribeToTopic();
                }
                else {
                    //uncheck, disable notifications
                    unSubscribeToTopic();
                }
            }
        });

        hotlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = hotline; // Replace with your desired phone number

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));

                Context context = view.getContext(); // Get the context from the button view

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });
    }

    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //save setting in shared preferences
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", true);
                        spEditor.apply();

                        Toast.makeText(context, ""+enableMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(enableMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unSubscribeToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //save setting in shared preferences
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED", false);
                        spEditor.apply();

                        Toast.makeText(context, ""+disabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(disabledMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        initUI(view);
        return view;
    }
}