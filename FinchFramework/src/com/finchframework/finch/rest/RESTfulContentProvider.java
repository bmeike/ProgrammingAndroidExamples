package com.finchframework.finch.rest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.finchframework.finch.Finch;
import org.apache.http.client.methods.HttpGet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates functions for asynchronous RESTful requests so that subclass
 * content providers can use them for initiating request while still using
 * custom methods for interpreting REST based content such as, RSS, ATOM,
 * JSON, etc.
 */
public abstract class RESTfulContentProvider extends ContentProvider {
    protected FileHandlerFactory mFileHandlerFactory;
    private Map<String, UriRequestTask> mRequestsInProgress =
            new HashMap<String, UriRequestTask>();

    public RESTfulContentProvider() {
    }

    public void setFileHandlerFactory(FileHandlerFactory fileHandlerFactory) {
        mFileHandlerFactory = fileHandlerFactory;
    }

    public abstract Uri insert(Uri uri, ContentValues cv, SQLiteDatabase db);

    private UriRequestTask getRequestTask(String queryText) {
        return mRequestsInProgress.get(queryText);
    }

    /**
     * Allows the subclass to define the database used by a response handler.
     *
     * @return database passed to response handler.
     */
    public abstract SQLiteDatabase getDatabase();

    public void requestComplete(String mQueryText) {
        synchronized (mRequestsInProgress) {
            mRequestsInProgress.remove(mQueryText);
        }
    }

    /**
     * Abstract method that allows a subclass to define the type of handler
     * that should be used to parse the response of a given request.
     *
     * @param requestTag unique tag identifying this request.
     * @return The response handler created by a subclass used to parse the
     *         request response.
     */
    protected abstract ResponseHandler newResponseHandler(String requestTag);

    UriRequestTask newQueryTask(String requestTag, String url) {
        UriRequestTask requestTask;

        final HttpGet get = new HttpGet(url);
        ResponseHandler handler = newResponseHandler(requestTag);
        requestTask = new UriRequestTask(requestTag, this, get,
                handler, getContext());

        mRequestsInProgress.put(requestTag, requestTask);
        return requestTask;
    }

    /**
     * Creates a new worker thread to carry out a RESTful network invocation.
     *
     * @param queryTag unique tag that identifies this request.
     * @param queryUri the complete URI that should be access by this request.
     */
    public void asyncQueryRequest(String queryTag, String queryUri) {
        synchronized (mRequestsInProgress) {
            UriRequestTask requestTask = getRequestTask(queryTag);
            if (requestTask == null) {
                requestTask = newQueryTask(queryTag, queryUri);
                Thread t = new Thread(requestTask);
                // allows other requests to run in parallel.
                t.start();
            }
        }
    }

    /**
     * Spawns a thread to download bytes from a url and store them in a file,
     * such as for storing a thumbnail.
     *
     * @param id the database id used to reference the downloaded url.
     */
    public void cacheUri2File(String id, String url) {
        // use media id as a unique request tag
        final HttpGet get = new HttpGet(url);
        UriRequestTask requestTask = new UriRequestTask(
                get, mFileHandlerFactory.newFileHandler(id),
                getContext());
        Thread t = new Thread(requestTask);
        t.start();
    }

    public void deleteFile(String id) {
        mFileHandlerFactory.delete(id);
    }

    public String getCacheName(String id) {
        return mFileHandlerFactory.getFileName(id);
    }

    public static String encode(String gDataQuery) {
        try {
            return URLEncoder.encode(gDataQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(Finch.LOG_TAG, "could not decode UTF-8," +
                    " this should not happen");
        }
        return null;
    }
}
