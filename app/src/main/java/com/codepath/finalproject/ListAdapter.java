package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    // List context
    Context context;
    // List values
    List<SMS> smsList;
    View rowView;
    String name;
    String number;
    String body;
    String date;

    public ListAdapter(Context mContext, List<SMS> mSmsList) {
        Log.i("Constructor", ""+mSmsList.size());
        context = mContext;
        smsList = mSmsList;
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

        name = smsList.get(position).getContact();
        number = smsList.get(position).getNumber();
        body = smsList.get(position).getBody();
        date = smsList.get(position).getDate();

        if (!name.equals("")) {
            holder.tvUserName.setText(name);
        }
        else {
            holder.tvUserName.setText(number);
        }
        holder.tvBody.setText(body);
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTime;
        public TextView date;
        public ImageView ivProfileImage;

        public ViewHolder(View itemView){
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            date = (TextView) rowView.findViewById(R.id.tvTimeStamp);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    User user = new User();
                    user.setName(name);
                    user.setNumber(number);
                    intent.putExtra("user", user);
                    context.startActivity(intent);
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context = itemView.getContext();
            int position = getAdapterPosition();
            String name = smsList.get(position).getContact();
            String number = smsList.get(position).getNumber();
            Intent intent = new Intent(context, MessagingActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("number", number);
            context.startActivity(intent);
        }
    }
}
