package com.wgl.androidandjs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView1);
        mProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //设置WebView支持JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/ppw.html");
        //在js中调用本地java方法
        webView.addJavascriptInterface(new JsInterface(this), "AndroidWebView");
        //帮WebView处理事件
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("wapIndex/home")) {
                    finish();
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        //添加客户端支持
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                //由于是本地的H5，瞬间加载完毕。为了达到加载进度条效果，这里添加一些代码，实际应用中不需要

                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    /*
                     * if (View.INVISIBLE == mProgressBar.getVisibility()) {
					 * mProgressBar.setVisibility(View.VISIBLE); }
					 */
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {

                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }
        });
    }

    private class JsInterface {
        private Context mContext;

        public JsInterface(Context context) {
            this.mContext = context;
        }

        //在js中调用window.AndroidWebView.showInfoFromJs(name)，便会触发此方法。
        @JavascriptInterface
        public void showInfoFromJs(String name) {
            Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
        }
    }

    //在java中调用js代码
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void doClick(View view) {

        //调用js中的函数：showInfoFromJava(msg)
        String msg = "123456";
        webView.loadUrl("javascript:showInfoFromJava('"+msg+"')");
//        webView.loadUrl("javascript:showInfoFromJava()");
    }
}
