package com.finchframework.finch.rest;

import android.content.Context;
import android.util.Log;
import com.finchframework.finch.Finch;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class RawResponse implements HttpResponse {
    private HttpEntity mDebugEntity;

    public RawResponse(Context c, int rawResource) {
        try {
            final InputStream rawVideoInput = c.getResources().
                    openRawResource(rawResource);
            mDebugEntity = new InputStreamEntity(rawVideoInput,
                    rawVideoInput.available());
        } catch (IOException e) {
            Log.d(Finch.LOG_TAG, "exception from raw input stream", e);
        }
    }

    public StatusLine getStatusLine() {
        return null;
    }

    public void setStatusLine(StatusLine statusLine) {
    }

    public void setStatusLine(ProtocolVersion protocolVersion,
                              int i) {
    }

    public void setStatusLine(ProtocolVersion protocolVersion,
                              int i, String s) {
    }

    public void setStatusCode(int i)
            throws IllegalStateException {
    }

    public void setReasonPhrase(String s)
            throws IllegalStateException {
    }

    public HttpEntity getEntity() {
        return mDebugEntity;
    }

    public void setEntity(HttpEntity httpEntity) {
    }

    public Locale getLocale() {
        return null;
    }

    public void setLocale(Locale locale) {
    }

    public ProtocolVersion getProtocolVersion() {
        return null;
    }

    public boolean containsHeader(String s) {
        return false;
    }

    public Header[] getHeaders(String s) {
        return new Header[0];
    }

    public Header getFirstHeader(String s) {
        return null;
    }

    public Header getLastHeader(String s) {
        return null;
    }

    public Header[] getAllHeaders() {
        return new Header[0];
    }

    public void addHeader(Header header) {
    }

    public void addHeader(String s, String s1) {
    }

    public void setHeader(Header header) {
    }

    public void setHeader(String s, String s1) {
    }

    public void setHeaders(Header[] headers) {
    }

    public void removeHeader(Header header) {
    }

    public void removeHeaders(String s) {
    }

    public HeaderIterator headerIterator() {
        return null;
    }

    public HeaderIterator headerIterator(String s) {
        return null;
    }

    public HttpParams getParams() {
        return null;
    }

    public void setParams(HttpParams httpParams) {
    }
};
