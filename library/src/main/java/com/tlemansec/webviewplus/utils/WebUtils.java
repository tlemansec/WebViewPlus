package com.tlemansec.webviewplus.utils;

import android.util.Log;
import android.webkit.WebView;

import com.tlemansec.webviewplus.WebConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by thibault on 28/11/2015.
 */
public abstract class WebUtils {

    //region Attributes

    private static final String TAG = WebUtils.class.getSimpleName();

    //endregion


    //region Utils

    /**
     * Recursive function to go back to the root of the webview's back history.
     * @param view The webView concerned by the modification back history.
     */
    public static void backToRootHistory(WebView view) {
        if (view != null && view.canGoBack()) {
            Log.d(TAG, "back in webview");

            //The webview is available and we can go back in its history
            view.goBack();

            //call in a recursive way so we can go back to the root of the back history.
            backToRootHistory(view);
        }
    }

    /**
     * Get all parameters and their values from an url. The method handles multi-valued params,
     * hence the List<String> rather than String.
     *
     * @param url The url to read and interpret.
     * @return a map of paramters/values.
     */
    public static Map<String, String> getQueryParams(String url) throws MalformedURLException {
        Map<String, String> params = new HashMap<>();

        try {
            URL tmpUrl = new URL(url);

            //Get the url part containing parameters.
            String query = tmpUrl.getQuery();

            if (query != null) {
                for (String param : query.split(WebConstants.PARAMETER_SEPARATOR)) {
                    String[] pair = param.split(WebConstants.NAME_VALUE_SEPARATOR);

                    //Get the key associated to the current param.
                    String key = URLDecoder.decode(pair[0], WebConstants.UTF_8);
                    String value = "";

                    if (pair.length > 1) {
                        //Get the value associated to the current param.
                        value = URLDecoder.decode(pair[1], WebConstants.UTF_8);
                    }

                    params.put(key, value);
                }
            }

            return params;

        } catch (Exception ex) {
            Log.getStackTraceString(ex);
            return params;
        }
    }

    /**
     * Construct a new url by adding parameters to it.
     *
     * @param url The url to complete with parameters.
     * @param params A Map of parameters to add to the url.
     * @return a String url with added parameters.
     */
    public static String addQueryParams(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        try {
            String encodedUrl = URLEncoder.encode(url, WebConstants.UTF_8);
            String newUrl = removeUrlEndSlashCharacter(encodedUrl);

            if (isQueryExpectingParams(url)) {
                //ie https://www.youtube.com/watch?
                newUrl +=  addParameters(params);

            } else if (isQueryWithExistingParams(url)) {
                //ie https://www.google.fr/webhp?sourceid=chrome-instant
                newUrl += addParameters(params);

            } else {
                //ie https://www.google.fr
                newUrl += WebConstants.QUERY_CHARACTER;
                newUrl += addParameters(params);
            }

            newUrl = URLEncoder.encode(newUrl, WebConstants.UTF_8);
            Log.d(TAG, "encodedUrl: " + newUrl);

            return newUrl;

        } catch (Exception ex) {
            Log.getStackTraceString(ex);
            return url;
        }
    }

    /**
     * Construct a String url containing get parameters from the Map.
     *
     * @param params The Map with parameters to add.
     * @return a String url containing get parameters from the Map.
     */
    private static String addParameters(Map<String, String> params) {
        String url = "";

        if (params != null) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                url += WebConstants.PARAMETER_SEPARATOR + pair.getKey()
                        + WebConstants.NAME_VALUE_SEPARATOR + pair.getValue();

                //Avoids a ConcurrentModificationException.
                it.remove();
            }
        }

        return url;
    }

    /**
     * Detect if the query is expecting parameters, ie already contains the ? character and already
     * has at least one parameter.
     *
     * @param url The url to analyze to know if it has parameters or not.
     * @return true if the url has parameters.
     */
    private static boolean isQueryWithExistingParams(String url) throws MalformedURLException {
        try {
            URL tmpUrl = new URL(url);

            //Get the url part containing parameters.
            return tmpUrl.getQuery().length() > 1;

        } catch (Exception ex) {
            Log.getStackTraceString(ex);
            return false;
        }
    }

    /**
     * Detect if the query is waiting for parameters and is malformed.
     * ie https://www.youtube.com/watch?
     *
     * @param url The url to analyze to know if it is waiting for parameters or not.
     * @return true if the url is waiting for parameters.
     */
    private static boolean isQueryExpectingParams(String url) {
        try {
            return url.endsWith(WebConstants.QUERY_CHARACTER);

        } catch (NullPointerException ex) {
            Log.getStackTraceString(ex);
            return false;
        }
    }

    /**
     * Remove the last character from a string if it is a "/".
     *
     * @param url The string to analyze which may end by a /.
     * @return a string without the / at the end.
     */
    private static String removeUrlEndSlashCharacter(String url) {
        if (url != null && url.endsWith(WebConstants.SPLIT_CHARACTER)) {
            url = url.replace(url.substring(url.length() - 1), WebConstants.EMPTY_STR);
        }

        return url;
    }

    //endregion
}
