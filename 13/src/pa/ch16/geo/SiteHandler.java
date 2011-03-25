package pa.ch16.geo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.finchframework.finch.rest.RESTfulContentProvider;
import com.finchframework.finch.rest.ResponseHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SiteHandler implements ResponseHandler {
    private RESTfulContentProvider mContentProvider;
    private SQLiteDatabase mDb;
    private String mRequestTag;

    // strings used to scrap site listing html.
    private static final String MSID_MARKER = "<div msid=\"";
    private static final String MSID_END = "\"";
    private static final String TITLE_MARKER = "maptitle\"> ";
    private static final String TITLE_END = "</a>";

    // larger than all markers
    private static final int MAX_LEN = 15;

    private static final int NO_ACTION = -1;
    private static final int READING_ID = 1;
    private static final int READING_TITLE = 2;

    public SiteHandler(RESTfulContentProvider contentProvider,
                       SQLiteDatabase db, String requestTag)
    {
        mContentProvider = contentProvider;
        mDb = db;
        mRequestTag = requestTag;
    }

    public void handleResponse(HttpResponse response, Uri uri)
            throws IOException
    {
        HttpEntity entity = response.getEntity();

        InputStream siteContent = entity.getContent();
        BufferedReader htmlReader = new BufferedReader(
                new InputStreamReader(siteContent, "UTF-8"));

        StringBuilder markerWindow = new StringBuilder();
        StringBuilder buffer = new StringBuilder();

        ContentValues siteEntry = null;

        int expMsidIdx = MAX_LEN - MSID_MARKER.length();
        int expTitleIdx = MAX_LEN - TITLE_MARKER.length();
     
        int action = NO_ACTION;
        char c;
        for (int r = htmlReader.read(); r >= 0;
             r = htmlReader.read())
        {
            c = (char)r;

            // window should be a large enough window of chars
            if (markerWindow.length() > (MAX_LEN + 1)) {
                markerWindow.deleteCharAt(0);
            }
            markerWindow.append(c);

            if (action == NO_ACTION) {
                int msidIdx = markerWindow.indexOf(MSID_MARKER);
                int titleIndex = markerWindow.indexOf(TITLE_MARKER);

                if ((msidIdx == expMsidIdx) && (msidIdx >= 0)) {
                    action = READING_ID;
                } else if ((titleIndex == expTitleIdx) && (titleIndex >= 0))  {
                    action = READING_TITLE;
                }

            } else {

                int expMsidEndIdx = markerWindow.length() - MSID_END.length();
                int msidEndIdx = markerWindow.indexOf(MSID_END);

                int expTitleEndIdx = markerWindow.length() - TITLE_END.length();
                int titleEndIdx = markerWindow.indexOf(TITLE_END);

                switch (action) {
                    case READING_ID:
                        if ((msidEndIdx == expMsidEndIdx) &&
                                (expMsidEndIdx >= 0))
                        {
                            if (siteEntry == null) {
                                siteEntry = new ContentValues();
                                siteEntry.put(Geo.Sites.GUID, mRequestTag);
                            }

                            siteEntry.put(Geo.Sites.MSID, buffer.toString());
                            buffer = new StringBuilder();
                            action = NO_ACTION;
                        } else {
                            buffer.append(c);
                        }

                        break;
                    case READING_TITLE:
                        if ((titleEndIdx == expTitleEndIdx) &&
                                (expTitleEndIdx >= 0))
                        {
                            String title = buffer.toString().trim();
                            title = title.replace("\n", "");
                            title = title.replaceAll(" +", " ");
                            siteEntry.put(Geo.Sites.TITLE, title);

                            mContentProvider.insert(Geo.Sites.CONTENT_URI,
                                    siteEntry, mDb);
                            
                            siteEntry = null;
                            buffer = new StringBuilder();

                            action = NO_ACTION;
                        } else {
                            buffer.append(c);
                        }
                        break;
                    case NO_ACTION:
                        break;
                    default:
                        throw new IllegalStateException("unknown action: " +
                                action);
                }
            }
        }
    }
}
