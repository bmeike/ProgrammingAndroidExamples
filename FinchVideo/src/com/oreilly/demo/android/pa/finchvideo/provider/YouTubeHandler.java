package com.oreilly.demo.android.pa.finchvideo.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import com.finchframework.finch.Finch;
import com.finchframework.finch.rest.RESTfulContentProvider;
import com.finchframework.finch.rest.ResponseHandler;
import com.oreilly.demo.android.pa.finchvideo.FinchVideoDemo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parses YouTube entity data and and inserts it into the finch video content
 * provider.
 */
public class YouTubeHandler implements ResponseHandler {
    public static final String MEDIA = "media";
    public static final String GROUP = "group";
    public static final String DESCRIPTION = "description";
    public static final String THUMBNAIL = "thumbnail";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

    public static final String YT = "yt";
    public static final String DURATION = "duration";
    public static final String FORMAT = "format";

    public static final String URI = "uri";
    public static final String THUMB_URI = "thumb_uri";

    public static final String MOBILE_FORMAT = "1";

    public static final String ENTRY = "entry";
    public static final String ID = "id";

    private static final String FLUSH_TIME = "5 minutes";

    private RESTfulContentProvider mFinchVideoProvider;

    private String mQueryText;
    private boolean isEntry;

    public YouTubeHandler(RESTfulContentProvider restfulProvider,
                          String queryText)
    {
        mFinchVideoProvider = restfulProvider;
        mQueryText = queryText;
    }

    /*
     * Handles the response from the YouTube gdata server, which is in the form
     * of an RSS feed containing references to YouTube videos.
     */
    @Override
    public void handleResponse(HttpResponse response, Uri uri) {
        try {
            int newCount = parseYoutubeEntity(response.getEntity());

            // only flush old state now that new state has arrived
            if (newCount > 0) {
                deleteOld();
            }

        } catch (IOException e) {
            // use the exception to avoid clearing old state, if we can not
            // get new state.  This way we leave the application with some
            // data to work with in absence of network connectivity.

            // we could retry the request for data in the hope that the network
            // might return.
        }
    }

    private void deleteOld() {
        // delete any old elements, not just ones that match the current query.

        Cursor old = null;

        try {
            SQLiteDatabase db = mFinchVideoProvider.getDatabase();
            old = db.query(FinchVideo.Videos.VIDEO, null,
                    "video." + FinchVideo.Videos.TIMESTAMP +
                            " < strftime('%s', 'now', '-" + FLUSH_TIME + "')",
                    null, null, null, null);
            int c = old.getCount();
            if (old.getCount() > 0) {
                StringBuilder sb = new StringBuilder();
                boolean next;
                if (old.moveToNext()) {
                    do {
                        String ID = old.getString(FinchVideo.ID_COLUMN);
                        sb.append(BaseColumns._ID);
                        sb.append(" = ");
                        sb.append(ID);

                        // get rid of associated cached thumb files
                        mFinchVideoProvider.deleteFile(ID);

                        next = old.moveToNext();
                        if (next) {
                            sb.append(" OR ");
                        }
                    } while (next);
                }
                String where = sb.toString();

                db.delete(FinchVideo.Videos.VIDEO, where, null);

                Log.d(Finch.LOG_TAG, "flushed old query results: " + c);
            }
        } finally {
            if (old != null) {
                old.close();
            }
        }
    }

