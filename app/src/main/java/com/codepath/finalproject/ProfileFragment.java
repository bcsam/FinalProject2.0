package com.codepath.finalproject;

import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
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
import java.io.IOException;
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

<<<<<<< HEAD
        long contactIdLong = Long.parseLong(user.getContactId()); // TODO: 7/23/17 throws error if contact Id is "" 
=======
        if (!user.getName().equals("Me")) {
            long contactIdLong = Long.parseLong(user.getContactId());
>>>>>>> 9341c1a9cd2dc406b626133462779cb1118821a5

            Bitmap image = BitmapFactory.decodeStream(openDisplayPhoto(contactIdLong));

            if (image != null) {
                ivProfileImage.setImageBitmap(null);
                ivProfileImage.setImageBitmap(Bitmap.createScaledBitmap(image, 150, 150, false));
            } else {
                ivProfileImage.setImageResource(R.drawable.ic_person_gray);
            }
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

    public InputStream openDisplayPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    getActivity().getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}