package com.codepath.finalproject;

import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
        final ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfile);
        final ImageView ivProfileImageIcon = (ImageView) v.findViewById(R.id.ivProfileIcon);
        TextView textCircle = (TextView) v.findViewById(R.id.circleText);

        ivProfileImageIcon.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onPreDraw() {
                ivProfileImageIcon.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().startPostponedEnterTransition();
                return true;
            }
        });


        User user = getArguments().getParcelable("user");

        if (!user.getName().equals("Me") && !user.getName().equals("")) {
            long contactIdLong = Long.parseLong(user.getContactId());

            Bitmap image = BitmapFactory.decodeStream(openDisplayPhoto(contactIdLong));

            ivProfileImageIcon.setVisibility(View.INVISIBLE);
            ivProfileImage.setVisibility(View.INVISIBLE);
            textCircle.setVisibility(View.INVISIBLE);

            if (image != null) {
                ivProfileImage.setVisibility(View.VISIBLE);
                //ivProfileImageIcon.setVisibility(View.INVISIBLE);
                ivProfileImage.setImageBitmap(null);
                ivProfileImage.setImageBitmap(getCroppedBitmap(Bitmap.createScaledBitmap(image, 200, 200, false)));
            } else
            if (user.getName() != null && !user.getName().equals("")){
                ivProfileImage.setImageResource(R.drawable.ic_person_gray);
                textCircle.setVisibility(View.VISIBLE);
                //ivProfileImage.setVisibility(View.INVISIBLE);
                textCircle.setText("" + user.getName().charAt(0));
            } else {
                ivProfileImage.setVisibility(View.VISIBLE);
            }
        }

        getActivity().setTitle(user.toStringNumber());

        if (user.getName() != null && !user.getName().equals("")) {
            tvName.setText(user.getName());
            getActivity().setTitle(user.getName());
            //getActivity().setTitle(user.toStringNumber());
        }
        else {
            tvName.setVisibility(View.GONE);
            getActivity().setTitle(user.toStringNumber());
        }

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}