package org.fruct.oss.tourme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ArticleActivity extends Activity {

	private String articleId = null;
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		
		// Get article Id\name\URL from intent
		Intent intent = getIntent();
		articleId = intent.getStringExtra("ARTICLE_ID");
		Log.i(ConstantsAndTools.ARTICLE_ID, articleId); // FIXME: if no Id passed?
		
		// And load it to the webView
		setupWebView(articleId);
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
        webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        
        //Wait for the page to load then send the location information
        webView.setWebViewClient(new BrowserWebViewClient());
        webView.loadUrl(URL);
        
        // Connect java and js by interface
        webView.addJavascriptInterface(new JavaScriptInterface(this), "android");
	}
	
	 /* 
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
    }
    
    /* 
     * Javascript interaction interface     * 
     * To use it, in JS write "android.functionname()"
     */
    @SuppressWarnings("unused")
    private class JavaScriptInterface {
    	Context context;
    	
    	JavaScriptInterface (Context c) {
    		context = c;
    	}
    	
    	// Write your functions here

	    public void sendToast(String message) {
	    	Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	    } 
	  }
    
    /*
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
