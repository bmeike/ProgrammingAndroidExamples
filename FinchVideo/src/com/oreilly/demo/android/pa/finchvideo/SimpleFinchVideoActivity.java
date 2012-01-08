package com.oreilly.demo.android.pa.finchvideo;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.finchframework.finch.rest.RESTfulContentProvider;
import com.oreilly.demo.android.pa.finchvideo.provider.FinchVideo;

/**
 * Simple FinchVideo search application that merely displays a list of video
 * related meta-data.
 */
public class SimpleFinchVideoActivity extends Activity {
    public static final String MEDIA_ID = "mediaId";

    SimpleCursorAdapter mAdapter;

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mMediaIdEditText;
    private Button mInsertButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_video_activity);

        final ListView searchList = (ListView)
                findViewById(R.id.simple_video_list);

        Cursor videoCursor =
                managedQuery(FinchVideo.SimpleVideos.CONTENT_URI, null,
                        null, null, null);

        // have to reset this on a new search

        // Maps video entries from the database to views
        mAdapter = new SimpleCursorAdapter(this,
            R.layout.simple_video_list_item,
            videoCursor,
            new String[] {
            FinchVideo.SimpleVideos.TITLE_NAME,
            FinchVideo.SimpleVideos.DESCRIPTION_NAME
        },
        new int[] { R.id.simple_video_title_text });

        SimpleCursorAdapter.ViewBinder savb =
            new SimpleCursorAdapter.ViewBinder() {
            @Override public boolean setViewValue(
                View view,
                Cursor cursor, int i)
            {
                switch (i) {
                    case FinchVideo.TITLE_COLUMN:
                        TextView tv = (TextView)
                        view.findViewById(
                            R.id.simple_video_title_text);
                        String videoText = cursor.getString(i);
                        tv.setText(videoText);
                        break;
                }

                return true;
            }
        };

        mAdapter.setViewBinder(savb);

        searchList.setAdapter(mAdapter);

        mTitleEditText = (EditText)
                findViewById(R.id.simple_video_title_edit);
        mDescriptionEditText = (EditText)
                findViewById(R.id.simple_video_description_edit);
        mMediaIdEditText = (EditText)
                findViewById(R.id.simple_video_media_id_edit);

        mInsertButton = (Button)
                findViewById(R.id.simple_video_insert_button);
        mInsertButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { insert(); }
        });
    }

    void insert() {
        ContentValues values = new ContentValues();
        String title = mTitleEditText.getText().toString();
        values.put(FinchVideo.SimpleVideos.TITLE_NAME, title);
        String description = mDescriptionEditText.getText().toString();
        values.put(FinchVideo.SimpleVideos.DESCRIPTION_NAME, description);
        String mediaId = mMediaIdEditText.getText().toString();
        String uri = "http://gdata.youtube.com/mediaId/" +
                RESTfulContentProvider.encode(mediaId);
        values.put(FinchVideo.SimpleVideos.URI_NAME, uri);

        getContentResolver().insert(FinchVideo.SimpleVideos.CONTENT_URI, values);

        mTitleEditText.setText("");
        mDescriptionEditText.setText("");
        mMediaIdEditText.setText("");
        mTitleEditText.requestFocus();
    }
}
