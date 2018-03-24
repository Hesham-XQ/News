package com.example.android.news;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;
import android.content.Context;

import java.util.List;

/**
 * Created by SAMO on 3/23/2018.
 */

public class newsLoader extends AsyncTaskLoader<List<News>> {

    private String urlString;
    // List with the data, held in cache memory of the loader
    private List<News> listInCacheMemory;

    public newsLoader(Context context , String mUrlString) {
        super(context);
        urlString = mUrlString;
    }

    @Override
    protected void onStartLoading() {
        // if we have no data in cache -> we kick off loading it
        if (listInCacheMemory == null) {
            forceLoad();
        } else {
            deliverResult(listInCacheMemory);
        }
    }


    @Override
    public List<News> loadInBackground() {
        // Check if input Url string is not null or empty.
        // If that is the case, AsyncTask returns null
        if (urlString == null) {
            return null;
        }
        List<News> newsList = QueryUtils.getDataFromHttp(urlString);
        return newsList;
    }

    @Override
    public void deliverResult(List<News> data) {
        listInCacheMemory = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }
}
