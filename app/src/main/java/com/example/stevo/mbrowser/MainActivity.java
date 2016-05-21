package com.example.stevo.mbrowser;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

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
    private AutoCompleteTextView editSearchBox;
    private	JsonSearchTask jsonSearch;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

   /*     FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                mWebView.loadUrl("http://www.google.hr");
            }
        });
*/
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
      //  sListView = (ListView)findViewById(R.id.suggestlist);
        m_lang = getResources().getConfiguration().locale.getLanguage();


        if (m_lang == null || m_lang.length() <= 0)
            m_lang = "en";

        mActivity = this;
        webcontainer = (FrameLayout) findViewById(R.id.frame_bar);
        mWebView =(WebView)findViewById(R.id.webView);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mProgress.setVisibility(View.GONE);
        mPackageName = mActivity.getPackageName();


        editSearchBox=(AutoCompleteTextView) findViewById(R.id.editSearch);
        editSearchBox.setThreshold(1);
        editSearchBox.addTextChangedListener(new TextWatcher(){

            public void afterTextChanged(Editable s){
                String newText=s.toString();
                if (newText.length() > 0 & !(newText.startsWith("http")) & !(newText.startsWith("www")))
                {
                    drawer.closeDrawer(GravityCompat.START);
                 //   if (sListView.getParent() == null)drawer.addView(sListView);
                    try
                    {
                        if (jsonSearch != null)
                        {

                            if (jsonSearch.getStatus() == AsyncTask.Status.RUNNING)
                            {
                                jsonSearch.cancel(true);
                            }
                        }//not  null

                        jsonSearch = new JsonSearchTask(mActivity, newText, editSearchBox);
                        jsonSearch.execute(newText);

                        //  return true;
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(mActivity, e.toString(), Toast.LENGTH_SHORT).show();
                        //   return false;
                    }
                }
                  else return;
            }

            public void beforeTextChanged(CharSequence s, int start,int count,int after){}
            public void onTextChanged(CharSequence s, int start,int before,int count){
                // searchQ(s.toString());
            }

        });
        editSearchBox.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
                        if (actionId== EditorInfo.IME_ACTION_SEARCH) {
                            searchQ(v.getEditableText().toString());


                        }
                        return false;
                    }
                });
      /*  editSearchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                searchQ(arg0.getItemAtPosition(arg2).toString());
            }
            });
            */
        initializeWebView();

    }

    private void initializeWebView() {
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
        mSettings.setJavaScriptEnabled(true);

        //multi tab
        mSettings.setJavaScriptCanOpenWindowsAutomatically(false);

        mSettings.setUseWideViewPort(true);
        mSettings.setLoadWithOverviewMode(true);
        mSettings.setSupportZoom(true);
        mSettings.setBuiltInZoomControls(true);
        mSettings.setDisplayZoomControls(false);
        if (API < 18) mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
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


        // onTouch
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            int mLocation = 0;
            int eventY = 0;
            int mAction = 0;
            long mTime = 0;
            int currY = 0;
            int oldY = 0;

            @Override
            public boolean onTouch(final View view, MotionEvent event) {
              //  if (!AutoHide) return false;
                if (view != null && !view.hasFocus()) {
                    view.requestFocus();
                }

                //	final	int actBarHeight=mActivity.getActionBar().getHeight();
                mAction = event.getAction();


                //Down

                if (mAction == MotionEvent.ACTION_DOWN) {
                    eventY = (int) event.getY();
                    mLocation = eventY;
                    mTime = System.currentTimeMillis();
                    oldY = view != null ? view.getScrollY() : 0;

                }

                //UP
                else if (mAction == MotionEvent.ACTION_UP || mAction == MotionEvent.ACTION_CANCEL) {
                    boolean actBarShow = toolbar.isShown();  //mActivity.isActionBarShowing();
                    boolean actModeShow = toolbar.hasExpandedActionView(); //mActivity.isActionModeShowing();

                    TypedValue tv = new TypedValue();
                    mActivity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
                    int actBarHeight = mActivity.getResources().getDimensionPixelSize(tv.resourceId);

                    currY = view != null ? view.getScrollY() : 0;
                    eventY = (int) event.getY();

                    //Show
                    if (!actBarShow & (System.currentTimeMillis() - mTime) > 130 & ((currY < actBarHeight & (eventY - mLocation) > 30) || (currY > actBarHeight & (oldY - currY) > actBarHeight)) & !actModeShow) {
                       getSupportActionBar().show(); // mActivity.showActionBar();

                        assert view != null;
                        view.scrollTo(view.getScrollX(), currY + actBarHeight);

                    }
                    //Hide
                    else if (actBarShow & (mLocation - eventY) > 30 & (currY > actBarHeight || currY == 0) & !actModeShow) {
                        if ((System.currentTimeMillis() - mTime) > 160) {
                           getSupportActionBar().hide();;
                            // mActivity.hideActionBar();
                            assert view != null;

                            if (ifNoBottom()) {
                                view.scrollTo(view.getScrollX(), view.getScrollY() - actBarHeight);
                            }
                        }

                    }
                    mTime = 0;
                    mLocation = 0;

                }

                return false;
            }

        });
    }
    //end ontouch
    boolean ifNoBottom() {
        TypedValue tv = new TypedValue();
        mActivity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);

        int actBarHeight = mActivity.getResources().getDimensionPixelSize(tv.resourceId);
        int height = (int) Math.floor(mWebView.getContentHeight() * mWebView.getScale());
        int webViewHeight = mWebView.getMeasuredHeight();
        if((mWebView.getScrollY() + webViewHeight) >=( height-actBarHeight))
            return false;
        else return true;
    }

    private void searchQ(String query)
    {
     //   mDrawerLayout.closeDrawer(mDrawer);

/*
        if (!mWebView.isShown())
        {
            showTab(mCurrentView);
        }

        if (sListView.getParent() != null)mDrawerLayout.removeView(sListView);
*/
        if (query.startsWith("http://") || query.startsWith("https://") || query.startsWith("file:///"))
        {
            mWebView.loadUrl(query);
        }
        else if (query.startsWith("www.") || query.startsWith("192."))
        {
            mWebView.loadUrl("http://" + query);
        }
        else if (query.startsWith("m."))
        {
            mWebView.loadUrl("http://" + query);
        }
        else
        {
            mWebView.loadUrl("http://www.google.com/search?&source=android-browser&hl=" + m_lang + "&q=" + query);
        }//end show
        //if (searchView != null)
        //{
        //searchView.setQuery(query, false);
        //searchView.clearFocus();
        //}

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
   private class JsonSearchTask extends AsyncTask<String, Void, Cursor>
    {

        private	String search_item="";
        private Context context;
        //private	ListView sListView;
        private	AutoCompleteTextView editSearchBox;
        private MatrixCursor cursor;
        private String m_lang;

        JsonSearchTask(Context context, String item, AutoCompleteTextView editSearchBox)
        {
            try
            {
                this.context = context;
                //this.sListView = sListView;
                this.editSearchBox = editSearchBox;
                search_item = URLEncoder.encode(item, "utf-8");
		/*
			InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
			InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
			String localeString = ims.getLocale();
			Locale locale = new Locale(localeString);
			m_lang = locale.getLanguage();
		*/
                m_lang = context.getResources().getConfiguration().locale.getLanguage();


                if (m_lang == null || m_lang.length() <= 0)
                    m_lang = "en";

                String[] COLUMNS = {
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1
                };
                cursor = new MatrixCursor(COLUMNS);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected Cursor doInBackground(String... arg0)
        {
            try
            {
                String	result = sendUrlQuery(search_item);
                JSONArray city= new JSONArray(result);
                JSONArray parsedResult =  city.getJSONArray(1);

                for (int i=0;i < 6;i++)
                {
                    if (isCancelled())
                    {  return null; }
                    String citi =parsedResult.getString(i);
                    cursor.addRow(new String[]{ String.valueOf(i + 1), citi});
                }

            }
            catch (JSONException | IOException e)
            {
                e.printStackTrace();
            }
            return cursor;
        }
        private String sendUrlQuery(String query) throws IOException
        {
            String result = "";

            URL downUrl = new URL(
                    "http://google.com/complete/search?q=" + query
                            + "&client=android&hl=" + m_lang + "&oe=utf-8");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(downUrl.openStream()));

            String line ;
            while ((line = bufferedReader.readLine()) != null)
            {
                result += line;
            }
            bufferedReader.close();


            return result;
        }
        @Override
        protected void onPostExecute(Cursor cursor)
        {

            SuggestionsListAdapter mSuggestionsAdapter = new  SuggestionsListAdapter(context, cursor);

            //	sListView.setAdapter(mSuggestionsAdapter);
            editSearchBox.setAdapter(mSuggestionsAdapter);
            mSuggestionsAdapter.notifyDataSetChanged();
            super.onPostExecute(cursor);
        }
        private class SuggestionsListAdapter extends CursorAdapter
        {
            private    class   ViewHolder
            {
                TextView   textSuggest;
                ImageView arrowSuggest;

            }
            public SuggestionsListAdapter(Context context, Cursor c)
            {
                super(context, c, 0);

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor)
            {
                ViewHolder holder  =   (ViewHolder)    view.getTag();
                final int textIndex1 = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);

                final	String ts=cursor.getString(textIndex1);
                holder.textSuggest.setText(ts,TextView.BufferType.EDITABLE);
                holder.textSuggest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if (editSearchBox != null)
                        {
                            editSearchBox.setText(ts);
                            searchQ(ts);
                            mWebView.requestFocus();
                            editSearchBox.dismissDropDown();



                        }

                    }
                });
                holder.arrowSuggest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if (editSearchBox != null)
                        {
                            editSearchBox.setText(ts);

                        }

                    }
                });
            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent)
            {
                View   view    =   LayoutInflater.from(context).inflate(R.layout.suggest_row,  null);
                ViewHolder holder  =   new ViewHolder();
                holder.textSuggest    =   (TextView)  view.findViewById(R.id.textSuggest);
                holder.arrowSuggest    =   (ImageView)  view.findViewById(R.id.arrowSuggest);
                view.setTag(holder);

                return view;
            }
        }

    }// End Json class
}
