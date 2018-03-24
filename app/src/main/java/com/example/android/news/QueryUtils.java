package com.example.android.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom class that holds static variables and methods required by other classes & activities of the app.
 */

public final class QueryUtils {

    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_SECTION = "sectionName";
    private static final String KEY_DATE = "webPublicationDate";
    private static final String KEY_TITLE = "webTitle";
    private static final String KEY_WEB_URL = "webUrl";

    // String constants used:
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name AppUtilities (and an object instance of AppUtilities is not needed).
     */
    private QueryUtils() {
    }

    ////
    /* Utility methods that are used to implement an http query and read information from it:
    ////
    /**
     * Executes calls to helper methods and returns a List of Book objects
     * @param stringWithHttpQuery string that contains URL query
     * @return List<Book> a List of Book objects
     */
    static List<News> getDataFromHttp(String stringWithHttpQuery) {
        String JSONString = "";
        List<News> newsList = new ArrayList<>();

        // check whether input String is valid
        if (stringWithHttpQuery == null) {
            return newsList;
        } else {
            // call a method that transforms String into Url
            URL url = createUrl(stringWithHttpQuery);

            // get http response as a JSON String
            try {
                JSONString = performHttpConnection(url);


                newsList = extractFromJSONString(JSONString) ;

//                newsList = extractFromJSONString(JSONString);
            } catch (IOException exc_03) {
                Log.e(LOG_TAG, "Http connection was not successful " + exc_03);
            }
        }
        return newsList;
    }



    /**
     * Creates an URL object from an input String
     *
     * @param stringWithHttpQuery string that contains URL query
     * @return URL object
     */
    static private URL createUrl(String stringWithHttpQuery) {
        URL urlWithHttpQuery = null;
        try {
            urlWithHttpQuery = new URL(stringWithHttpQuery);
        } catch (MalformedURLException exc_01) {
            Log.e(LOG_TAG, "The app was not able to create a URL request from the query " + exc_01);
        }
        return urlWithHttpQuery;
    }

    /**
     * Uses URL object to create and execute Http connection, obtains InputStream and calls a helper method to read it
     *
     * @param url URL query
     * @return received JSON response in a String format
     */
    static private String performHttpConnection(URL url) throws IOException {
        String JSONResponse = "";
        if (url == null) {
            return JSONResponse;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            // check whether the connection response code is appropriate (in this case == 200)
            if (httpURLConnection.getResponseCode() == 200) {
                Log.e(LOG_TAG, "we recieeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeved" + httpURLConnection.getResponseCode());
                inputStream = httpURLConnection.getInputStream();
                JSONResponse = readInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Bad response from the server was received - response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException exc_02) {
            Log.e(LOG_TAG, "IOE exception was encountered when trying to connect to http " + exc_02);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JSONResponse;
    }

    /**
     * Reads InputStream and parses it into a String
     *
     * @param stream InputStream
     * @return String
     */
    static private String readInputStream(InputStream stream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (stream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Reads JSONString and extracts relevant data from it
     *
     * @param JSONString - result of the previous http query parsed into String format
     * @return List<Book> a list of Book objects
     */
    static private List<News> extractFromJSONString(String JSONString ) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(JSONString)) {
            return null;
        }

        // Create an empty List<NewsItem>
        List<News> news = new ArrayList<News>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON is
        // formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {
            // Build a list of News objects
            JSONObject baseJSONObject = new JSONObject(JSONString);
            JSONObject responseJSONObject = baseJSONObject.getJSONObject(KEY_RESPONSE);
            JSONArray newsResults = responseJSONObject.getJSONArray(KEY_RESULTS);

            // Variables for JSON parsing
            String section;
            String publicationDate;
            String title;
            String webUrl;
            String contributor = null;
String thumbnail = null;
            for (int i = 0; i < newsResults.length(); i++) {
                JSONObject newsArticle = newsResults.getJSONObject(i);
                // Check if a sectionName exists
                if (newsArticle.has(KEY_SECTION)) {
                    section = newsArticle.getString(KEY_SECTION);
                } else section = null;

                // Check if a webPublicationDate exists
                if (newsArticle.has(KEY_DATE)) {
                    publicationDate = newsArticle.getString(KEY_DATE);
                } else publicationDate = null;

                // Check if a webTitle exists
                if (newsArticle.has(KEY_TITLE)) {
                    title = newsArticle.getString(KEY_TITLE);
                } else title = null;

                // Check if a sectionName exists
                if (newsArticle.has(KEY_WEB_URL)) {
                    webUrl = newsArticle.getString(KEY_WEB_URL);
                } else webUrl = null;

                JSONObject fields = newsArticle.getJSONObject("fields");
                if (fields.has("thumbnail")) {
                  thumbnail = fields.getString("thumbnail");
                    }

                JSONArray tagsArray = newsArticle.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    // Get contributor
                    JSONObject tag = tagsArray.getJSONObject(j);
                    if (tag.has("webTitle")) {
                        contributor = tag.getString("webTitle");
                    } else {
                        contributor = "";
                    }
                }
                if (tagsArray.length() == 0) {
                    contributor = "";
                }


                // Create the News object and add it to the news List.
                News newsList = new News(title,  publicationDate ,section, webUrl ,thumbnail , contributor);
                news.add(newsList);
            }

        } catch (JSONException e) {
            //use a handler to create a toast on the UI thread
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
//                    Context mContext = getClass();
//                    Toast.makeText(mContext, "Problem parsing the Guardian JSON results", Toast
//                            .LENGTH_SHORT)
//                            .show();
                }
            });
            Log.e(LOG_TAG, "Problem parsing the Guardian JSON results", e);
        }
        return news;
    }


    static String formatStringForUrl(String query) {
        String stringFormattedForUrl = null;
        try {
            stringFormattedForUrl = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException exc_05) {
            Log.e(LOG_TAG, "An exception was encountered while trying to convert the search query into URL " + exc_05);
        }
        return stringFormattedForUrl;
    }

}