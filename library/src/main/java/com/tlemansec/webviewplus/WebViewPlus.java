package com.tlemansec.webviewplus;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tlemansec.webviewplus.bridge.DefaultHandler;
import com.tlemansec.webviewplus.bridge.listeners.BridgeHandler;
import com.tlemansec.webviewplus.bridge.listeners.CallBackFunction;
import com.tlemansec.webviewplus.bridge.listeners.WebViewJavascriptBridge;
import com.tlemansec.webviewplus.bridge.models.Message;
import com.tlemansec.webviewplus.bridge.utils.BridgeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by thibault on 23/12/2015.
 */
public class WebViewPlus extends WebView implements WebViewJavascriptBridge {

    //region Attributes

    private Map<String, CallBackFunction> mResponseCallbacks = new HashMap<>();
    private Map<String, BridgeHandler> mMessageHandlers = new HashMap<>();
    private BridgeHandler mDefaultHandler = new DefaultHandler();

    private List<Message> mStartupMessage = new ArrayList<>();

    private long mUniqueId = 0;

    private static final String TAG = WebViewPlus.class.getSimpleName();

    //endegion


    //region Constructors

    /**
     * Simple constructor to use when creating a view from code.
     * It will not be called when you inflate from XML.
     *
     * @param context The Context the view is running in.
     */
    public WebViewPlus(Context context) {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML.
     * This is called when a view is being constructed from an XML file,
     * supplying attributes that were specified in the XML file.
     * This version uses a default style of 0
     *
     * @param context The Context the view is running in.
     * @param attrs A collection of attributes, as found associated with a tag in an XML document.
     */
    public WebViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style.
     * This constructor of View allows subclasses to use
     * their own base style when they are inflating.
     *
     * @param context The Context the view is running in.
     * @param attrs A collection of attributes, as found associated with a tag in an XML document.
     * @param defStyleAttr An attribute in the current theme that contains a reference
     *                     to a style resource that supplies default values for the view.
     */
    public WebViewPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //endregion


    //region Life Cycle

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    //endregion


    //region Utils

    public void initializeDefaultWebView() {
        setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        initializeDefaultWebViewSettings();
        initializeDefaultWebViewProperties();
        setWebViewClient(new WebViewPlusClient());
    }

    /**
     * Initialize various settings for the webview like the zoom, controls or scrollbar.
     */
    public void initializeDefaultWebViewSettings() {
        WebSettings webSettings = getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setLayoutAlgorithm(webSettings.getLayoutAlgorithm().NORMAL);
        webSettings.setDefaultTextEncodingName(WebConstants.UTF_8);
    }

    /**
     * Initialize the very basic properties of the webview.
     */
    public void initializeDefaultWebViewProperties() {
        setInitialScale(90);

        //Specifies the type of layer backing this view.
        if (Build.VERSION.SDK_INT >= 19) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void handlerReturnData(String url) {
        String functionName = BridgeUtils.getFunctionFromReturnUrl(url);
        CallBackFunction f = mResponseCallbacks.get(functionName);
        String data = BridgeUtils.getDataFromReturnUrl(url);

        if (f != null) {
            f.onCallBack(data);
            mResponseCallbacks.remove(functionName);
            return;
        }
    }

    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtils.CALLBACK_ID_FORMAT, ++mUniqueId
                    + (WebConstants.UNDERLINE_CHARACTER + SystemClock.currentThreadTimeMillis()));
            mResponseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    private void queueMessage(Message m) {
        if (mStartupMessage != null) {
            mStartupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    private void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(
                BridgeUtils.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);

        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    public void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtils.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        Log.getStackTraceString(e);
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = mResponseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            mResponseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = mMessageHandlers.get(m.getHandlerName());
                            } else {
                                handler = mDefaultHandler;
                            }
                            handler.handler(m.getData(), responseFunction);
                        }
                    }
                }
            });
        }
    }

    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        mResponseCallbacks.put(BridgeUtils.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            mMessageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }

    //endregion
}
