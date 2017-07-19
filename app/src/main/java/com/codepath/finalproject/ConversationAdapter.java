package com.codepath.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>{

    // List context
    Context context;
    AnalyzerClient client;
    // List values
    List<SMS> smsList;
    ArrayList<TextBody> textBodyArray;
    View rowView;

    public ConversationAdapter(Context mContext, List<SMS> mSmsList) {
        context = mContext;
        smsList = mSmsList;
        TextBody temp = new TextBody();
        textBodyArray = new ArrayList<TextBody>();
        for(int i = 0; i < getItemCount(); i++)
            textBodyArray.add(temp);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new AnalyzerClient();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType == 0)
            rowView = inflater.inflate(R.layout.item_outgoing_messaging_text, parent, false);
        else
            rowView = inflater.inflate(R.layout.item_incoming_messaging_text, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String name = smsList.get(position).getContact();
        final String number = smsList.get(position).getNumber();
        String body = smsList.get(position).getBody();
        String date = millisToDate(Long.parseLong(smsList.get(position).getDate()));
        TextBody textBody = new TextBody();
        textBody.setMessage(body);
        //client.getScores(textBody);
        holder.tvBody.setText(body);
        holder.tvBody.getBackground().setColorFilter(Color.parseColor(textBody.getBubbleColor()), PorterDuff.Mode.SRC_ATOP);
        holder.date.setText(date);
        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
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
        Log.i("position", String.valueOf(position));
        textBodyArray.set(position, textBody);
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String myNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
        Log.i("myNumber", myNumber);
        Log.i("getNumber", smsList.get(position).getNumber());
        if(smsList.get(position).getNumber().equals(myNumber)){
            return 0;
        }
        return 1;
    }

    public static String millisToDate(long currentTime) {
        String finalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar smsTime = Calendar.getInstance();

        String monthString;

        switch (month) {
            case 0:  monthString = "January";
                break;
            case 1:  monthString = "February";
                break;
            case 2:  monthString = "March";
                break;
            case 3:  monthString = "April";
                break;
            case 4:  monthString = "May";
                break;
            case 5:  monthString = "June";
                break;
            case 6:  monthString = "July";
                break;
            case 7:  monthString = "August";
                break;
            case 8:  monthString = "September";
                break;
            case 9: monthString = "October";
                break;
            case 10: monthString = "November";
                break;
            case 11: monthString = "December";
                break;
            default: monthString = "Invalid month";
                break;
        }

        String dateMonth = monthString + " " + day;

        if (calendar.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            int AMPM = calendar.get(Calendar.AM_PM);
            String curTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));

            if (AMPM == 0) {
                return curTime + " AM";
            }
            else {
                return curTime + " PM";
            }
        } else {
            return dateMonth;
        }
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

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context = itemView.getContext();
            int position = getAdapterPosition();
            Intent intent = new Intent(context, MessageDetailActivity.class);
            intent.putExtra("textBody", textBodyArray.get(position));
            intent.putExtra("sms", smsList.get(position));
            context.startActivity(intent);
        }
    }
}
