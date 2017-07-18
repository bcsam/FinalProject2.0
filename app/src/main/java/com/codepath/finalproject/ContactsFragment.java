package com.codepath.finalproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ContactsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

        @SuppressLint("InlinedApi")
        private final static String[] FROM_COLUMNS = {
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Contacts.DISPLAY_NAME
        };

        @SuppressLint("InlinedApi")
        private static final String[] PROJECTION =
                {
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.LOOKUP_KEY,
                        Build.VERSION.SDK_INT
                                >= Build.VERSION_CODES.HONEYCOMB ?
                                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                                ContactsContract.Contacts.DISPLAY_NAME

                };

        private static final int CONTACT_ID_INDEX = 0;
        private static final int LOOKUP_KEY_INDEX = 1;

        @SuppressLint("InlinedApi")
        private static final String SELECTION =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                        ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";

        private String mSearchString;
        private String[] mSelectionArgs = { mSearchString };

        private final static int[] TO_IDS = {
                android.R.id.text1
        };

        ListView mContactsList;

        long mContactId;

        String mContactKey;

        Uri mContactUri;

        private SimpleCursorAdapter mCursorAdapter;


        public ContactsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.contact_list_fragment,
                    container, false);
        }

        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mContactsList =
                    (ListView) getActivity().findViewById(R.layout.contacts_list_view);

            mCursorAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.contacts_list_item,
                    null,
                    FROM_COLUMNS, TO_IDS,
                    0);

            mContactsList.setAdapter(mCursorAdapter);

            mContactsList.setOnItemClickListener(this);

            getLoaderManager().initLoader(0, null, this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
            mSelectionArgs[0] = "%" + mSearchString + "%";
            return new CursorLoader(
                    getActivity(),
                    ContactsContract.Contacts.CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    mSelectionArgs,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCursorAdapter.swapCursor(null);
        }

    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position, long rowID) {

        Cursor cursor = mCursorAdapter.getCursor();

        cursor.moveToPosition(position);

        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
    }
}
