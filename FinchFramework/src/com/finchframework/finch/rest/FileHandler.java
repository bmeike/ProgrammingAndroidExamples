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
    private File mCacheDir;

    public FileHandler(File cacheDir, String id) {
        mCacheDir = cacheDir;
        mId = id;
    }

    public File getFile(String ID) {
        return new File(mCacheDir, ID);
    }

    public void handleResponse(HttpResponse response, Uri uri)
            throws IOException {
        InputStream urlStream = response.getEntity().getContent();
        FileOutputStream fout =
                new FileOutputStream(getFile(mId));
        byte[] bytes = new byte[256];
        int r;
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
