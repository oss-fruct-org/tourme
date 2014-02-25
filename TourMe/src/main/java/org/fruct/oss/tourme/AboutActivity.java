package org.fruct.oss.tourme;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new AboutFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class AboutFragment extends Fragment {

        public AboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about, container, false);

            try {
                String versionStr = getActivity().getResources().getString(R.string.app_name)
                        + " "
                        + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
                TextView version = (TextView) rootView.findViewById(R.id.about_version);
                version.setText(versionStr);
            } catch (Exception e) {
                Log.e("tourme about", e.toString());
            }

            TextView licenses = (TextView) rootView.findViewById(R.id.about_licenses);
            licenses.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            licenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context cont = getActivity();
                    AlertDialog.Builder alert = new AlertDialog.Builder(cont);
                    alert.setTitle(getResources().getString(R.string.about_licenses));
                    WebView wv = new WebView(cont);

                    wv.loadUrl("file:///android_asset/legal.html");
                    wv.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });

                    alert.setView(wv);
                    alert.setNegativeButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                alert.show();
                }
            });

            return rootView;
        }
    }

}
