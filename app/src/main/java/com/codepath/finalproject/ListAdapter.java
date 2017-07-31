package com.codepath.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.StrictMode;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    // List context
    Context context;
    AnalyzerClient client;
    // List values
    ArrayList<SMS> smsList;
    ArrayList<SMS> incomingList;
    ArrayList<SMS> outgoingList;
    View rowView;
    Bitmap image;

    public ListAdapter(Context mContext, ArrayList<SMS> mSmsList, ArrayList<SMS> mIncomingList, ArrayList<SMS> mOutgoingList) {
        context = mContext;
        smsList = mSmsList;
        incomingList = mIncomingList;
        outgoingList = mOutgoingList;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        client = new AnalyzerClient();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        rowView = inflater.inflate(R.layout.item_incoming_text, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //final ViewHolder holder1 = holder;
        SMS sms = smsList.get(position);
        final String name = smsList.get(position).getContact();
        final String number = smsList.get(position).getNumber();
        String contactId = smsList.get(position).getContactId();

        if (contactId == null){
            contactId = "";
        }


        String body = smsList.get(position).getBody();
        String date = millisToDate(Long.parseLong(smsList.get(position).getDate()));



        if (!contactId.equals("")) {
            long contactIdLong = Long.parseLong(contactId);
            image = BitmapFactory.decodeStream(smsList.get(position).openPhoto(contactIdLong));

            if (image != null) {
                holder.profileCircle.setVisibility(View.INVISIBLE);
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

        if(!name.equals(""))
            holder.tvUserName.setText(name);
        else
            holder.tvUserName.setText(number);
        holder.tvBody.setText(body);
        holder.date.setText(date);

        holder.tvBody.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                holder.tvTime.setVisibility(View.VISIBLE);
                return true;
            }
        });
        //setAnimation(holder.itemView, getItemViewType(position));
    }

    private void setAnimation(View viewToAnimate, int viewType){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.bottom_top_slide);
            viewToAnimate.startAnimation(animation);
    }

    public int getItemCount() {
        return smsList.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTime;
        public TextView date;
        public ImageView ivProfileImage;
        public ImageView ivProfileIcon;
        public TextView textCircle;
        public ImageView profileCircle;

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
                    int position = getAdapterPosition();
                    User user = new User(context);
                    user.setName(smsList.get(position).getContact());
                    user.setNumber(smsList.get(position).getNumber());
                    user.setContactId(smsList.get(position).getContactId());
                    intent.putExtra("user", user);

                    String transitionName = context.getString(R.string.profileTransition);
                    ActivityOptionsCompat transition = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ivProfileImage, transitionName);
                    context.startActivity(intent, transition.toBundle());
                }
            });
            ivProfileIcon = (ImageView) itemView.findViewById(R.id.ivProfileIcon);
            ivProfileIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    int position = getAdapterPosition();
                    User user = new User(context);
                    user.setName(smsList.get(position).getContact());
                    user.setNumber(smsList.get(position).getNumber());
                    user.setContactId(smsList.get(position).getContactId());
                    intent.putExtra("user", user);

                    String p1TransitionName = context.getString(R.string.profileTransition);
                    String p2TransitionName = context.getString(R.string.nameTransition);
                    Pair<View, String> p1 = Pair.create((View) ivProfileIcon, p1TransitionName);
                    Pair<View, String> p2 = Pair.create((View) ivProfileIcon, p2TransitionName);;
                    ActivityOptionsCompat transition = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2);
                    context.startActivity(intent, transition.toBundle());
                }
            });
            textCircle = (TextView)  itemView.findViewById(R.id.circleText);
            profileCircle = (ImageView) itemView.findViewById(R.id.ivProfileIcon);

            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String name = smsList.get(position).getContact();
            String number = smsList.get(position).getNumber();
            String id = smsList.get(position).getContactId();
            notifyDataSetChanged();
            Intent intent = new Intent(context, MessagingActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("number", number);
            //intent.putExtra("id", id);
            intent.putParcelableArrayListExtra("incomingList", incomingList);
            intent.putParcelableArrayListExtra("outgoingList", outgoingList);
            ((Activity) context).startActivityForResult(intent, 1);
        }

        public void onClickImage(View view){

        }
    }
}
