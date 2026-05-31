package com.qrscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;

public class MainActivity extends Activity {

    private static final String TAG = "QRScanner";
    private static final String DEFAULT_URL = "https://192.168.45.223:3001/";

    private WebView webView;
    private TextView tvUrl;
    private TextView tvResult;
    private Button btnReload;
    private EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll().build());

        setContentView(R.layout.activity_main);

        tvUrl     = (TextView)  findViewById(R.id.tv_url);
        tvResult  = (TextView)  findViewById(R.id.tv_result);
        btnReload = (Button)    findViewById(R.id.btn_reload);
        etUrl     = (EditText)  findViewById(R.id.et_url);
        webView   = (WebView)   findViewById(R.id.webview);

        // 카메라 런타임 권한 요청
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
        }

        etUrl.setText(DEFAULT_URL);

        setupWebView();
        setupButtons();

        webView.loadUrl(DEFAULT_URL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "카메라 권한 허용됨");
                webView.reload();
            } else {
                Log.e(TAG, "카메라 권한 거부됨");
            }
        }
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.addJavascriptInterface(new AndroidBridge(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "연결 성공: " + url);
                tvUrl.setText("연결됨: " + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Log.e(TAG, "연결 실패: " + description);
                tvUrl.setText("연결 실패: " + description);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d(TAG, "권한 요청: " + Arrays.toString(request.getResources()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                        Log.d(TAG, "권한 허용 완료");
                    }
                });
            }

            @Override
            public boolean onJsAlert(WebView view, String url,
                                     String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(message)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int w) {
                                        result.confirm();
                                    }
                                })
                        .create().show();
                return true;
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage msg) {
                Log.d(TAG, "[JS] " + msg.message());
                return true;
            }
        });
    }

    private void setupButtons() {
        btnReload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inputUrl = etUrl.getText().toString().trim();
                if (!inputUrl.isEmpty()) {
                    tvUrl.setText("연결 시도: " + inputUrl);
                    webView.loadUrl(inputUrl);
                } else {
                    webView.loadUrl(DEFAULT_URL);
                }
            }
        });
    }

    private class AndroidBridge {
        @android.webkit.JavascriptInterface
        public void onScanResult(final String result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    tvResult.setText("스캔: " + result);
                    Toast.makeText(MainActivity.this,
                            "스캔 완료: " + result, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @android.webkit.JavascriptInterface
        public void showToast(final String message) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this,
                            message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @android.webkit.JavascriptInterface
        public String getAppVersion() { return "1.0.0"; }
    }

    @Override protected void onResume()  { super.onResume();  webView.onResume(); }
    @Override protected void onPause()   { super.onPause();   webView.onPause(); }
    @Override protected void onDestroy() { super.onDestroy(); if (webView != null) webView.destroy(); }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}