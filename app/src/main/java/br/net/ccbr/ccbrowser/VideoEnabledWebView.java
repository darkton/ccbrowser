package br.net.ccbr.ccbrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.Map;
import android.webkit.*;
import android.net.*;
import br.net.ccbr.ccbrowser.MainActivity.*;

/**
 * This class serves as a WebView to be used in conjunction with a VideoEnabledWebChromeClient.
 * It makes possible:
 * - To detect the HTML5 video ended event so that the VideoEnabledWebChromeClient can exit full-screen.
 * 
 * Important notes:
 * - Javascript is enabled by default and must not be disabled with getSettings().setJavaScriptEnabled(false).
 * - setWebChromeClient() must be called before any loadData(), loadDataWithBaseURL() or loadUrl() method.
 *
 * @author Cristian Perez (http://cpr.name)
 *
 */
public class VideoEnabledWebView extends WebView
{

	
    public class JavascriptInterface
    {
        @android.webkit.JavascriptInterface
        public void notifyVideoEnd() // Must match Javascript interface method of VideoEnabledWebChromeClient
        {
            // This code is not executed in the UI thread, so we must force that to happen
            new Handler(Looper.getMainLooper()).post(new Runnable()
				{
					@Override
					public void run()
					{
						if (videoEnabledWebChromeClient != null)
						{
							videoEnabledWebChromeClient.onHideCustomView();
						}
					}
				});
        }
    }
	
    private VideoEnabledWebChromeClient videoEnabledWebChromeClient;
    private boolean addedJavascriptInterface;
	
	
	public VideoEnabledWebView(final Context context)
    {
        super(context);
        addedJavascriptInterface = false;
    }

    public VideoEnabledWebView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        addedJavascriptInterface = false;
    }

    public VideoEnabledWebView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        addedJavascriptInterface = false;
    }
	

    

    /**
     * Indicates if the video is being displayed using a custom view (typically full-screen)
     * @return true it the video is being displayed using a custom view (typically full-screen)
     */
    public boolean isVideoFullscreen()
    {
        return videoEnabledWebChromeClient != null && videoEnabledWebChromeClient.isVideoFullscreen();
    }

    /**
     * Pass only a VideoEnabledWebChromeClient instance.
     */
    @Override @SuppressLint("SetJavaScriptEnabled")
    public void setWebChromeClient(WebChromeClient client)
    {
        getSettings().setJavaScriptEnabled(true);

        if (client instanceof VideoEnabledWebChromeClient)
        {
            this.videoEnabledWebChromeClient = (VideoEnabledWebChromeClient) client;
        }

        super.setWebChromeClient(client);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding)
    {
        addJavascriptInterface();
        super.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl)
    {
        addJavascriptInterface();
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @Override
    public void loadUrl(String url)
    {
        addJavascriptInterface();
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders)
    {
        addJavascriptInterface();
        super.loadUrl(url, additionalHttpHeaders);
    }

    private void addJavascriptInterface()
    {
        if (!addedJavascriptInterface)
        {
            // Add javascript interface to be called when the video ends (must be done before page load)
            addJavascriptInterface(new JavascriptInterface(), "_VideoEnabledWebView"); // Must match Javascript interface name of VideoEnabledWebChromeClient

            addedJavascriptInterface = true;
        }
    }

	private OnScrollChangedCallback mOnScrollChangedCallback;
	
	
	@Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangedCallback != null) mOnScrollChangedCallback.onScroll(l, t, oldl, oldt);
    }

    public OnScrollChangedCallback getOnScrollChangedCallback()
    {
        return mOnScrollChangedCallback;
    }
	
	
	
	public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback)
    {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }


    
    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public static interface OnScrollChangedCallback
    {
        public void onScroll(int l, int t, int oldl, int oldt);
    }
	
}

