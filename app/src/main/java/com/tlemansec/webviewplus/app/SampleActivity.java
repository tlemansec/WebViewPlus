package com.tlemansec.webviewplus.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.tlemansec.webviewplus.WebViewPlus;
import com.tlemansec.webviewplus.WebViewPlusClient;
import com.tlemansec.webviewplus.utils.WebUtils;
import com.tlemansec.webviewplus.app.R;

import java.net.MalformedURLException;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Thibault on 26/06/15.
 */
public class SampleActivity extends Activity {

    //region Attributes

    @Bind(R.id.webview) WebViewPlus mWebView;

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                WebUtils.backToRootHistory(mWebView);

                return true;
            }

            return false;
        }
    };

    private final String TAG = SampleActivity.class.getSimpleName();

    //endregion


    //region Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_sample_activity);
        ButterKnife.bind(this);

        try {
            Map<String, String> parameters = WebUtils.getQueryParams("https://www4.integ.fdj.fr/mobiles/jouer/loto/?app=appmobi&game=loto&appName=LOTO_android_MOB");
            int i = 0;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mWebView.initializeDefaultWebView();

        //Use the back stack of a webview when user presses back button.
        mWebView.setOnKeyListener(mOnKeyListener);

        mWebView.setWebViewClient(new WebViewPlusClient());
        mWebView.loadUrl("https://www.youtube.com/watch?v=J-j9ulNibZk");
    }

    //endregion


    //region Utils



    //endregion
}
