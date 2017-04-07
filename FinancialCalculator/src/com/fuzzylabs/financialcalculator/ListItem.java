package com.fuzzylabs.financialcalculator;

import com.fuzzylabs.financialcalculator.TwoTextArrayAdapter.RowType;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItem implements Item {
    private final int btnResource;
    private final String titleText;
    private final String tagText;
    

    public ListItem(int btnResource, String titleText, String tagText ) {
        this.btnResource = btnResource;
        this.titleText = titleText;
        this.tagText = tagText;
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
        TextView title = (TextView) view.findViewById(R.id.navigationTitle);
        TextView tag = (TextView) view.findViewById(R.id.navigationTag);
        img.setBackgroundResource(btnResource);
        title.setText(titleText);
        tag.setText(tagText);

        return view;
    }

}
