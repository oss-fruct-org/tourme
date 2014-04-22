package org.fruct.oss.tourme;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Подключение к сервису Gets
 * авторищация и получение token
 */
public class GeTSAuthorization extends AsyncTask<String, Void, String> {

    public static Context cont = MainActivity.context;
    public String login;
    public String password;

    private DBHelper dbHelper;

    public Cursor cursor = null; // Cursor to existing items (if items exist)

    public GeTSAuthorization(Context context, String login, String password) {
        this.login = login;
        this.password = password;

        this.dbHelper = new DBHelper(context);
    }

    // Download data from Gets, parse and save
    @Override
    protected String doInBackground(String... urls) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://oss.fruct.org/projects/gets/service/login.php");

        try {
            StringEntity se = new StringEntity( "<request>" +
                    "  <params>" +
                    "    <login>" + this.login + "</login>" +
                    "    <password>" + this.password + "</password>" +
                    "  </params>" +
                    "</request>", HTTP.UTF_8);
            se.setContentType("text/xml");
            httppost.setEntity(se);

            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity resEntity = httpresponse.getEntity();

            return EntityUtils.toString(resEntity);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Parse JSON file
    @Override
    public void onPostExecute(String result) {

        try {
            XmlPullParser xpp = Xml.newPullParser();

            xpp.setInput(new StringReader(result));

            int eventType = 0;
            eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String name = xpp.getName();
                        if (name.equals("auth_token")) {
                            String token = xpp.nextText();
                            Log.e("tourme gets auth token", token);
                            MainActivity.sh.edit().putString(ConstantsAndTools.GETS_TOKEN, token).commit();
                        }
                        break;
                }

                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            Log.e("tourme parser error", e.toString() + " ");
        } catch (IOException e) {
            Log.e("tourme parser IO error", e.toString() + " ");
        }

    }

}
