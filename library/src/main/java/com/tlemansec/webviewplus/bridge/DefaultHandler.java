package com.tlemansec.webviewplus.bridge;

import com.tlemansec.webviewplus.bridge.listeners.BridgeHandler;
import com.tlemansec.webviewplus.bridge.listeners.CallBackFunction;


/**
 * Created by thibault on 24/12/2015.
 */
public class DefaultHandler implements BridgeHandler {

    //region Attributes

    private final String TAG = "DefaultHandler";

    //endregion


    //region Life Cycle

    @Override
    public void handler(String data, CallBackFunction function) {
        if(function != null){
            function.onCallBack("DefaultHandler response data");
        }
    }

    //endregion
}