package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bcsam on 7/21/17.
 */

class ComposeAdapter extends RecyclerView.Adapter<ComposeAdapter.ViewHolder>{
    Context context;
    List<User> contactList;
    ArrayList<SMS> incomingList = new ArrayList<>();
    ArrayList<SMS> outgoingList = new ArrayList<>();
    String message;

    public ComposeAdapter(Context mContext, List<User> mContactList, ArrayList<SMS> mIncomingList, ArrayList<SMS> mOutgoingList) {
        context = mContext;
        contactList = mContactList;
        incomingList = mIncomingList;
        outgoingList = mOutgoingList;
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

        //contact numbers are in the form +1 555-555-5555
        String number = contact.getNumber();
        /*if(number.length() > 7)
            number = number.substring(0,2) + " (" + number.substring(3,6) + ") " + number.substring(7);*/

        holder.tvContactNumber.setText(number);
        holder.tvContactName.setText(contact.getName());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvContactName;
        TextView tvContactNumber;
        EditText etBody;

        public ViewHolder(View itemView){
            super(itemView);

            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvContactNumber = (TextView) itemView.findViewById(R.id.tvContactNumber);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }


        @Override
        public void onClick(View v) {
            //EditText etNumber = (EditText) itemView.findViewById(R.id.etNumber);
            //etNumber.setText(tvContactNumber.getText().toString());

            int position = getAdapterPosition();
            Intent intent = new Intent(context, MessagingActivity.class);
            String name = contactList.get(position).getName();
            String number = contactList.get(position).getNumber();
            //String id = contactList.get(position).getContactId();
            intent.putExtra("name", name);
            intent.putExtra("number", number);
            //intent.putExtra("id", id);
            intent.putParcelableArrayListExtra("incomingList", incomingList);
            intent.putParcelableArrayListExtra("outgoingList", outgoingList);
            context.startActivity(intent);
        }
    }
}