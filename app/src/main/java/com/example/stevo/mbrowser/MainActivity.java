package com.example.stevo.mbrowser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private	final int API = android.os.Build.VERSION.SDK_INT;
    private	String m_lang;
    private	MainActivity mActivity;
    private FrameLayout webcontainer;
    private ProgressBar mProgress;
    private	String mPackageName;
    private WebView mWebView;
    private	WebSettings mSettings;
    private	String mDefaultUserAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                mWebView.loadUrl("http://www.google.hr");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        m_lang = getResources().getConfiguration().locale.getLanguage();


        if (m_lang == null || m_lang.length() <= 0)
            m_lang = "en";

        mActivity = this;
        webcontainer = (FrameLayout) findViewById(R.id.frame_bar);
        mWebView =(WebView)findViewById(R.id.webView);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mProgress.setVisibility(View.GONE);
        mPackageName = mActivity.getPackageName();
        initializeWebView();

    }

    private void initializeWebView()
    {
        mWebView.setWebChromeClient(new AwbChromeClient());
        mWebView.setWebViewClient(new AwbWebClient());

        mWebView.setDrawingCacheEnabled(true);
        //   mWebView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        mWebView.setAnimationCacheEnabled(true);

        mWebView.setBackgroundColor(mActivity.getResources().getColor(
                android.R.color.white));
        mSettings = mWebView.getSettings();
        mDefaultUserAgent = mSettings.getUserAgentString();

        mSettings.setSupportMultipleWindows(true);
        mSettings.setJavaScriptEnabled( true);

        //multi tab
        mSettings.setJavaScriptCanOpenWindowsAutomatically(false);

        mSettings.setUseWideViewPort(true);
        mSettings.setLoadWithOverviewMode(true);
        mSettings.setSupportZoom(true);
        mSettings.setBuiltInZoomControls(true);
        mSettings.setDisplayZoomControls(false);
        if (API < 18)mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mSettings.setSaveFormData(true);

        mSettings.setAppCacheEnabled(true);
        String appCachePath = mActivity.getCacheDir().getAbsolutePath();
        mSettings.setAppCachePath(appCachePath);
        mSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mSettings.setDomStorageEnabled(true);
        //  mSettings.setAllowFileAccess(true);
        //	mSettings.setAllowContentAccess(true);

        //	mSettings.setDefaultTextEncodingName("utf-8");


		/*		if (API > 15)
		 {
		 mSettings.setAllowFileAccessFromFileURLs(false);
		 mSettings.setAllowUniversalAccessFromFileURLs(false);
		 }
		 */
		/*			if (API < 18)
		 {
		 mSettings.setSavePassword(true);
		 }
		 */

        mWebView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if( mWebView.canGoBack())mWebView.goBack();
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
        //    myIntent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void updateProgress(int n)
    {
        if (!mProgress.isShown())
            mProgress.setVisibility(View.VISIBLE);

        mProgress.setProgress(n);
        if (n == 100)
        {
            mProgress.setVisibility(View.INVISIBLE);
      //      setIsFinishedLoading();
        }
        else
        {
        //    setIsLoading();
        }
    }
    public boolean isShown()
    {
        return mWebView != null &&
                mWebView.isShown();

    }
    private class AwbWebClient extends WebViewClient
    {

        /*
        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, String url)
        {

            if (mAdBlock.isAd(url))
            {
                ByteArrayInputStream EMPTY = new ByteArrayInputStream(
                        "".getBytes());
                return new	WebResourceResponse(
                        "text/plain", "utf-8", EMPTY);

            }
            else	return null;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {

            if (view.isShown())
            {
                mActivity.updateUrl(url);
            }
            mTitle.setFavicon(null);
            mTitle.setThumbnail(null);
            mTitle.setTouchIconUrl(null);
            mActivity.update();

            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            mTitle.setTitle(view.getTitle());
            mActivity.update();
            super.onPageFinished(view, url);

        }
*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (url.startsWith("market://")
                    || url.startsWith("http://play.google.com/store/apps")
                    || url.startsWith("https://play.google.com/store/apps"))
            {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                urlIntent.putExtra(mPackageName + ".Origin", 1);
                mActivity.startActivity(urlIntent);
                return true;
            }
            else if (url.startsWith("http://maps.google.com")
                    || url.startsWith("https://maps.google.com"))
            {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                urlIntent.putExtra(mPackageName + ".Origin", 1);
                mActivity.startActivity(urlIntent);
                return true;
            }
            else if (url.startsWith("https://mega.co.nz"))

            {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                urlIntent.putExtra(mPackageName + ".Origin", 1);
                mActivity.startActivity(urlIntent);
                return true;
            }
            else if (url.contains("tel:") || TextUtils.isDigitsOnly(url))
            {
                mActivity.startActivity(new Intent(Intent.ACTION_DIAL, Uri
                        .parse(url)));
                return true;
            }
        /*    else if (url.contains("mailto:"))
            {
                MailTo mailTo = MailTo.parse(url);
                Intent i = Utils.newEmailIntent(mailTo.getTo(),
                        mailTo.getSubject(), mailTo.getBody(), mailTo.getCc());
                mActivity.startActivity(i);
                view.reload();
                return true;
            }*/
            else if (url.startsWith("intent://"))
            {
                Intent intent;
                try
                {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                }
                catch (URISyntaxException ex)
                {
                    return false;
                }
                if (intent != null)
                {
                    mActivity.startActivity(intent);
                    return true;
                }
                else	return false;
            }
            else if (url.startsWith("vnd.youtube"))
            {
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            else	if (url.endsWith(".swf"))
            {
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            else if (url.endsWith(".mp3"))
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "audio/*");
                view.getContext().startActivity(intent);
                return true;
            }
            else if (url.endsWith(".mp4") || url.endsWith(".3gp"))
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                view.getContext().startActivity(intent);
                return true;
            }
            else if (url.endsWith(".WebM"))
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                view.getContext().startActivity(intent);
                return true;
            }
            else
            {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }
    }

    private class AwbChromeClient extends WebChromeClient
    {
      /*  public void openFileChooser(ValueCallback<Uri> uploadMsg)
        {
            mActivity.openFileChooser(uploadMsg);
        }
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType)
        {
            mActivity.openFileChooser(uploadMsg);
        }
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
        {
            mActivity.openFileChooser(uploadMsg);
        }
*/
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
        {
            final GeolocationPermissions.Callback callbackF = callback;
            final String originF = origin;
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Locations");
            builder.setMessage("Allow app to use your location?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            callbackF.invoke(originF, true, true);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id)
                {
                    callbackF.invoke(originF, false, false);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            if (isShown())
            {
                mActivity.updateProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
/*
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon)
        {
            mTitle.setFavicon(icon);
            mTitle.setThumbnail(icon);
            mActivity.update();
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title)
        {
            mTitle.setTitle(title);
            mActivity.update();
            super.onReceivedTitle(view, title);
        }
        @Override
        public void onReceivedTouchIconUrl(WebView view, String src, boolean precomposed)
        {
            mTitle.setTouchIconUrl(src);
            super.onReceivedTouchIconUrl(view, src, precomposed);
        }


        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg)
        {
            return	mActivity.onCreateWindow(resultMsg);

        }

        @Override
        public Bitmap getDefaultVideoPoster()
        {
            return mActivity.getDefaultVideoPoster();
        }

        @Override
        public View getVideoLoadingProgressView()
        {
            return mActivity.getVideoLoadingProgressView();
        }

        @Override
        public void onHideCustomView()
        {
            mActivity.onHideCustomView();
            //	super.onHideCustomView();
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback)
        {
            mActivity.onShowCustomView(view,
                    callback);
            //	super.onShowCustomView(view, callback);
        }
        */
    }

}
