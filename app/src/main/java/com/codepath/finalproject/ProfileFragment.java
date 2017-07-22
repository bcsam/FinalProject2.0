package com.codepath.finalproject;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by bcsam on 7/12/17.
 */

public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        TextView tvNumber = (TextView) v.findViewById(R.id.tvNumber);
        ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfile);


        User user = getArguments().getParcelable("user");

        long contactIdLong = Long.parseLong(user.getContactId());

        Bitmap image = BitmapFactory.decodeStream(openPhoto(contactIdLong));

        if (image != null) {
            ivProfileImage.setImageBitmap(null);
            ivProfileImage.setImageBitmap(Bitmap.createScaledBitmap(image, 45, 45, false));
        } else {
            ivProfileImage.setImageResource(R.drawable.ic_person_white);
        }

        tvName.setText(user.getName());
        tvNumber.setText(user.toStringNumber());
        return v;
    }

    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getActivity().getContentResolver().query(photoUri,
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}