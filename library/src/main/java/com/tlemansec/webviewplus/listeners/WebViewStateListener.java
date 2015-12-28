package com.tlemansec.webviewplus.listeners;

import android.graphics.Bitmap;


/**
 * Created by thibault on 23/12/2015.
 */
public interface WebViewStateListener {

    void onStartLoading(String url, Bitmap favicon);
    void onError(int errorCode, String description, String failingUrl);
    void onFinishLoaded(String loadedUrl);

}