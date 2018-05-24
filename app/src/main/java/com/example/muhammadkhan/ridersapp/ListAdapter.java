package com.example.muhammadkhan.ridersapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Muhammad Khan on 13/02/2018.
 */

public class ListAdapter extends ArrayAdapter {
    String[] titles;
    Integer [] icons;
    Context context;
    private LayoutInflater inflater;

    public ListAdapter(Context context, String[] items,Integer [] icons) {
        super(context, R.layout.drawer_items, items);

        this.context = context;
        this.titles = items;
        this.icons=icons;
    }

    public View getView(int position, View view, ViewGroup parent) {
            inflater = LayoutInflater.from(context);
            View rowView = inflater.inflate(R.layout.drawer_items, null, false);
            //this code gets references to objects in the listview_row.xml file
            TextView nameTextField = (TextView) rowView.findViewById(R.id.title);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

            ////this code sets the values of the objects to values from the arrays
            nameTextField.setText(titles[position]);
            imageView.setImageResource(icons[position]);
            return rowView;
    }
}
