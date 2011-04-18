package com.finchframework.finch.rest;

import java.io.File;

/**
 * Creates instances of FileHandler objects that use a common cache directory.
 * The cache directory is set in the constructor to the file handler factory.
 */
public class FileHandlerFactory {
    private String mCacheDir;

    public FileHandlerFactory(String cacheDir) {
        mCacheDir = cacheDir;
        init();
    }

    private void init() {
        File cacheDir = new File(mCacheDir);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
    }

    public FileHandler newFileHandler(String id) {
        return new FileHandler(mCacheDir, id);
    }

    // not really used since ContentResolver uses _data field.
    public File getFile(String ID) {
        String cachePath = getFileName(ID);

        File cacheFile = new File(cachePath);
        if (cacheFile.exists()) {
            return cacheFile;
        }
        return null;
    }

    public void delete(String ID) {
        String cachePath = mCacheDir + "/" + ID;

        File cacheFile = new File(cachePath);
        if (cacheFile.exists()) {
            cacheFile.delete();
        }
    }

    public String getFileName(String ID) {
        return mCacheDir + "/" + ID;
    }
}
