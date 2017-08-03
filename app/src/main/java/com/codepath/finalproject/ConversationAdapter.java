package com.codepath.finalproject;

import android.app.Activity;
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
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
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

import static com.codepath.finalproject.R.id.ivProfileIcon;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    // List context
    Context context;
    // List values
    ArrayList<SMS> smsList;
    ArrayList<User> users;
    View rowView;
    Bitmap image;

    ArrayList<SMS> incomingList = new ArrayList<>();
    ArrayList<SMS> outgoingList = new ArrayList<>();
    int lastPosition = 2147483647;
    String id;

    public ConversationAdapter(Context mContext, ArrayList<SMS> mSmsList, ArrayList<SMS> incomingList, ArrayList<SMS> outgoingList, ArrayList<User> users) {

        context = mContext;
        smsList = mSmsList;
        for(SMS s: smsList)
            Log.i("messages", s.getBubbleColor());
        this.incomingList = incomingList;
        this.outgoingList = outgoingList;
        this.users = users;
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
        SMS[] params = new SMS[1];
        params[0] = smsList.get(position);
        drawable.setColorFilter(Color.parseColor(params[0].getBubbleColor()), PorterDuff.Mode.SRC_ATOP);
        setAnimation(holder.itemView, position);
        if(params[0].getBubbleColor().equals("#DFAD8E")) {
            AnalyzerClient analyzerClient = new AnalyzerClient(context, drawable);
            analyzerClient.execute(params);
        }
        String body = smsList.get(position).getBody();
        String date = millisToDate(Long.parseLong(smsList.get(position).getDate()));

        holder.tvBody.setText(body);
        holder.date.setText(date);

        holder.textCircle.setVisibility(View.INVISIBLE);
        holder.ivProfileCircle.setVisibility(View.VISIBLE);
        holder.ivProfileImage.setVisibility(View.INVISIBLE);

        final String id = smsList.get(position).getContactId();
        final String name = smsList.get(position).getContact();


        if (id != null && !id.equals("") && smsList.get(position).getType() == 1) {
            long contactIdLong = Long.parseLong(id);
            image = BitmapFactory.decodeStream(openPhoto(contactIdLong));

            if (image != null) {
                holder.ivProfileCircle.setVisibility(View.INVISIBLE);
                holder.ivProfileImage.setVisibility(View.VISIBLE);
                holder.ivProfileImage.setImageBitmap(null);
                //holder.ivProfileImage.setImageBitmap(Bitmap.createScaledBitmap(image, 45, 45, false));
                holder.ivProfileImage.setImageBitmap(getCroppedBitmap(Bitmap.createScaledBitmap(image, 100, 100, false)));
                image = null;
            } else if (!smsList.get(position).getContact().equals("")) {
                holder.textCircle.setVisibility(View.VISIBLE);
                holder.ivProfileImage.setVisibility(View.INVISIBLE);
                holder.ivProfileCircle.setVisibility(View.INVISIBLE);
                holder.textCircle.setText("" + name.charAt(0));
            }
        }

    }

    private void setAnimation(View viewToAnimate, int position){

        if(position<lastPosition) {

            if (getItemViewType(position) == 0) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.right_left_slide);
                viewToAnimate.startAnimation(animation);
                //lastPosition = position;
            } else {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.left_right_slide);
                viewToAnimate.startAnimation(animation);
                //lastPosition = position;
            }
            lastPosition = position;
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

    public ArrayList<SMS> getModifyList() {
        return smsList;
    }

    public void setUserList(ArrayList<User> newUsers){ users = newUsers; }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTime;
        public TextView date;
        public ImageView ivProfileImage;
        public ImageView ivProfileCircle;
        public TextView textCircle;

        public ViewHolder(View itemView){
            super(itemView);
           final View iv = itemView;
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvBody.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    context = iv.getContext();
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, MessageDetailActivity.class);
                    intent.putParcelableArrayListExtra("incomingList", incomingList);
                    intent.putExtra("users", users);
                    intent.putParcelableArrayListExtra("outgoingList", outgoingList);
                    intent.putExtra("sms", smsList.get(position));

                    String transitionName = context.getString(R.string.messageDetailTransition);
                    ActivityOptionsCompat transition = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, tvBody, transitionName);
                    context.startActivity(intent, transition.toBundle());
                }
            });
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            date = (TextView) rowView.findViewById(R.id.tvTimeStamp);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("from", "messaging");
                    int position = getAdapterPosition();
                    User user = new User(context);
                    if(smsList.get(position).getType() == 2){
                        user.setName("Me");
                        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
                        if (!mPhoneNumber.equals("")) {
                            user.setNumber("+" + mPhoneNumber); //this is why the + shows up
                        }
                    }
                    else {
                        user.setName(smsList.get(position).getContact());
                        user.setNumber(smsList.get(position).getNumber());
                        user.setContactId(smsList.get(position).getContactId());
                    }
                    intent.putExtra("user", user);

                    String transitionName = context.getString(R.string.profileTransition);
                    ActivityOptionsCompat transition = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ivProfileImage, transitionName);
                    context.startActivity(intent, transition.toBundle());
                }
            });
            ivProfileCircle = (ImageView) itemView.findViewById(ivProfileIcon);
            ivProfileCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("from", "messaging");
                    int position = getAdapterPosition();
                    User user = new User(context);
                    if(smsList.get(position).getType() == 2){
                        user.setName("Me");
                        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                        String mPhoneNumber = tMgr.getLine1Number(); // TODO: 7/14/17 this line does not set mPhoneNumber
                        if (!mPhoneNumber.equals("")) {
                            user.setNumber("+" + mPhoneNumber); //this is why the + shows up
                        }
                        intent.putExtra("user", user);
                        intent.putExtra("users", users);
                    }
                    else {
                        for(User u: users){
                            if(u.getNumber().equals(smsList.get(position).getNumber()))
                                position = users.indexOf(u);
                        }
                        intent.putExtra("users", users);
                        intent.putExtra("position", position);
                    }
                    intent.putExtra("incomingList", incomingList);
                    intent.putExtra("outgoingList", outgoingList);
                    String transitionName = context.getString(R.string.profileTransition);
                    ActivityOptionsCompat transition = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ivProfileCircle, transitionName);
                    ((Activity) context).startActivityForResult(intent, 0, transition.toBundle());
                }
            });
            textCircle = (TextView) itemView.findViewById(R.id.circleText);
        }

    }
}
