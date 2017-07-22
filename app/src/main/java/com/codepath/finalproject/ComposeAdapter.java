package com.codepath.finalproject;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bcsam on 7/21/17.
 */

class ComposeAdapter extends RecyclerView.Adapter<ComposeAdapter.ViewHolder>{
    Context context;
    List<User> contactList;

    public ComposeAdapter( List<User> mContactList) {
        contactList = mContactList;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.item_contact, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ComposeAdapter.ViewHolder holder, int position) {
        // TODO: 7/21/17 add in contact pictures
        User contact = contactList.get(position);

        holder.tvContactNumber.setText(contact.getNumber());
        holder.tvContactName.setText(contact.getName());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvContactName;
        TextView tvContactNumber;

        public ViewHolder(View itemView){
            super(itemView);

            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvContactNumber = (TextView) itemView.findViewById(R.id.tvContactNumber);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            EditText etNumber = (EditText) itemView.findViewById(R.id.etNumber);
            etNumber.setText(tvContactNumber.getText().toString());
        }
    }
}