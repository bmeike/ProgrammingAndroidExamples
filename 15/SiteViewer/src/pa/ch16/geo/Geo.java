package pa.ch16.geo;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Public API for the example geo Site caching content provider example.
 * Downloads and caches kml site based geography files.
 */
public class Geo {
    public static final int ID_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int DESCRIPTION_COLUMN = 2;
    public static final int GUID_COLUMN = 4;
    public static final int MSID_COLUMN = 4;
    public static final int SITE_ID_COLUMN = 5;
    public static final int TIMESTAMP_COLUMN = 3;

    public static final String AUTHORITY =
            "com.finchframework.finch.geo.Sites";

    public static final String KML_TYPE =
            "vnd.finchFramework.kml/vnd.finch.kml-content";

    /**
     * Geo sites columns.
     */
    public static final class Sites implements BaseColumns {
        // This class cannot be instantiated
        private Sites() {}

        // uri references all sites
        public static final Uri SITE_URI = Uri.parse("content://" +
                AUTHORITY + "/" + Sites.SITES);

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = SITE_URI;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * geosite.
         */
        public static final String CONTENT_SITE_TYPE =
                "vnd.android.cursor.item/vnd.finch.geosite";

        /**
         * The title of the video
         * <P>Type: TEXT</P>
         */
        public static final String TITLE = "title";

        /**
         * The site itself
         * <P>Type: TEXT</P>
         */
        public static final String SITES = "sites";

        public static final String GUID = "guid";

        public static final String MSID = "msid";

        public static final String TIMESTAMP = "timestamp";

        public static final String DESCRIPTION = "description";

    }
}
