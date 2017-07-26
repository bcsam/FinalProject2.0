package com.codepath.finalproject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
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

class ComposeAdapter extends RecyclerView.Adapter<ComposeAdapter.ViewHolder>{
    Context context;
    List<User> contactList;
    ArrayList<SMS> incomingList = new ArrayList<>();
    ArrayList<SMS> outgoingList = new ArrayList<>();
    String message;
    MainActivity.DataTransfer dtTransfer;

    public ComposeAdapter(Context mContext, ArrayList<User> mContactList, ArrayList<SMS> mIncomingList, ArrayList<SMS> mOutgoingList, MainActivity.DataTransfer dtTransfer) {
        this.dtTransfer = dtTransfer;
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

        String id = "";

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if(cursor != null) {
            while(cursor.moveToNext()){
                id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }


        holder.tvContactNumber.setText(number);
        holder.tvContactName.setText(contact.getName());


        if (!id.equals("")) {
            //Toast.makeText(context, id, Toast.LENGTH_LONG).show();
            long contactIdLong = Long.parseLong(id);
            Bitmap image = BitmapFactory.decodeStream(openPhoto(contactIdLong));

            if (image != null) {
                holder.profileImage.setImageBitmap(null);
                holder.profileImage.setImageBitmap(getCroppedBitmap(Bitmap.createScaledBitmap(image, 45, 45, false)));
            } else if (!contact.getName().equals("")) {
                //holder.textCircle.setVisibility(View.VISIBLE);
                holder.profileImage.setVisibility(View.INVISIBLE);
                //holder.textCircle.setText("" + name.charAt(0));
            }
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

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvContactName;
        TextView tvContactNumber;
        ImageView profileImage;
        EditText etBody;

        public ViewHolder(View itemView){
            super(itemView);

            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvContactNumber = (TextView) itemView.findViewById(R.id.tvContactNumber);
            profileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
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
            number = number.replaceAll("-", "");
            number = number.replaceAll(" ", "");
            ArrayList<SMS> messages = new ArrayList<>();


            for(SMS s: incomingList){
                if(s.getNumber().equals(number))
                    messages.add(s);
            }
            for(SMS s: outgoingList){
                if(s.getNumber().equals(number)) {
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
}