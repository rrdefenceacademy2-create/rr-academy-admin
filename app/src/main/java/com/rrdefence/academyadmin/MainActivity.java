package com.rrdefence.academyadmin;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.ValueCallback;

/** RR Academy Admin and CBT admin. Online data stays in the existing Firebase project. */
public final class MainActivity extends Activity {
    private static final String SITE_HOST = "rr-academy-management.web.app";
    private static final String ADMIN = "https://rr-academy-management.web.app/student/admin.html?v=14";
    private static final String CBT = "https://rr-academy-management.web.app/student/cbt-admin.html?v=14";
    private static final int FILE_PICKER_CODE = 321;
    private final int navy = Color.rgb(18, 59, 101);
    private final int pale = Color.rgb(232, 241, 251);
    private LinearLayout root;
    private LinearLayout content;
    private WebView browser;
    private ProgressBar progress;
    private Button adminButton;
    private Button cbtButton;
    private ValueCallback<Uri[]> fileCallback;
    private String selectedUrl = ADMIN;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(Color.BLACK);
        buildUi();
        initializeBrowser(state);
    }

    private int dp(int n) { return (int) (getResources().getDisplayMetrics().density * n + 0.5f); }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(12);
        b.setAllCaps(false);
        b.setMinHeight(dp(44));
        b.setPadding(dp(7), 0, dp(7), 0);
        return b;
    }

    private void buildUi() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= 30) {
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                v.setPadding(0, bars.top, 0, bars.bottom);
                return insets;
            });
        }

        TextView title = new TextView(this);
        title.setText("RR DEFENCE ACADEMY  •  BARMER");
        title.setTextColor(Color.WHITE);
        title.setTextSize(15);
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(navy);
        title.setPadding(dp(6), dp(13), dp(6), dp(13));
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setPadding(dp(5), dp(2), dp(5), dp(2));
        nav.setBackgroundColor(pale);
        adminButton = button("Admin Panel");
        cbtButton = button("CBT Control");
        Button refresh = button("↻ Refresh");
        nav.addView(adminButton, new LinearLayout.LayoutParams(0, dp(50), 1));
        nav.addView(cbtButton, new LinearLayout.LayoutParams(0, dp(50), 1));
        nav.addView(refresh, new LinearLayout.LayoutParams(0, dp(50), 1));
        root.addView(nav);

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgress(0);
        root.addView(progress, new LinearLayout.LayoutParams(-1, dp(3)));
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        root.addView(content, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);

        adminButton.setOnClickListener(v -> navigate(ADMIN));
        cbtButton.setOnClickListener(v -> navigate(CBT));
        refresh.setOnClickListener(v -> {
            if (browser != null) browser.reload();
            else initializeBrowser(null);
        });
        updateNav();
    }

    private void updateNav() {
        adminButton.setEnabled(!selectedUrl.contains("admin.html") || selectedUrl.contains("cbt-admin"));
        cbtButton.setEnabled(!selectedUrl.contains("cbt-admin.html"));
    }

    private void navigate(String url) {
        selectedUrl = url;
        updateNav();
        if (browser != null) browser.loadUrl(url);
        else initializeBrowser(null);
    }

    private void initializeBrowser(Bundle state) {
        try {
            if (browser != null) return;
            browser = new WebView(this);
            browser.setBackgroundColor(Color.WHITE);
            WebSettings cfg = browser.getSettings();
            cfg.setJavaScriptEnabled(true);
            cfg.setDomStorageEnabled(true);
            cfg.setAllowFileAccess(false);
            cfg.setAllowContentAccess(true);
            cfg.setAllowFileAccessFromFileURLs(false);
            cfg.setAllowUniversalAccessFromFileURLs(false);
            cfg.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
            cfg.setJavaScriptCanOpenWindowsAutomatically(false);
            cfg.setSupportMultipleWindows(false);
            cfg.setCacheMode(WebSettings.LOAD_DEFAULT);
            if (Build.VERSION.SDK_INT >= 26) cfg.setSafeBrowsingEnabled(true);
            CookieManager.getInstance().setAcceptCookie(true);
            // Auth uses Email/Password; no SMS, camera, contact or storage permission.

            browser.setWebViewClient(new WebViewClient() {
                @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Uri url = request.getUrl();
                    if ("https".equalsIgnoreCase(url.getScheme()) && SITE_HOST.equalsIgnoreCase(url.getHost())) {
                        return false;
                    }
                    if (request.isForMainFrame() && "https".equalsIgnoreCase(url.getScheme())) {
                        try { startActivity(new Intent(Intent.ACTION_VIEW, url)); }
                        catch (ActivityNotFoundException e) { Toast.makeText(MainActivity.this, "ब्राउज़र उपलब्ध नहीं", Toast.LENGTH_LONG).show(); }
                    }
                    return true;
                }
                @Override public void onPageFinished(WebView view, String url) {
                    progress.setVisibility(View.GONE);
                }
                @Override public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError error) {
                    if (req.isForMainFrame()) {
                        Toast.makeText(MainActivity.this, "इंटरनेट जाँचें और Refresh दबाएँ", Toast.LENGTH_LONG).show();
                    }
                }
                @Override public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.cancel();
                    Toast.makeText(MainActivity.this, "सुरक्षित कनेक्शन नहीं बना", Toast.LENGTH_LONG).show();
                }
            });
            browser.setWebChromeClient(new WebChromeClient() {
                @Override public void onProgressChanged(WebView view, int amount) {
                    progress.setVisibility(amount == 100 ? View.GONE : View.VISIBLE);
                    progress.setProgress(amount);
                }
                @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                    if (fileCallback != null) fileCallback.onReceiveValue(null);
                    fileCallback = callback;
                    try {
                        Intent intent = params.createIntent();
                        startActivityForResult(intent, FILE_PICKER_CODE);
                        return true;
                    } catch (Exception ex) {
                        fileCallback = null;
                        callback.onReceiveValue(null);
                        Toast.makeText(MainActivity.this, "फाइल चयन नहीं खुला", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
            });
            content.removeAllViews();
            content.addView(browser, new LinearLayout.LayoutParams(-1, -1));
            if (state == null || browser.restoreState(state) == null) browser.loadUrl(selectedUrl);
        } catch (RuntimeException e) {
            browser = null;
            content.removeAllViews();
            TextView msg = new TextView(this);
            msg.setText("Android System WebView उपलब्ध नहीं है।\nChrome में Admin Panel खोलें।");
            msg.setTextSize(17);
            msg.setPadding(dp(20), dp(35), dp(20), dp(18));
            content.addView(msg);
            Button open = button("Chrome में खोलें");
            content.addView(open);
            open.setOnClickListener(v -> {
                try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(selectedUrl))); }
                catch (Exception ignored) { }
            });
        }
    }

    @Override protected void onActivityResult(int req, int result, Intent data) {
        super.onActivityResult(req, result, data);
        if (req == FILE_PICKER_CODE && fileCallback != null) {
            Uri[] resultUris = WebChromeClient.FileChooserParams.parseResult(result, data);
            fileCallback.onReceiveValue(resultUris);
            fileCallback = null;
        }
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        if (browser != null) browser.saveState(out);
        super.onSaveInstanceState(out);
    }

    @Override public void onBackPressed() {
        if (browser != null && browser.canGoBack()) browser.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (fileCallback != null) { fileCallback.onReceiveValue(null); fileCallback = null; }
        if (browser != null) {
            content.removeView(browser);
            browser.destroy();
            browser = null;
        }
        super.onDestroy();
    }
}
