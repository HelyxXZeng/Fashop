package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.fashop.R;

import java.util.Arrays;

public class CheckboxItemAdapter extends ArrayAdapter<String> {

    private String[] items;
    private boolean[] checked;

    public CheckboxItemAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.items = items;
        checked = new boolean[items.length];
        Arrays.fill(checked, false);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.viewholder_checkbox_item, parent, false);
        }

        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        checkbox.setText(items[position]);
        checkbox.setChecked(checked[position]);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked[position] = isChecked;
            }
        });

        return convertView;
    }

    public boolean[] getCheckedItems() {
        return checked;
    }
}

