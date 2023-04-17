package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fashop.R;
import com.example.fashop.activity.AboutUsActivity;
import com.example.fashop.activity.HelpCenterActivity;
import com.example.fashop.activity.LegalInformationActivity;
import com.example.fashop.activity.PrivacyPolicyActivity;
import com.example.fashop.activity.QuestionsActivity;

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

    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    private void initUI(View view) {
        tvPrivacyPolicy = view.findViewById(R.id.tvPrivacyPolicy);
        tvQuestions = view.findViewById(R.id.tvQuestions);
        tvLegalInformation = view.findViewById(R.id.tvLegalInformation);
        tvAboutUs = view.findViewById(R.id.tvAboutUs);

        initListener();
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