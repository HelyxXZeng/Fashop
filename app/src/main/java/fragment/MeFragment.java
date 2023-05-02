package fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fashop.R;
import com.example.fashop.activity.AboutUsActivity;
import com.example.fashop.activity.HelpCenterActivity;
import com.example.fashop.activity.LegalInformationActivity;
import com.example.fashop.activity.LoginActivity;
import com.example.fashop.activity.PrivacyPolicyActivity;
import com.example.fashop.activity.QuestionsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {

    Context context;
    private TextView tvPrivacyPolicy;
    private TextView tvAboutUs;
    private TextView tvLegalInformation;
    private TextView tvQuestions;

    //
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ImageView imgAvt;
    private TextView tvName, tvPassword, tvAddress, tvEmail, tvPhone, tvUserName, tvUserEmail;
    //

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
        //
        checkUser();
        initListener();
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

    private void initListener() {
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