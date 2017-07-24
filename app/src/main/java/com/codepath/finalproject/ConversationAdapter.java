package com.codepath.finalproject;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>{

    // List context
    Context context;
    // List values
    List<SMS> smsList;
    View rowView;

    public ConversationAdapter(Context mContext, List<SMS> mSmsList) {
        context = mContext;
        smsList = mSmsList;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        Drawable drawable = holder.tvBody.getBackground();
        AnalyzerClient client = new AnalyzerClient(context, drawable);
        SMS[] params = new SMS[1];
        final String name = smsList.get(position).getContact();
        final String number = smsList.get(position).getNumber();
        final String id = smsList.get(position).getContactId();
        String body = smsList.get(position).getBody();
        String date = millisToDate(Long.parseLong(smsList.get(position).getDate()));
        params[0] = smsList.get(position);
        //client.execute(params);
        holder.tvBody.setText(body);
        holder.date.setText(date);

        /*long contactIdLong = Long.parseLong(id);
=======
        if (!name.equals("Me")) {
            long contactIdLong = Long.parseLong(id);
>>>>>>> 6d4446d331e3af863753fb2c4fb1de7eb9286e31

        Bitmap image = null;
        //image = BitmapFactory.decodeStream(smsList.get(position).openPhoto(contactIdLong));

<<<<<<< HEAD
        if (image != null) {
            holder.ivProfileImage.setImageBitmap(null);
            holder.ivProfileImage.setImageBitmap(Bitmap.createScaledBitmap(image, 45, 45, false));
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.ic_person_white);
        }*/

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                User user = new User(context);
                user.setName(name);
                user.setNumber(number);
                user.setContactId(id);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });
        Log.i("position", String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (smsList.get(position).getType() == 2) {
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
            String curTime = String.format("%d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));

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

    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
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
            intent.putExtra("sms", smsList.get(position));
            context.startActivity(intent);
        }
    }
}
