package org.fruct.oss.tourme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class ArticleActivity extends Activity {

	private String articleId = null;
	private String articleTitle = null;
	private Bundle articleCoords;
	
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Get article Id\name\URL from intent
		Intent intent = getIntent();
		articleId = intent.getStringExtra(ConstantsAndTools.ARTICLE_ID); // FIXME: if no Id passed?
		articleTitle = intent.getStringExtra(ConstantsAndTools.ARTICLE_TITLE);
		
		articleCoords = intent.getBundleExtra(ConstantsAndTools.ARTICLE_COORDINATES);
		
		setTitle(articleTitle);

		// And load it to the webView
		setupWebView(articleId);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case(android.R.id.home):
				finish();
				break;
		case(R.id.article_activity_show_on_map):
			// TODO: open Map and center on selected point
			// 1. put LatLon & fragment id to intent
			// 2. open activity & set args for fragment
			// 3. open fragment by fragment id there
			// 4. handle args in fragment
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra(ConstantsAndTools.ARTICLE_COORDINATES, articleCoords);
			i.putExtra(ConstantsAndTools.ARTICLE_TITLE, articleTitle);
			//i.putExtra(ConstantsAndTools.FRAGMENT_TO_OPEN, "map");
			startActivity(i);
			
			break;
		}
		
		return true;		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_article, menu);
		return true;
	}

	/*
	 * Set up webView instance
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void setupWebView(String URL) {
		webView = (WebView) findViewById (R.id.articleWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        
        //Wait for the page to load then send the location information
        webView.setWebViewClient(new BrowserWebViewClient());
        webView.setWebChromeClient(new BrowserWebChromeClient());
        webView.loadUrl(URL);
        
        // Connect java and js by interface
        //webView.addJavascriptInterface(new JavaScriptInterface(this), "android");
	}

	/** 
     * Web client for opening external links (domain != wikipedia.org TODO) in external browser,
     * not in webView (preserves for Internet surfing via our webView)
     */
    private class BrowserWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //if (Uri.parse(url).getHost().equals("wikipedia.org"))	// FIXME FOR LOCAL FILES   
            if (url.contains("wikipedia.org"))
            	return false;
            
            // Start another activity if URL is not for our domain
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;            
        }        
        
        @Override
        public void onPageFinished(WebView view, String url) {       	
        	
        	// Hide Wikipedia's searchbar
            // Not for 4.4
            if (Build.VERSION.SDK_INT < 19) {
                webView.loadUrl("javascript:document.getElementsByClassName('header')[0].style.display='none';");
                webView.loadUrl("javascript:document.getElementsByClassName('pre-content')[0].style.display='none';");
            }

        	String webViewTitle = view.getTitle();
        	if (webViewTitle != null) {
        		try {
	        		int lastDashIndex = webViewTitle.lastIndexOf("—");
	        		webViewTitle = webViewTitle.substring(0, lastDashIndex);
	        		ArticleActivity.this.setTitle(webViewTitle);
        		} catch (Exception e) {
        			ArticleActivity.this.setTitle(webViewTitle);
        		}
        	}
        	ProgressBar pbar = (ProgressBar) findViewById(R.id.article_progressbar);
        	pbar.setVisibility(View.GONE);
        	view.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Hold the page loading progress and show progressbar
     * @author alexander
     *
     */
    private class BrowserWebChromeClient extends WebChromeClient {

    	public void onProgressChanged(WebView webView, int progress) {
    		ProgressBar pbar = (ProgressBar) findViewById(R.id.article_progressbar);
    		pbar.setProgress(progress);
    	}
    }

    /**
     * Back button operates browser's history
     * If we're on the first page of webView, then Back button closes the activity
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (webView.canGoBack() == true){
                    webView.goBack();
                } else {
                    finish();
                }
                
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}