    private int parseYoutubeEntity(HttpEntity entity) throws IOException {
        InputStream youTubeContent = entity.getContent();
        InputStreamReader inputReader = new InputStreamReader(youTubeContent);

        int inserted = 0;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(inputReader);

            int eventType = xpp.getEventType();
            String startName = null;
            ContentValues mediaEntry = null;

            // iterative pull parsing is a useful way to extract data from
            // streams, since we dont have to hold the DOM model in memory
            // during the parsing step.

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.END_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    startName = xpp.getName();

                    if ((startName != null)) {

                        if ((ENTRY).equals(startName)) {
                            mediaEntry = new ContentValues();
                            mediaEntry.put(FinchVideo.Videos.QUERY_TEXT_NAME,
                                    mQueryText);
                        }

                        if ((MEDIA + ":" + CONTENT).equals(startName)) {
                            int c = xpp.getAttributeCount();
                            String mediaUri = null;
                            boolean isMobileFormat = false;

                            for (int i = 0; i < c; i++) {
                                String attrName = xpp.getAttributeName(i);
                                String attrValue = xpp.getAttributeValue(i);

                                if ((attrName != null) &&
                                        URI.equals(attrName))
                                {
                                    mediaUri = attrValue;
                                }

                                if ((attrName != null) && (YT + ":" + FORMAT).
                                        equals(MOBILE_FORMAT))
                                {
                                    isMobileFormat = true;
                                }
                            }

                            if (isMobileFormat && (mediaUri != null)) {
                                mediaEntry.put(URI, mediaUri);
                            }
                        }

                        if ((MEDIA + ":" + THUMBNAIL).equals(startName)) {
                            int c = xpp.getAttributeCount();
                            for (int i = 0; i < c; i++) {
                                String attrName = xpp.getAttributeName(i);
                                String attrValue = xpp.getAttributeValue(i);

                                if (attrName != null) {
                                    if ("url".equals(attrName)) {
                                        mediaEntry.put(
                                                FinchVideo.Videos.
                                                        THUMB_URI_NAME,
                                                attrValue);
                                    } else if (WIDTH.equals(attrName))
                                    {
                                        mediaEntry.put(
                                                FinchVideo.Videos.
                                                        THUMB_WIDTH_NAME,
                                                attrValue);
                                    } else if (HEIGHT.equals(attrName))
                                    {
                                        mediaEntry.put(
                                                FinchVideo.Videos.
                                                        THUMB_HEIGHT_NAME,
                                                attrValue);
                                    }
                                }
                            }
                        }

                        if (ENTRY.equals(startName)) {
                            isEntry = true;
                        }
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    String endName = xpp.getName();

                    if (endName != null) {
                        if (ENTRY.equals(endName)) {
                            isEntry = false;
                        } else if (endName.equals(MEDIA + ":" + GROUP)) {
                            // insert the complete media group
                            inserted++;

                            // Directly invoke insert on the finch video
                            // provider, without using content resolver.  We
                            // would not want the content provider to sync this
                            // data back to itself.
                            SQLiteDatabase db =
                                    mFinchVideoProvider.getDatabase();

                            String mediaID = (String) mediaEntry.get(
                                    FinchVideo.Videos.MEDIA_ID_NAME);

                            // insert thumb uri
                            String thumbContentUri =
                                    FinchVideo.Videos.THUMB_URI + "/" + mediaID;
                            mediaEntry.put(FinchVideo.Videos.
                                    THUMB_CONTENT_URI_NAME,
                                    thumbContentUri);

                            String cacheFileName =
                                    mFinchVideoProvider.getCacheName(mediaID);
                            mediaEntry.put(FinchVideo.Videos._DATA,
                                    cacheFileName);

                            Uri providerUri = mFinchVideoProvider.
                                    insert(FinchVideo.Videos.CONTENT_URI,
                                            mediaEntry, db);

                            if (providerUri != null) {
                                String thumbUri = (String) mediaEntry.
                                        get(FinchVideo.Videos.THUMB_URI_NAME);

                                // We might consider lazily downloading the
                                // image so that it was only downloaded on
                                // viewing.  Downloading more aggressively,
                                // could also improve performance.

                                mFinchVideoProvider.
                                        cacheUri2File(String.valueOf(mediaID),
                                                thumbUri);
                            }
                        }
                    }

                } else if (eventType == XmlPullParser.TEXT) {
                    // newline can turn into an extra text event
                    String text = xpp.getText();
                    if (text != null) {
                        text = text.trim();
                        if ((startName != null) && (!"".equals(text))){
                            if (ID.equals(startName) && isEntry) {
                                int lastSlash = text.lastIndexOf("/");
                                String entryId =
                                        text.substring(lastSlash + 1);
                                mediaEntry.put(FinchVideo.Videos.MEDIA_ID_NAME,
                                        entryId);
                            } else if ((MEDIA + ":" + TITLE).
                                    equals(startName)) {
                                mediaEntry.put(TITLE, text);
                            } else if ((MEDIA + ":" +
                                    DESCRIPTION).equals(startName))
                            {
                                mediaEntry.put(DESCRIPTION, text);
                            }
                        }
                    }
                }
                eventType = xpp.next();
            }

            // an alternate notification scheme, might be to notify only after
            // all entries have been inserted.

        } catch (XmlPullParserException e) {
            Log.d(FinchVideoDemo.LOG_TAG,
                    "could not parse video feed", e);
        } catch (IOException e) {
            Log.d(FinchVideoDemo.LOG_TAG,
                    "could not process video stream", e);
        }

        return inserted;
    }
}
