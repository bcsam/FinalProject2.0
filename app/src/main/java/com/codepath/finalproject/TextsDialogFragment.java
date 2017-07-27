package com.codepath.finalproject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
// ...

public class TextsDialogFragment extends DialogFragment {


    public TextsDialogFragment() {
    }

    public static TextsDialogFragment newInstance(User user, int tone) {
        TextsDialogFragment frag = new TextsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        args.putInt("tone", tone);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_texts_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Uri uri = Uri.parse("content://sms/sent");
        Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
        ((Activity) getContext()).startManagingCursor(c);
        ArrayList<SMS> smsList = new ArrayList<SMS>();
        // Read the sms data and store it in the listco
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String text = c.getString(c.getColumnIndexOrThrow("body")).toString();
                String number = c.getString(c.getColumnIndexOrThrow("address")).toString();
                String name = getContactName(number, getContext());
                SMS sms = new SMS();
                sms.setBody(text);
                sms.setNumber(number);
                sms.setContact(name);
                smsList.add(sms);
                c.moveToNext();
            }
        }
        c.close();
    }

    public String getContactName(final String phoneNumber,Context context)

    {

        if (phoneNumber == null || phoneNumber.equals("")) {
            return "Anonymous";
        }

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);

        if(cursor.moveToFirst())
        {

            contactName=cursor.getString(0);

        }
        cursor.close();

        return contactName;
    }
}