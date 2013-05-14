package com.oreilly.demo.android.pa.finchvideo;

import android.app.Activity;
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.finchframework.finch.Finch;
import com.oreilly.demo.android.pa.finchvideo.provider.FinchVideo;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Slightly more sophisticated FinchVideo search application that allows a user
 * to type a search query and see network results update as they are received
 * from RESTful web services like gdata.youtube.com.  The results appear one by
 * one in the graphical list display as they are parsed from network data.
 */
public class FinchVideoActivity extends Activity {
    SimpleCursorAdapter mAdapter;

    private EditText mSearchText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_query_activity);
        Resources r = getResources();

        final ListView searchList = (ListView) findViewById(R.id.video_list);
        Cursor videoCursor =
                managedQuery(FinchVideo.Videos.CONTENT_URI, null,
                        null, null, null);

        // have to reset this on a new search

        // Maps video entries from the database to views
        mAdapter = new SimpleCursorAdapter(this,
                R.layout.video_list_item,
                videoCursor,
                new String[] {
                        FinchVideo.Videos.TITLE,
                        FinchVideo.Videos.THUMB_URI_NAME
                },
                new int[] { R.id.video_text, R.id.video_thumb_icon });

        SimpleCursorAdapter.ViewBinder savb =
                new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int i) {
                        switch (i) {
                            case FinchVideo.TITLE_COLUMN:
                                TextView tv = (TextView)
                                        view.findViewById(R.id.video_text);
                                String videoText = cursor.getString(i);
                                tv.setText(videoText);

                                break;
                            case FinchVideo.THUMB_URI_COLUMN:
                                setThumbResource(view, cursor);
                                break;
                        }

                        return true;
                    }
                };

        mAdapter.setViewBinder(savb);

        searchList.setAdapter(mAdapter);

        mSearchText = (EditText) findViewById(R.id.video_search_box);
        mSearchText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView,
                                                  int actionId,
                                                  KeyEvent keyEvent)
                    {
                        // a null key event observed on some devices
                        if (null != keyEvent) {
                            int keyCode = keyEvent.getKeyCode();
                            if ((keyCode == KeyEvent.KEYCODE_ENTER) &&
                                    (keyEvent.getAction() ==
                                            KeyEvent.ACTION_DOWN))
                            {
                                // action only causes the provider to ensure
                                // the presence of some search results.
                                query();

                                return true;
                            }
                        }
                        return false;
                    }
                });

        final ImageButton refreshButton = (ImageButton)
                findViewById(R.id.video_update_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { query(); }
        });
        refreshButton.setFocusable(true);
    }

    void setThumbResource(View view, Cursor cursor) {
        Uri thumbUri = ContentUris.
                withAppendedId(FinchVideo.Videos.THUMB_URI,
                        cursor.getLong(FinchVideo.ID_COLUMN));
        try {
            InputStream thumbStream =
                    getContentResolver().openInputStream(thumbUri);
            ImageView iv = (ImageView)
                    view.findViewById(R.id.video_thumb_icon);
            Bitmap bm = BitmapFactory.decodeStream(thumbStream);
            iv.setImageBitmap(bm);

        } catch (FileNotFoundException e) {
            // dont print because its possible the file was not downloaded yet.
            Log.d(Finch.LOG_TAG, "Thumb not yet downloaded?");
        }
    }

    // sends the query to the finch video content provider
    void query() {
        if (!searchEmpty()) {
            String queryString =
                    FinchVideo.Videos.QUERY_PARAM_NAME + "=" +
                            Uri.encode(mSearchText.getText().toString());
            Uri queryUri =
                    Uri.parse(FinchVideo.Videos.CONTENT_URI + "?" +
                            queryString);
            Cursor c = managedQuery(queryUri, null, null, null, null);
            mAdapter.changeCursor(c);
        }
    }

    private boolean searchEmpty() {
        Editable text = mSearchText.getText();
        if (text != null) {
            String textString = text.toString();
            return "".equals(textString);
        }
        return true;
    }
}
