package com.oreilly.demo.android.pa.finchvideo.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Public API for the example FinchVideo caching content provider example.
 *
 * The public API for a content provider should only contain information that
 * should be referenced by content provider clients. Implementation details
 * such as constants only used by a content provider subclass should not appear
 * in the provider API.
 */
public class FinchVideo {
    public static final int ID_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int DESCRIPTION_COLUMN = 2;
    public static final int THUMB_URI_COLUMN = 3;
    public static final int THUMB_WIDTH_COLUMN = 4;
    public static final int THUMB_HEIGHT_COLUMN = 5;
    public static final int TIMESTAMP_COLUMN = 6;
    public static final int QUERY_TEXT_COLUMN = 7;
    public static final int MEDIA_ID_COLUMN = 8;

    public static final String AUTHORITY =
            "com.oreilly.demo.android.pa.finchvideo.FinchVideo";

    public static final String SIMPLE_AUTHORITY =
            "com.oreilly.demo.android.pa.finchvideo.SimpleFinchVideo";
    /**
     * Simple Videos columns
     */
    public static final class SimpleVideos implements BaseColumns {
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        // This class cannot be instantiated
        private SimpleVideos() {}

        // uri references all videos
        public static final Uri VIDEOS_URI = Uri.parse("content://" +
                SIMPLE_AUTHORITY + "/" + SimpleVideos.VIDEO_NAME);

        /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = VIDEOS_URI;

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.finch.video";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * video.
         */
        public static final String CONTENT_VIDEO_TYPE =
                "vnd.android.cursor.item/vnd.finch.video";

        /**
         * The video itself
         * <P>Type: TEXT</P>
         */
        public static final String VIDEO_NAME = "video";

        /**
         * Column name for the title of the video
         * <P>Type: TEXT</P>
         */
        public static final String TITLE_NAME = "title";

        /**
         * Column name for the description of the video.
         */
        public static final String DESCRIPTION_NAME = "description";

        /**
         * Column name for the media uri
         */
        public static final String URI_NAME = "uri";
    }

    /**
     * Videos content provider public API for more advanced videos example.
     */
    public static final class Videos implements BaseColumns {
        // This class cannot be instantiated
        private Videos() {}

        // uri references all videos
        public static final Uri VIDEOS_URI = Uri.parse("content://" +
                AUTHORITY + "/" + SimpleVideos.VIDEO_NAME);

        public static final Uri THUMB_URI = Uri.parse("content://" +
                AUTHORITY + "/" + Videos.THUMB);

        /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = VIDEOS_URI;

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.finch.video";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * video.
         */
        public static final String CONTENT_VIDEO_TYPE =
                "vnd.android.cursor.item/vnd.finch.video";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * thumbnail.
         */
        public static final String CONTENT_THUMB_TYPE =
                "vnd.android.cursor.item/vnd.finch.videoThumb";

        /**
         * The title of the video
         * <P>Type: TEXT</P>
         */
        public static final String TITLE = "title";

        /**
         * The video itself
         * <P>Type: TEXT</P>
         */
        public static final String VIDEO = "video";

        /**
         * Used to create content provider thumb URIs
         */
        public static final String THUMB = "thumb";

        /**
         * Name of the URI parameter that contains key words that will be
         * sent to the google YouTube API
         */
        public static final String QUERY_PARAM_NAME = "q";

        /**
         * Name of the column that contains the timestamp when a YouTube media
         * element was inserted into the FinchVideoContentProvider database.
         */
        public static final String TIMESTAMP = "timestamp";

        /**
         * Name of the column that contains YouTube media descriptions.
         */
        public static final String DESCRIPTION = "description";

        /**
         * Name of the column that contains YouTube media uris.
         */
        public static final String URI_NAME = "uri";

        /**
         * Name of the column that contains a uri to thumbnails for YouTube
         * video media elements.
         */
        public static final String THUMB_URI_NAME = "thumb_uri";

        /**
         * Name of the column that contains a content uri to thumbnails for
         * YouTube video media elements.
         */
        public static final String THUMB_CONTENT_URI_NAME = "thumb_content_uri";

        /**
         * Name of the thumb width column.
         */
        public static final String THUMB_WIDTH_NAME = "thumb_width";

        /**
         * Name of the thumb height column.
         */
        public static final String THUMB_HEIGHT_NAME = "thumb_height";

        /**
         * Name of the query text column.
         */
        public static final String QUERY_TEXT_NAME = "query_text";

        /**
         * Name of the media ID column.
         */
        public static final String MEDIA_ID_NAME = "media_id";

        /**
         * Name of the thumb data column.
         */
        public static final String _DATA = "_data";
    }
}
