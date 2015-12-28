package com.tlemansec.webviewplus.bridge.utils;

import android.content.Context;
import android.webkit.WebView;

import com.tlemansec.webviewplus.WebConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by thibault on 24/12/2015.
 */
public class BridgeUtils {

    //region Attributes

    public static final String YY_OVERRIDE_SCHEMA = "yy://";
    public static final String YY_RETURN_DATA = YY_OVERRIDE_SCHEMA + "return/";//格式为   yy://return/{function}/returncontent
    public static final String YY_FETCHQUEUE = YY_RETURN_DATA + "_fetchQueue/";

    public static final String CALLBACK_ID_FORMAT = "JAVA_CB_%s";
    public static final String JAVASCRIPT_STR = "javascript:";
    public static final String JAVASCRIPT_BRIDGE_STR = JAVASCRIPT_STR + "WebViewJavascriptBridge.";
    public static final String JS_HANDLE_MESSAGE_FROM_JAVA = JAVASCRIPT_BRIDGE_STR + "_handleMessageFromNative('%s');";
    public static final String JS_FETCH_QUEUE_FROM_JAVA = JAVASCRIPT_BRIDGE_STR + "_fetchQueue();";

    private static final String TAG = BridgeUtils.class.getSimpleName();

    //endregion


    //region Utils

    public static String parseFunctionName(String jsUrl){
        return jsUrl.replace(JAVASCRIPT_BRIDGE_STR, "").replaceAll("\\(.*\\);", "");
    }

    public static String getDataFromReturnUrl(String url) {
        if(url.startsWith(YY_FETCHQUEUE)) {
            return url.replace(YY_FETCHQUEUE, WebConstants.EMPTY_STR);
        }

        String temp = url.replace(YY_RETURN_DATA, WebConstants.EMPTY_STR);
        String[] functionAndData = temp.split(WebConstants.SPLIT_CHARACTER);

        if(functionAndData.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < functionAndData.length; i++) {
                sb.append(functionAndData[i]);
            }

            return sb.toString();
        }

        return null;
    }

    public static String getFunctionFromReturnUrl(String url) {
        String temp = url.replace(YY_RETURN_DATA, WebConstants.EMPTY_STR);
        String[] functionAndData = temp.split(WebConstants.SPLIT_CHARACTER);

        if(functionAndData.length >= 1){
            return functionAndData[0];
        }

        return null;
    }

    /**
     * js 文件将注入为第一个script引用
     * @param view
     * @param url
     */
    public static void webViewLoadJs(WebView view, String url){
        String js = "var newscript = document.createElement(\"script\");";
        js += "newscript.src=\"" + url + "\";";
        js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);";
        view.loadUrl("javascript:" + js);
    }

    /**
     * Load a local JS file in the webview. The file is defined by his path.
     *
     * @param view The webview in which we will load the JS file.
     * @param path The path to the file.
     */
    public static void webViewLoadLocalJs(WebView view, String path){
        //Read the file from the assets and get its content.
        String jsContent = assetFile2Str(view.getContext(), path);

        //Load the content in the webview.
        view.loadUrl(JAVASCRIPT_STR + jsContent);
    }

    public static String assetFile2Str(Context c, String urlStr){
        InputStream in = null;
        try{
            in = c.getAssets().open(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();
            in.close();

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //Do your job.
                }
            }
        }

        return null;
    }

    //endregion
}
