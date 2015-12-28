package com.tlemansec.webviewplus.bridge.listeners;

/**
 * Created by thibault on 24/12/2015.
 */
public interface WebViewJavascriptBridge {

    void send(String data);
    void send(String data, CallBackFunction responseCallback);

}
