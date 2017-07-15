package com.codepath.finalproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    // List context
    private Context context;
    // List values
    private List<SMS> smsList;
    View rowView;

    public ListAdapter(Context context, List<SMS> smsList) {
        Log.i("Constructor", ""+smsList.size());
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("onCreateViewHolder", "in method");
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        rowView = inflater.inflate(R.layout.item_incoming_text, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("OnBindViewHoler", "in method");
        if (!smsList.get(position).getContact().equals("")) {
            holder.tvUserName.setText(smsList.get(position).getContact());
        }
        else {
            holder.tvUserName.setText(smsList.get(position).getNumber());
        }
        holder.tvBody.setText(smsList.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTime;

        public ViewHolder(View itemView){
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeStamp);
        }
    }
}
