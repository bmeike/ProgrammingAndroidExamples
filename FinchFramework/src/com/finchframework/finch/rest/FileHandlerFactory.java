package com.finchframework.finch.rest;

import java.io.File;

/**
 * Creates instances of FileHandler objects that use a common cache directory.
 * The cache directory is set in the constructor to the file handler factory.
 */
public class FileHandlerFactory {
    private File mCacheDir;

    public FileHandlerFactory(File cacheDir) {
        mCacheDir = cacheDir;
        init();
    }

    private void init() {
        if (!mCacheDir.exists()) {
            mCacheDir.mkdir();
        }
    }

    public FileHandler newFileHandler(String id) {
        return new FileHandler(mCacheDir, id);
    }

    public void delete(String ID) {
        File cacheFile = new File(mCacheDir, ID);
        if (cacheFile.exists()) {
            cacheFile.delete();
        }
    }

    public String getFileName(String ID) {
        return new File(mCacheDir, ID).toString();
    }
}
