package com.codepath.finalproject;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    // List context
    Context context;
    // List values
    ArrayList<SMS> smsList;
    View rowView;
    Bitmap image;

    ArrayList<SMS> incomingList = new ArrayList<>();
    ArrayList<SMS> outgoingList = new ArrayList<>();
    int lastPosition = 2147483647;
    String id;

    public ConversationAdapter(Context mContext, ArrayList<SMS> mSmsList, ArrayList<SMS> incomingList, List<SMS> outgoingList) {

        context = mContext;
        smsList = mSmsList;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 0)
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
        id = smsList.get(position).getContactId(); //might have to change -Brent
        if(id == null){
            id = "";
        }
        String body = smsList.get(position).getBody();
        String date = millisToDate(Long.parseLong(smsList.get(position).getDate()));
        params[0] = smsList.get(position);
        if(params[0].getBubbleColor().equals(""))
            client.execute(params);
        else
            drawable.setColorFilter(Color.parseColor(params[0].getBubbleColor()), PorterDuff.Mode.SRC_ATOP);
        holder.tvBody.setText(body);
        holder.date.setText(date);

        holder.textCircle.setVisibility(View.INVISIBLE);
        holder.ivProfileCircle.setVisibility(View.VISIBLE);
        holder.ivProfileImage.setVisibility(View.INVISIBLE);


        if (!id.equals("") && smsList.get(position).getType() == 1) {
            long contactIdLong = Long.parseLong(id);
            image = BitmapFactory.decodeStream(openPhoto(contactIdLong));

            if (image != null) {
                holder.ivProfileCircle.setVisibility(View.INVISIBLE);
                holder.ivProfileImage.setVisibility(View.VISIBLE);
                holder.ivProfileImage.setImageBitmap(null);
                //holder.ivProfileImage.setImageBitmap(Bitmap.createScaledBitmap(image, 45, 45, false));
                holder.ivProfileImage.setImageBitmap(getCroppedBitmap(Bitmap.createScaledBitmap(image, 45, 45, false)));
                image = null;
            } else if (!name.equals("")) {
                holder.textCircle.setVisibility(View.VISIBLE);
                holder.ivProfileImage.setVisibility(View.INVISIBLE);
                holder.textCircle.setText("" + name.charAt(0));
            }
        }

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

        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position){
        if(getItemViewType(position)==0) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.right_left_slide);
            viewToAnimate.startAnimation(animation);
            //lastPosition = position;
        }else{
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.left_right_slide);
            viewToAnimate.startAnimation(animation);
            //lastPosition = position;
        }
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

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
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

        String dateMonth = monthString.substring(0, 3) + " " + day;

        if (calendar.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            int AMPM = calendar.get(Calendar.AM_PM);
            String curTime = String.format("%d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
            if (curTime.charAt(0) == '0') {
                curTime = String.format("%d:%02d", 12, calendar.get(Calendar.MINUTE));
            }

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
        public ImageView ivProfileCircle;
        public TextView textCircle;

        public ViewHolder(View itemView){
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            date = (TextView) rowView.findViewById(R.id.tvTimeStamp);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivProfileCircle = (ImageView) itemView.findViewById(R.id.ivProfileIcon);
            textCircle = (TextView) itemView.findViewById(R.id.circleText);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            context = itemView.getContext();
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.expand);
            itemView.setAnimation(animation);
            itemView.startAnimation(animation);
            int position = getAdapterPosition();
            Log.i("onClick", smsList.get(position).getBody());
            Intent intent = new Intent(context, MessageDetailActivity.class);
            intent.putParcelableArrayListExtra("incomingList", incomingList);
            intent.putParcelableArrayListExtra("outgoingList", outgoingList);
            intent.putExtra("sms", smsList.get(position));
            context.startActivity(intent);

        }
    }
}
