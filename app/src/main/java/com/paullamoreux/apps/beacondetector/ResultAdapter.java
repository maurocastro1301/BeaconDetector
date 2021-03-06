package com.paullamoreux.apps.beacondetector;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by paul on 2/26/15.
 */
public class ResultAdapter extends ArrayAdapter<ScanResult> {

    public ResultAdapter(Context context, List<ScanResult> results) {
        super(context, 0, results);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ScanResult result = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_result, parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvLarge);
        TextView tvHome = (TextView) convertView.findViewById(R.id.tvSmall);
        // Populate the data into the template view using the data object
        tvName.setText(result.getDevice().getAddress());
        tvHome.setText(result.getDevice().getName());
        // Return the completed view to render on screen

        return convertView;
    }
}
