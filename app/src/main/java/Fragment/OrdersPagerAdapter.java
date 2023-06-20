package Fragment;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.common.hash.HashingOutputStream;

public class OrdersPagerAdapter extends FragmentStateAdapter {

    public OrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new PendingFragment();
            case 1:
                return new ConfirmedFragment();
            case 2:
                return new ShippingFragment();
            case 3:
                return new CompletedFragment();
            case 4:
                return new DeclinedFragment();
            default:
                return new CanceledFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}