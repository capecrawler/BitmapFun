package com.example.android.bitmapfun.util;

import com.example.android.bitmapfun.BuildConfig;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class InBitmapDrawable extends BitmapDrawable{

	
	static final String LOG_TAG = "InBitmapDrawable";

    private int mCacheRefCount = 0;
    private int mDisplayRefCount = 0;

    private boolean mHasBeenDisplayed;

    public InBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    /**
     * Notify the drawable that the displayed state has changed. Internally a
     * count is kept so that the drawable knows when it is no longer being
     * displayed.
     *
     * @param isDisplayed - Whether the drawable is being displayed or not
     */
    public void setIsDisplayed(boolean isDisplayed) {
        synchronized (this) {
            if (isDisplayed) {
                mDisplayRefCount++;
                mHasBeenDisplayed = true;
            } else {
                mDisplayRefCount--;
            }
        }

        // Check to see if recycle() can be called
        checkState();
    }

    /**
     * Notify the drawable that the cache state has changed. Internally a count
     * is kept so that the drawable knows when it is no longer being cached.
     *
     * @param isCached - Whether the drawable is being cached or not
     */
    public void setIsCached(boolean isCached) {
        synchronized (this) {
            if (isCached) {
                mCacheRefCount++;
            } else {
                mCacheRefCount--;
            }
        }

        // Check to see if recycle() can be called
        // checkState();
    }

    private synchronized void checkState() {
        // If the drawable cache and display ref counts = 0, and this drawable
        // has been displayed, then recycle
        if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed
                && hasValidBitmap()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "No longer being used or cached so recycling. "
                        + toString());
            }
            getBitmap().recycle();        
        }
        
        
    }
    
    
    public synchronized boolean inBitmapSafe() {
        // If the drawable cache and display ref counts = 0, and this drawable
        // has been displayed, then recycle
        if (mCacheRefCount <= 0 && mDisplayRefCount <= 0 && mHasBeenDisplayed
                && hasValidBitmap()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "No longer being used or cached so recycling. "
                        + toString());
            }

            //getBitmap().recycle();
            return true;
        }
        return false;
        
    }

    private synchronized boolean hasValidBitmap() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }
    
}
