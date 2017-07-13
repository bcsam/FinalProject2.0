package com.codepath.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class ListAdapter extends ArrayAdapter<SMS> {

    // List context
    private final Context context;
    // List values
    private final List<SMS> smsList;

    public ListAdapter(Context context, List<SMS> smsList) {
        super(context, R.layout.item_incoming_text, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_incoming_text, parent, false);

        TextView senderNumber = (TextView) rowView.findViewById(R.id.tvUserName);
        senderNumber.setText(smsList.get(position).getNumber());

        return rowView;
    }

}
