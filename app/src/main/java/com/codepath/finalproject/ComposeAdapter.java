package com.codepath.finalproject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bcsam on 7/21/17.
 */

class ComposeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int STRING = 0;
    private final int USER = 1;
    Context context;
    List<Object> contactList;
    ArrayList<SMS> incomingList = new ArrayList<>();
    ArrayList<SMS> outgoingList = new ArrayList<>();
    String message;

    Bitmap image;
    long contactIdLong;
    String number;
    String id;

    MainActivity.DataTransfer dtTransfer;


    public ComposeAdapter(Context mContext, ArrayList<Object> mContactList, ArrayList<SMS> mIncomingList, ArrayList<SMS> mOutgoingList, MainActivity.DataTransfer dtTransfer) {
        this.dtTransfer = dtTransfer;
        context = mContext;
        contactList = mContactList;
        incomingList = mIncomingList;
        outgoingList = mOutgoingList;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (contactList.get(position) instanceof String) {
            return STRING;
        } else if (contactList.get(position) instanceof User) {
            return USER;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case STRING:
                View v1 = inflater.inflate(R.layout.item_custom_number, parent, false);
                viewHolder = new NumberViewHolder(v1);
                break;

            case USER:
                View v2 = inflater.inflate(R.layout.item_contact, parent, false);
                viewHolder = new ContactsViewHolder(v2);
                break;

             default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new ContactsViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case STRING:
                NumberViewHolder vh1 = (NumberViewHolder) viewHolder;
                configureNumberViewHolder(vh1, position);
                break;
            case USER:
                ContactsViewHolder vh2 = (ContactsViewHolder) viewHolder;
                configureContactsViewHolder(vh2, position);
                break;
        }
    }

    public void configureNumberViewHolder(NumberViewHolder holder, int position) {
        String number = (String) contactList.get(position);
        holder.tvCustomNumber.setText(number);
    }

    public void configureContactsViewHolder(ContactsViewHolder holder, int position) {
        // TODO: 7/21/17 add in contact pictures
        User contact = (User) contactList.get(position);

        //contact numbers are in the form +1 555-555-5555
        number = contact.getNumber();
        /*if(number.length() > 7)
            number = number.substring(0,2) + " (" + number.substring(3,6) + ") " + number.substring(7);*/


        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        number = "";

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }


        holder.tvContactNumber.setText(number);
        holder.tvContactName.setText(contact.getName());

        holder.profileImage.setImageResource(R.drawable.ic_person_gray);


        if (!id.equals("")) {
            contactIdLong = Long.parseLong(id);
            id = "";
            image = BitmapFactory.decodeStream(openPhoto(contactIdLong));
            int count;
            ArrayList<Long> idList = new ArrayList<Long>();

            if (image != null) {
                holder.profileImageIcon.setVisibility(View.INVISIBLE);
                holder.profileImage.setVisibility(View.VISIBLE);
                holder.profileImage.setImageBitmap(null);
                holder.profileImage.setImageBitmap(getCroppedBitmap(Bitmap.createScaledBitmap(image, 45, 45, false)));
                idList.add(contactIdLong);
            } else {
                count = 0;
                for (int i = 0; i < idList.size(); i++) {
                    if (contactIdLong != idList.get(i)) {
                        count ++;
                    }
                }

                if (count == idList.size()) {
                    holder.textCircle.setVisibility(View.VISIBLE);
                    holder.profileImage.setVisibility(View.INVISIBLE);
                    holder.textCircle.setText("" + contact.getName().charAt(0));
                }
            }
            image = null;
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

    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);

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

    public class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvContactName;
        TextView tvContactNumber;
        ImageView profileImage;
        EditText etBody;
        ImageView profileImageIcon;
        TextView textCircle;

        public ContactsViewHolder(View itemView) {
            super(itemView);

            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvContactNumber = (TextView) itemView.findViewById(R.id.tvContactNumber);
            profileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            profileImageIcon = (ImageView) itemView.findViewById(R.id.ivProfileIcon);
            textCircle = (TextView) itemView.findViewById(R.id.circleText);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }


        @Override
        public void onClick(View v) {
            //EditText etNumber = (EditText) itemView.findViewById(R.id.etNumber);
            //etNumber.setText(tvContactNumber.getText().toString());

            int position = getAdapterPosition();
            User user = (User) contactList.get(position);
            String name = user.getName();
            String number = user.getNumber();
            number = number.replaceAll("-", "");
            number = number.replaceAll(" ", "");
            ArrayList<SMS> messages = new ArrayList<>();


            for (SMS s : incomingList) {
                if (s.getNumber().equals(number))
                    messages.add(s);
            }
            for (SMS s : outgoingList) {
                if (s.getNumber().equals(number)) {
                    int index = 0;
                    Log.i("MessagingActivity body", s.getBody());
                    for (SMS m : messages) {
                        if (Double.parseDouble(m.getDate()) < Double.parseDouble(s.getDate())) {
                            Log.i("MessagingActivity index", String.valueOf(index));
                            index = messages.indexOf(m);
                            break;
                        }
                    }
                    messages.add(index, s);
                }
            }

            dtTransfer.setValues(messages, name, number);

        }
    }


    public class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCustomNumber;

        public NumberViewHolder(View itemView) {
            super(itemView);
            tvCustomNumber = (TextView) itemView.findViewById(R.id.tvCustomNumber);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            //int position = getAdapterPosition();
            //Intent intent = new Intent(context, MessagingActivity.class);
            //String name = contactList.get(position).getName();
            //String number = contactList.get(position).getNumber();
            String number = tvCustomNumber.getText().toString();
            number = number.replaceAll("-", "");
            number = number.replaceAll(" ", "");
            ArrayList<SMS> messages = new ArrayList<>();


            for (SMS s : incomingList) {
                if (s.getNumber().equals(number))
                    messages.add(s);
            }
            for (SMS s : outgoingList) {
                if (s.getNumber().equals(number)) {
                    int index = 0;
                    Log.i("MessagingActivity body", s.getBody());
                    for (SMS m : messages) {
                        if (Double.parseDouble(m.getDate()) < Double.parseDouble(s.getDate())) {
                            Log.i("MessagingActivity index", String.valueOf(index));
                            index = messages.indexOf(m);
                            break;
                        }
                    }
                    messages.add(index, s);
                }
            }

            dtTransfer.setValues(messages, number, number);
        }
    }
}