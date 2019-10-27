package br.net.ccbr.ccbrowser;

import android.app.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.content.*;
import android.net.*;
import android.widget.*;
import android.graphics.*;
import java.net.*;
import android.text.*;
import android.text.style.*;
import android.animation.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.TextView.*;
import java.util.regex.*;
import android.*;
import android.content.pm.*;
import java.util.*;
import android.support.v4.app.*;
import android.view.animation.*;
import android.util.*;
import android.content.res.*;
import android.widget.RelativeLayout.*;
import android.webkit.CookieManager;
import android.annotation.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.*;

public class MainActivity extends AppCompatActivity
{
	private VideoEnabledWebView webView;
	private VideoEnabledWebChromeClient webChromeClient;
	
	private View barbtns;
	
	int i=0;
	private ImageButton setdesk;
	private ImageButton next;
	private String medge = "1";
	
	private ValueCallback<Uri> mUploadMessage;
	public ValueCallback<Uri[]> uploadMessage;
	public static final int REQUEST_SELECT_FILE = 100;
	private final static int FILECHOOSER_RESULTCODE = 1;
	
	private String oldurl = "about:blank";
	// public static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		AdBlocker.init(this);
		
		if(checkAndRequestPermissions()) {
			// carry on the normal flow, as the case of  permissions  granted.
		}
		
		// 
		setdesk = (ImageButton) findViewById(R.id.desktop_image);
		next = (ImageButton) findViewById(R.id.proximobtn);
		
