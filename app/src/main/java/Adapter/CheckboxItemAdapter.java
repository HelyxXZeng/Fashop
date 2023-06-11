package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.fashop.R;

import java.util.Arrays;
import java.util.List;

import Model.ProductCategory;
import Model.ProductModel;

public class CheckboxItemAdapter extends ArrayAdapter<ProductCategory> {

    private List<ProductCategory> items;
    private int[] id;
    private boolean[] checked;

    public CheckboxItemAdapter(Context context, int resource, List<ProductCategory> items) {
        super(context, resource, items);
        this.items = items;
        checked = new boolean[items.size()];
        Arrays.fill(checked, true);
        id = new int[items.size()];
        for (int i = 0; i < items.size(); i++)
        {
            id[i] = items.get(i).getID();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.viewholder_checkbox_item, parent, false);
        }

        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        checkbox.setText(items.get(position).getName());
        boolean isChecked = checked[position];

        // set a tag on the checkbox to store the current position
        checkbox.setTag(position);

        // only update the checkbox state if it has changed from its previous state
        if (checkbox.isChecked() != isChecked) {
            checkbox.setChecked(isChecked);
        }

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (int) buttonView.getTag();
                checked[position] = isChecked;
                Log.v("position:", Integer.toString(position));
            }
        });

        return convertView;
    }

    public boolean[] getCheckedItems() {
        return checked;
    }
    public int[] getIDItems() { return id; }
}