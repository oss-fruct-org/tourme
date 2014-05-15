package org.fruct.oss.tourme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Arrays;
import java.util.List;

/**
 * Get array with Wikipedia articles about N points for coordinates
 * in radius for locale * 
 * @author alexander
 *
 */
public class GeTSPoints extends AsyncTask<String, Void, String> {

    public static Context cont = MainActivity.context;

    private double longitude, latitude;
    private int resultsCount, radius;
    private String locale;
    String token;
    int category;

    private DBHelper dbHelper;
    SQLiteDatabase db;

    public Cursor cursor = null; // Cursor to existing items (if items exist)

    public GeTSPoints(Context context, String token, double longitude, double latitude, int radius, String locale, int category) {
        this.token = token;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.locale = locale;
        this.category = category;

        this.dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    // Необходимо получить точки из gets
    // Load points
    //Loads points for circle defined by given: category, radius, latitude and longitude; or radius, latitude and longitude; or category.
    //Request should be http://oss.fruct.org/projects/gets/service/loadPoints.php

    //Request:
    /*
        <request>
        <params>
        <auth_token>...</auth_token>
        <latitude>...</latitude>
        <longitude>...</longitude>sss
                <radius>...</radius>
        <category_id>...</category_id>
        </params>
        </request>
    */
    //  auth_token - auth token string (optional)
    //  latitude - float value
    //  longitude - float value
    //  radius - float non-negative value
    //  category_id - id of category - integer (list of category IDs can be obtained by getCategories request)
    //    Points can be retrieved by: only category_name, or latitude and longitude and radius, or combination of both previous possibilities.

    //Response:
    /*
        <response>
         <status>
        <code>...</code>
        <message>...</message>
        </status>
        <content>
        %list of points in kml format%
        </content>
        </response>
    */


    //Полученные данные необходимо отпарсить к формату json

    // Download and save categorys by token in database
    @Override
    protected String doInBackground(String... urls) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://oss.fruct.org/projects/gets/service/loadPoints.php");

        try {
            StringEntity se = new StringEntity("<request>" +
                    "    <params>" +
                    "        <auth_token>" + this.token + "</auth_token>" +
                    "        <latitude>" + this.latitude + "</latitude>" +
                    "        <longitude>" + this.longitude + "</longitude>" +
                    "        <radius>" + this.radius + "</radius>" +
                    "        <category_id>" + this.category + "</category_id>" +
                    "    </params>" +
                    "</request>", HTTP.UTF_8);


            se.setContentType("text/xml");
            httppost.setEntity(se);

            Log.e("tourme", "-1");
            HttpResponse httpresponse = httpclient.execute(httppost);
            Log.e("tourme", "-2");
            HttpEntity resEntity = httpresponse.getEntity();
            Log.e("tourme", "-3");

            String response = EntityUtils.toString(resEntity);
            Log.e("tourme getspoints", response + "_");



            XmlPullParser xpp = Xml.newPullParser();
            xpp.setInput(new StringReader(response));

            int eventType = xpp.getEventType();



            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {

                    String name = xpp.getName();

                    if (name.equals("Placemark")) {

                        ContentValues cv = new ContentValues();
                        cv.put("service", "gets");


                        xpp.next();
                        name = xpp.getName();



                        if (name.equals("name")) {
                            //Log.e("tourme xpp name", xpp.nextText());
                            cv.put("name", xpp.nextText());
                            xpp.next();
                            name = xpp.getName();
                            Log.e("tourme 00000000000", " " + name);
                        }

                        if (name.equals("description")) {
                            //Log.e("tourme xpp descr", xpp.nextText());
                            cv.put("description", xpp.nextText());

                        }

                        // Go to 'value'
                        for (int i = 0; i < 3; i++)
                            xpp.next();

                        name = xpp.getName();
                        Log.e("tourme value", name);

                        if (name.equals("value")) {
                            //Log.e("tourme xpp value", xpp.nextText());
                            cv.put("url", xpp.nextText());
                            //xpp.next();
                        }

                        for (int i = 0; i < 4; i++)
                            xpp.next();

                        name = xpp.getName();
                        Log.i("tourme name", name + " ");

                        if (name.equals("coordinates")) {

                            String coordinates = xpp.nextText();
                            //Log.e("tourme coordinates", coordinates + " _");
                            List<String> elephantList = Arrays.asList(coordinates.split(","));

                            Log.e("tourme xpp coordis", elephantList.get(0) + " _ " + elephantList.get(1));

                            cv.put("longitude", elephantList.get(0));
                            cv.put("latitude", elephantList.get(1));
                        }


                        //break;
                        //if (eventType == XmlPullParser.END_TAG) {
                        long rowID = db.insert(ConstantsAndTools.TABLE_WIKIARTICLES, null, cv);
                        Log.d("tourme rowid", rowID + " " + cv.toString());
                        //}
                    }
                }

                eventType = xpp.next();
            }



            String[] columns = new String[] {"latitude", "longitude", "name", "url", "description"};
            this.cursor = db.query(true, ConstantsAndTools.TABLE_WIKIARTICLES, columns, null, null, null, null, null, null);
            Log.e("tourme db", cursor.getCount() + " " + cursor.toString());


            //Log.e("tourme gets response", EntityUtils.toString(resEntity) + " ");
            //tvData.setText(EntityUtils.toString(resEntity));
        } catch (ClientProtocolException e) {
            Log.e("tourme getspoints", e.toString() + " ");
        } catch (IOException e) {
            Log.e("tourme getspoints", e.toString() + " ");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
    //Request:
    <request>
    <params>
        <auth_token>...</auth_token>
    </params>
    </request>

    //Response:
    <response>
    <status>
        <code>...</code>
        <message>...</message>
    </status>
    <content>
        <categories>
            <category>
                <id>...</id>
                <name>...</name>
                <description>...</description>
                <url>...</url>
            </category>
        </categories>
    </content>
    </response>
     */

    // Parse JSON file
    @Override
    public void onPostExecute(String result) {
        // Make here tricks with UI (after class extending)
    }
}
