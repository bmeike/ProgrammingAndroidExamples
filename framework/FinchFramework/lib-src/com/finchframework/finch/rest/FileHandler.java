package com.finchframework.finch.rest;

import android.net.Uri;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Writes data from urls into a local file cache that can be referenced by a
 * database ID.
 */
public class FileHandler implements ResponseHandler {
    private String mId;
    private String mCacheDir;

    public FileHandler(String cacheDir, String id) {
        mCacheDir = cacheDir;
        mId = id;
    }

    public
    String getFileName(String ID) {
        return mCacheDir + "/" + ID;
    }

    public void handleResponse(HttpResponse response, Uri uri)
            throws IOException
    {
        InputStream urlStream = response.getEntity().getContent();
        FileOutputStream fout =
                new FileOutputStream(getFileName(mId));
        byte[] bytes = new byte[256];
        int r = 0;
        do {
            r = urlStream.read(bytes);
            if (r >= 0) {
                fout.write(bytes, 0, r);
            }
        } while (r >= 0);

        urlStream.close();
        fout.close();
    }
}
