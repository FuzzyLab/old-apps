package com.fuzzylabs.medicinetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuzzylabs.medicinetracker.TwoTextArrayAdapter.RowType;

public class ListItem implements Item {
    private final int btnResource;
    private final String viewText;

    public ListItem(int btnResource, String viewText) {
        this.btnResource = btnResource;
        this.viewText = viewText;
    }

    @Override
    public int getViewType() {
        return RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.navigation_item, null);
        } else {
            view = convertView;
        }

        ImageView img = (ImageView) view.findViewById(R.id.navigationIcon);
        TextView text = (TextView) view.findViewById(R.id.navigationText);
        img.setBackgroundResource(btnResource);
        text.setText(viewText);

        return view;
    }

}
