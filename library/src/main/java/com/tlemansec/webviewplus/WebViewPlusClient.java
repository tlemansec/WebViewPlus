package com.tlemansec.webviewplus;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tlemansec.webviewplus.listeners.WebViewStateListener;


/**
 * Created by thibault on 17/09/2015.
 */
public class WebViewPlusClient extends WebViewClient {

    //region Attributes

    protected WebViewStateListener mWebViewStateListener;
    protected UrlLoadingState mUrlLoadingState = UrlLoadingState.NOT_LOADED;

    private static final String TAG = WebViewPlusClient.class.getSimpleName();

    //endregion


    //region Life Cycle

    /**
     * Notify the host application that a page has started loading.
     *
     * @param view The WebView that is initiating the callback.
     * @param url The url to be loaded.
     * @param favicon The favicon for this page if it already exists in the database.
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG, "onPageStarted: " + url);

        super.onPageStarted(view, url, favicon);

        mUrlLoadingState = UrlLoadingState.IS_LOADING;

        if (mWebViewStateListener != null) {
            mWebViewStateListener.onStartLoading(url, favicon);
        }
    }

    /**
     * Notify the host application that a page has finished loading.
     *
     * @param view The WebView that is initiating the callback.
     * @param url The url of the page.
     */
    @Override
    public void onPageFinished(final WebView view, String url) {
        Log.d(TAG, "onPageFinished: " + url);

        super.onPageFinished(view, url);

        if (UrlLoadingState.IS_LOADING.equals(mUrlLoadingState)) {
            mUrlLoadingState = UrlLoadingState.SUCCESFULLY_LOADED;
        }

        if (mWebViewStateListener != null) {
            mWebViewStateListener.onFinishLoaded(url);
        }
    }

    /**
     * Notify the host application that an SSL error occurred while loading a resource.
     *
     * @param view The WebView that is initiating the callback.
     * @param handler An SslErrorHandler object that will handle the user's response.
     * @param error The SSL error object.
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Log.e(TAG, "onReceivedSslError: " + error);

        handler.proceed();
    }

    /**
     * Report web resource loading error to the host application.
     *
     * @param view The WebView that is initiating the callback.
     * @param errorCode The originating request.
     * @param description Information about the error occured.
     * @param url The url that failed to load.
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String url) {
        Log.e(TAG, "onReceivedError: " + url + " | description: " + description
                + " | errorCode: " + errorCode);

        super.onReceivedError(view, errorCode, description, url);

        mUrlLoadingState = UrlLoadingState.FAILED_LOADING;

        if (mWebViewStateListener != null) {
            mWebViewStateListener.onError(errorCode, description, url);
        }
    }

    //endregion


    //region Getters Setters

    /**
     * @return a reference to the current loading state of the webview.
     */
    public UrlLoadingState getUrlLoadingState() {
        return mUrlLoadingState;
    }

    //endregion
}
