package pa.ch16;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import pa.ch16.geo.Geo;

/**
 * Enables selection of a given Google User Generated map.
 */
public class MapSelectActivity extends ListActivity {
    public static final String SITE_URL = "site_uri";

    private SimpleCursorAdapter mListAdapter;

    public static final String PROGRAMMING_ANDROID_GOOGLE_ID =
            "217137611414464524385";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView siteListView = getListView();

        String queryString = Geo.Sites.GUID + "=" +
                Uri.encode(PROGRAMMING_ANDROID_GOOGLE_ID);
        Uri siteUri = Uri.parse(Geo.Sites.CONTENT_URI + "?" +
                queryString);

        Cursor siteCursor = managedQuery(siteUri, null, null, null, null);

        // Maps video entries from the database to views
        mListAdapter = new SimpleCursorAdapter(this,
                R.layout.geo_list_item,
                siteCursor,
                new String[] {
                        Geo.Sites.TITLE,
                },
                new int[] { R.id.site_text });

        SimpleCursorAdapter.ViewBinder savb =
                new SimpleCursorAdapter.ViewBinder() {
                    public boolean setViewValue(View view, Cursor cursor, int i) {
                        switch (i) {
                            case Geo.TITLE_COLUMN:
                                TextView tv = (TextView)
                                        view.findViewById(R.id.site_text);
                                String videoText = cursor.getString(i);
                                tv.setText(videoText);

                                break;
                        }

                        return true;
                    }
                };

        mListAdapter.setViewBinder(savb);

        siteListView.setAdapter(mListAdapter);

        siteListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int pos, long id)
                    {
                        mListAdapter.getItem(pos);
                        Cursor c = mListAdapter.getCursor();
                        String msid = c.getString(Geo.MSID_COLUMN);

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra(SITE_URL,
                                Geo.Sites.CONTENT_URI + "/" + msid);
                        intent.setClass(MapSelectActivity.this,
                                SiteViewerActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