		webView = (VideoEnabledWebView) findViewById(R.id.webView);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAllowContentAccess(true);
		webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 7.0; SaturnoWeb AAA) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.83 Mobile Safari/537.36");
		// webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		webView.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url){
					
					view.loadUrl(url);
					return false;
				}
			});
			
		
		WebView webview = new WebView(this);
		WebSettings ws = webview.getSettings();
		ws.setSaveFormData(false);
		ws.setSavePassword(false);
			
		barbtns = findViewById(R.id.buttonsbar);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
			webView.setOnScrollChangedCallback(new VideoEnabledWebView.OnScrollChangedCallback(){
					public void onScroll(int l, int t, int oldl, int oldt){
						final int xp = oldt + 15;
						final int xpt = oldt - 15;

						if(t> xp){
							//Do stuff

							if(medge == "0") {

							} else if(medge == "3"){

							} else {
								// barbtns.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadeout));

								new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											barbtns.setVisibility(View.GONE);
										}
									}, 150);
								medge = "0";
							}
						} else if(t< xpt) {
							if(medge == "1") {

							} else if(medge == "3"){

							} else {
								barbtns.setVisibility(View.VISIBLE);
								//barbtns.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadein));

								medge = "1";
							}
						}
					}
				}); 
		} else{
			// do something for phones running an SDK before lollipop
		}
			
		registerForContextMenu(webView);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false);
		
		FrameLayout cancel = (FrameLayout) findViewById(R.id.cancel);
		cancel.setVisibility(View.GONE);
		
		/*RelativeLayout bottombtn = (RelativeLayout) findViewById(R.id.setBtn);
		bottombtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
				}
			});
			
		bottombtn.setOnTouchListener(new OnSwipe(getApplicationContext()) {
			public void onSwipeLeft() {
				if (webView.canGoBack()) {
					webView.goBack();
				}
			}

			public void onSwipeRight() {
				if (webView.canGoForward()) {
					webView.goForward();
				}
			}
			
			public void onSwipeTop() {
				webView.loadUrl(webView.getUrl());
			}
			
			public void onSwipeBottom() {
				
			}
		}); */
		
		webView.setWebViewClient(new WebViewClient() {
				private Map<String, Boolean> loadedUrls = new HashMap<>();

				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				@Override
				public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
					boolean ad = false;
					if (!loadedUrls.containsKey(url)) {
						ad = AdBlocker.isAd(url);
						loadedUrls.put(url, ad);
					} else {
						
					}
					return ad ? AdBlocker.createEmptyResource() :
						super.shouldInterceptRequest(view, url);
				}
			});
		
		webView.setDownloadListener(new DownloadListener() {
				@Override
				public void onDownloadStart(String url, String userAgent, String contentDescription,
											String mimetype, long contentLength) {

					DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

					request.allowScanningByMediaScanner();

					request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


					String fileName = URLUtil.guessFileName(url,contentDescription,mimetype);


					request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);

					DownloadManager dManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);


					dManager.enqueue(request);
				}
			});
		
		EditText SearchEditText =(EditText)findViewById(R.id.urlbar); 
		
		Typeface custom_font = Typeface.createFromAsset(getAssets(),  "RobotoSlab-Regular.ttf");


		SearchEditText.setTypeface(custom_font);
		
		SearchEditText.setOnEditorActionListener(new OnEditorActionListener() {
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_GO) {
						

						
						EditText urltext = (EditText) findViewById(R.id.urlbar);
						if(Patterns.WEB_URL.matcher(urltext.getText().toString().toLowerCase()).matches()) {
							String url = urltext.getText().toString();
							if (url.startsWith("http://")) {
								webView.loadUrl(urltext.getText().toString());
							} else if(url.startsWith("https://")) {
								webView.loadUrl(urltext.getText().toString());
							} else {
								webView.loadUrl("http://" + urltext.getText().toString());
							}
						} else {
							webView.loadUrl("https://www.google.com/search?q=" + urltext.getText().toString());
							
						}
						return true;
					}
					return false;
				}
			});
		
		final ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressbar);
	
		// Initialize the VideoEnabledWebChromeClient and set event handlers
		View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
		ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
		View loadingView = getLayoutInflater().inflate(R.layout.activity_main, null); // Your own view, read class comments
		webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
		{
			@Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
			
			protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
			{
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
			}

			@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
			public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
			{
				if (uploadMessage != null) {
					uploadMessage.onReceiveValue(null);
					uploadMessage = null;
				}

				uploadMessage = filePathCallback;

				Intent intent = fileChooserParams.createIntent();
				try
				{
					startActivityForResult(intent, REQUEST_SELECT_FILE);
				} catch (ActivityNotFoundException e)
				{
					uploadMessage = null;
					//Toast.makeText(this, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
					return false;
				}

				return true;
			}

			//For Android 4.1 only
			protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
			{
				mUploadMessage = uploadMsg;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
			}

			protected void openFileChooser(ValueCallback<Uri> uploadMsg)
			{
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
			}
			
			@Override
			public void onProgressChanged(WebView view, int progress)
			{
				if (webView.canGoForward())
				{
					next.setBackgroundResource(R.drawable.proximo);
				} else {
					next.setBackgroundResource(R.drawable.noproximo);
				}
				
				EditText urb = (EditText) findViewById(R.id.urlbar);
			
				if(progress < 100 && progressbar.getVisibility() == ProgressBar.GONE){
					progressbar.setVisibility(ProgressBar.VISIBLE);
					
					EditText urlEdit = (EditText) findViewById(R.id.urlbar);
					urlEdit.clearFocus();
					
					
				}
				progressbar.setProgress(progress);
				if(progress == 50) {
					EditText urlEdit = (EditText) findViewById(R.id.urlbar);
					urlEdit.setText(webView.getUrl(), TextView.BufferType.EDITABLE);
				}

				if(progress == 100) {
					EditText urlEdit = (EditText) findViewById(R.id.urlbar);
					urlEdit.setText(webView.getUrl(), TextView.BufferType.EDITABLE);
					urlEdit.clearFocus();
					
					if(webView.getUrl().equals("data:text/html;charset=utf-8;base64,")){
						urlEdit.setText("about:blank");
					} else if(webView.getUrl().equals("about:blank")) {
						urlEdit.setText("about:blank");
					}
					
					String urllink = webView.getUrl();
					if (urllink.startsWith("tel:") || urllink.startsWith("sms:") || urllink.startsWith("smsto:")
						|| urllink.startsWith("mms:") || urllink.startsWith("mmsto:")) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urllink));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						view.getContext().startActivity(intent);
					}
					
					progressbar.animate()
						.alpha(0.0f)
						.setDuration(300)
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								super.onAnimationEnd(animation);
								progressbar.setAlpha(1.0f);
								progressbar.setVisibility(ProgressBar.GONE);
							}
						});
						
					// progressbar.setVisibility(ProgressBar.GONE);
					setHttpColor();
				}
			}
		};
		
		webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
			{
				@Override
				public void toggledFullscreen(boolean fullscreen)
				{
					// Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
					if (fullscreen)
					{
						getWindow().getDecorView().setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
						
						
						
					} else {
						WindowManager.LayoutParams attrs = getWindow().getAttributes();
						attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
						attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
						
						getWindow().setAttributes(attrs);
						
						// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
						
						if (android.os.Build.VERSION.SDK_INT >= 14)
						{
							getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
						}
					}

				}
			});
		webView.setWebChromeClient(webChromeClient);

		final EditText editText = (EditText) findViewById(R.id.urlbar);

		hideSoftKeyboard(MainActivity.this, editText); // 

		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean hasFocus) {
					if (hasFocus) {
						new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									editText.setSelectAllOnFocus(true);
									editText.selectAll();
								}
							}, 100);
							
						if(webView.getUrl().equals("data:text/html;charset=utf-8;base64,")){
							editText.setText("");
						} else if(webView.getUrl().equals("about:blank")) {
							editText.setText("");
						}
						//lp.setMargins(0, 0, 0, 0);
						//editText.setLayoutParams(lp);

						FrameLayout cancel = (FrameLayout) findViewById(R.id.cancel);
						cancel.setVisibility(View.VISIBLE);

						FrameLayout reload = (FrameLayout) findViewById(R.id.reload);
						reload.setVisibility(View.GONE);

						
						
						if(webView.getUrl().equals("data:text/html;charset=utf-8;base64,")){
							editText.setText("");
						} else if(webView.getUrl().equals("about:blank")) {
							editText.setText("");
						} else {
							final String text = webView.getUrl();
							EditText urb = (EditText) findViewById(R.id.urlbar);
							int count2 = 0;

							for (int i = 0; i < webView.getUrl().length(); i++) {
								if (webView.getUrl().charAt(i) == ' ') {
									count2 = 0;
								} else {
									count2++;
								}
							}

							Spannable modifiedText = new SpannableString(text);
							modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, count2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

							urb.setText(modifiedText);
						}
						
						

					} else {
						// lp.setMargins(108, 16, 32, 16);
						// editText.setLayoutParams(lp);

						FrameLayout cancel = (FrameLayout) findViewById(R.id.cancel);
						cancel.setVisibility(View.GONE);

						FrameLayout reload = (FrameLayout) findViewById(R.id.reload);
						reload.setVisibility(View.VISIBLE);

						setHttpColor();

					}
				}
			});
		
		// Navigate everywhere you want, this classes have only been tested on YouTube's mobile site
		Intent intent = getIntent();
		String url = intent.getExtras().getString("url");
		
		webView.loadUrl(url);
		
		webView.setWebViewClient(new WebViewClient() { 
				@Override public void onReceivedError(WebView view, int errorCode, String description, final String failingUrl) {
					oldurl = failingUrl;
					description = description.replace("net::", "");
					
					if(description.equals("ERR_NAME_NOT_RESOLVED")) {
						showError("Não é possível acessar esse site", description, failingUrl);
						
					} else if(description.equals("ERR_ADDRESS_UNREACHABLE")) {
						showError("O endereço está inacessível", description, failingUrl);
						
					} else if(description.equals("ERR_INTERNET_DISCONNECTED")) {
						showError("Sem conexão com a internet", description, failingUrl);
					
					} else if(description.equals("ERR_CONNECTION_REFUSED")) {
						showError("O servidor recusou a solicitação", description, failingUrl);
						
						
					} else {
						showError("Erro não programado", description, failingUrl);
						
					}
					
					
				} });
	}
	
	private class Browser extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
	@SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
	
	public static interface OnScrollChangedCallback
    {
        public void onScroll(int l, int t, int oldl, int oldt);
    }
	
	public void setdesktop(View v){
		if(i == 0){
			webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
			setdesk.setBackgroundResource(R.drawable.mobile);
			webView.reload();
			i++;
		} else if(i==1) {
			webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 7.0; SaturnoWeb AAA) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.83 Mobile Safari/537.36");
			setdesk.setBackgroundResource(R.drawable.monitor);
			webView.reload();
			i=0;
		}
	}
	
	public void cancel(View v){
		EditText urb = (EditText) findViewById(R.id.urlbar);
		urb.setText("");
	}
	
	public void clear(View v) {
		Toast.makeText(MainActivity.this, "Deletando o histórico e cookies...", Toast.LENGTH_LONG).show();
		clearCookies(getApplicationContext());
		Intent i = new Intent(MainActivity.this, home.class);
		startActivity(i); 
	}
	
	public void reload(View v){
		// webView.reload();
		EditText SearchEditText =(EditText)findViewById(R.id.urlbar); 
		webView.reload();
		
	}
	
	public void next(View v) {
		if (webView.canGoForward())
		{
			webView.goForward();
		}
	}
	
	// Setar as cores
	public void setHttpColor() {
		EditText urb = (EditText) findViewById(R.id.urlbar);
			
		if (urb.getText().toString().startsWith("https://")) {
			
			try
			{
				URL url = new URL(urb.getText().toString());
				String s = url.getHost();

				int count = 0;

				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ' ') {
						count = 0;
					} else {
						count++;
					}
				}

				int count2 = 0;

				for (int i = 0; i < urb.getText().toString().length(); i++) {
					if (urb.getText().toString().charAt(i) == ' ') {
						count2 = 0;
					} else {
						count2++;
					}
				}

				final String text = urb.getText().toString();
				Spannable modifiedText = new SpannableString(text);
				modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray)), 5, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray)), 8 + count, count2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


				urb.setText(modifiedText);
			}
			catch (MalformedURLException e)
			{
				// do something
			}
		} else {
			try
			{
				URL url = new URL(urb.getText().toString());
				String s = url.getHost();

				int count = 0;

				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ' ') {
						count = 0;
					} else {
						count++;
					}
				}

				int count2 = 0;

				for (int i = 0; i < urb.getText().toString().length(); i++) {
					if (webView.getUrl().charAt(i) == ' ') {
						count2 = 0;
					} else {
						count2++;
					}
				}

				final String text = urb.getText().toString();
				Spannable modifiedText = new SpannableString(text);
				modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dark_gray)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray)), 4, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				modifiedText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray)), 7 + count, count2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



				urb.setText(modifiedText);
			}
			catch (MalformedURLException e)
			{
				// do something
			}

		}
	}
	
	
	@Override
	public void onBackPressed()
	{
		// Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
		if (!webChromeClient.onBackPressed())
		{
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				// Close app (presumably)
				super.onBackPressed();
			}
		}
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, 
									ContextMenu.ContextMenuInfo contextMenuInfo){
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        final VideoEnabledWebView.HitTestResult webViewHitTestResult = webView.getHitTestResult();

		if (webViewHitTestResult.getType() == VideoEnabledWebView.HitTestResult.SRC_ANCHOR_TYPE) {
			String link = webViewHitTestResult.getExtra();
			contextMenu.setHeaderTitle(link);

            contextMenu.add(0, 1, 0, "Copiar endereço do link")
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						String link = webViewHitTestResult.getExtra();
						setClipboard(MainActivity.this, link);
						return false;
					}
				});
		}

        if (webViewHitTestResult.getType() == VideoEnabledWebView.HitTestResult.IMAGE_TYPE ||
            webViewHitTestResult.getType() == VideoEnabledWebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

            contextMenu.setHeaderTitle("Imagem");

            contextMenu.add(0, 1, 0, "Abrir imagem")
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {

						String DownloadImageURL = webViewHitTestResult.getExtra();

						if(URLUtil.isValidUrl(DownloadImageURL)){
							webView.loadUrl(DownloadImageURL);
						} else {

						}
						return false;
					}
				});

			contextMenu.add(0, 2, 0, "Fazer download da imagem")
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {

						String DownloadImageURL = webViewHitTestResult.getExtra();

						if(URLUtil.isValidUrl(DownloadImageURL)){

							DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageURL));
							request.allowScanningByMediaScanner();

							request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
							DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
							downloadManager.enqueue(request);
						} else {

						}
						return false;
					}
				});
        }
    }
	
	public void reloader(View v) {
		Toast.makeText(this, "Reload",
					   Toast.LENGTH_LONG).show();
	}
	
	public static void hideSoftKeyboard (Activity activity, View view) 
	{
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}
	
	public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
	
	private  boolean checkAndRequestPermissions() {
		int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
		int locationPermissionTwo = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
		
		List<String> listPermissionsNeeded = new ArrayList<>();
		if (locationPermission != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
		}
		if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		if (locationPermissionTwo != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		}
		if (!listPermissionsNeeded.isEmpty()) {
			ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
			return false;
		}
		return true;
	}

	private void setClipboard(Context context, String text) {
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
			clipboard.setPrimaryClip(clip);
		}
	}
	
	public void showError(String name, String code, String url) {
		AssetManager assetManager = getAssets();
		InputStream input;
		String text = "";

		try {
			input = assetManager.open("errorz.htm");

			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			// byte buffer into a string
			text = new String(buffer);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String htmlData = text + "<p class=\"maintxt\">"+ name +"</p><p class=\"sectxt\">"+ code +"</p><a class=\"update\" href=\""+ url +"\">TENTAR DE NOVO</a></div></body></html>";


		webView.loadUrl("about:blank");
		webView.loadDataWithBaseURL(null,htmlData, "text/html", "UTF-8",null);
		webView.invalidate();
		
	}
}
