package com.example.android.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{
    private ProgressBar progressBar;
    private TextView noResultsView;
    static String baseUrl = "https://content.guardianapis.com/search?q= &use-date=published&order-by=newest&show-tags=contributor&show-fields=thumbnail,short-url&api-key=test" ;
    private ArrayList<News>newsArrayList = new ArrayList<News>();
    private ListView newsSearchResultsListView;
    private  NewsAdapter newsAdapter;
private static LoaderManager loaderManager ;
private String searchSection = null ;
DrawerLayout drawerLayout ;
    private String  searchString = "";
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noResultsView = (TextView) findViewById(R.id.empty_state_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        newsSearchResultsListView = (ListView) findViewById(R.id.search_results);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        final ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        // Find reference for navigation drawer list view header
        View header = getLayoutInflater().inflate(R.layout.nav_header, null);
        // Add header to list view
        drawerList.addHeaderView(header);
        // Set array adapter for navigation drawer list view
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.drawer_list_item_layout, getResources().getStringArray(R.array.navigation_drawer_list));
        // Populate navigation drawer list view
        drawerList.setAdapter(adapter);



        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Show message for fetching data
            noResultsView.setText("please waaaaaaait");

            // Initialize Loader and News Adapter
            initializeLoaderAndAdapter();

        } else {
            // Display error
            noResultsView.setText("nooooooooooooooooo0o0o0o");
        }





        newsSearchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                News selectedArticle = newsAdapter.getItem(position);
                String ur = selectedArticle.getUrl();

                Uri newsUri = Uri.parse(ur);
                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }





//            // Handles the row being being clicked
//            @Override
//            public void onClick(View view) {
//                int position = getAdapterPosition();
//                News news = mNews.get(position);
//
//                // Get the Url from the current NewsItem
//                mURL = news.getWebURL();
//
//                // Convert the String URL into a URI object (to pass into the Intent constructor)
//                Uri newsURI = Uri.parse(mURL);
//                // Create new intent to view the article's URL
//                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsURI);
//                // Start the intent
//                context.startActivity(websiteIntent);
//            }

        });
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 1:
                        searchSection = null;
                        // Close drawer layout
                        drawerLayout.closeDrawer(drawerList);
                        // Show progressbar, hide list view while loading
                        progressBar.setVisibility(View.VISIBLE);
                        newsSearchResultsListView.setVisibility(View.GONE);
                        // Set action bar text
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(getString(R.string.fresh_news));
                        }

                        if (networkInfo.isConnected()) {
                            handleQuery(searchString);
                        } else {
                            newsSearchResultsListView.setEmptyView(noResultsView);
                            if (newsAdapter != null) {
                                newsAdapter.clear();
                            }
                            progressBar.setVisibility(View.GONE);
                            noResultsView.setText(getString(R.string.no_internet_connection_message));
                        }
                        break;
                    // At defined position call handle method
                    case 2:
                 //   baseUrl = getString(R.string.base_url ) + getString(R.string.url_end) + getString(R.string.search_section) + getString(R.string.world) ;

                   handleNavigationDrawerClick(getString(R.string.world), drawerLayout, drawerList);

                        break;
                    case 3:
                        handleNavigationDrawerClick(getString(R.string.sport), drawerLayout, drawerList);
                        break;
                    case 4:
                        handleNavigationDrawerClick(getString(R.string.football), drawerLayout, drawerList);
                        break;
                    case 5:
                        handleNavigationDrawerClick(getString(R.string.culture), drawerLayout, drawerList);
                        break;
                    case 6:
                        handleNavigationDrawerClick(getString(R.string.business), drawerLayout, drawerList);
                        break;
                    case 7:
                        handleNavigationDrawerClick(getString(R.string.fashion), drawerLayout, drawerList);
                        break;
                    case 8:
                        handleNavigationDrawerClick(getString(R.string.technology), drawerLayout, drawerList);
                        break;
                    case 9:
                        handleNavigationDrawerClick(getString(R.string.travel), drawerLayout, drawerList);
                        break;
                    case 10:
                        handleNavigationDrawerClick(getString(R.string.money), drawerLayout, drawerList);
                        break;
                    case 11:
                        handleNavigationDrawerClick(getString(R.string.science), drawerLayout, drawerList);
                        break;
                }
            }
        });
        // Setup navigation drawer action bar toggle
        setupDrawer();
        // Display button for navigation drawer
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }



    private void setupDrawer() {
        // Set toggle button
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            // Called when navigation bar is open
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                searchString = "";
                // Set title text
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.navigation);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when navigation bar is closed
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Set title text
                if (getSupportActionBar() != null) {
                    if (searchSection != null) {
                        if (searchString.equals("")) {
                            getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
                        } else {
                            getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
                        }
                    } else {
                        getSupportActionBar().setTitle(getString(R.string.fresh_news) + searchString);
                    }
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Handle toggle button view
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }



    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        return new newsLoader(this , baseUrl) ;

       // return new newsLoader(this , baseUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsItems) {
        if (newsItems != null && !newsItems.isEmpty()) {
            newsAdapter.addAll(newsItems);

            // Hide loading indicator because the data has been loaded
            progressBar.setVisibility(View.GONE);
            // Hide message text
            noResultsView.setText("");

        } else {
            // Set message text to display "No articles found!"
            noResultsView.setText("no articles to show");
            progressBar.setVisibility(View.GONE);
        }
        Log.v("MainActivity", "Loader completed operation!");
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }

    public void initializeLoaderAndAdapter() {
        // Get a reference to the LoaderManager, in order to interact with loaders.
       loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(1, null,  this);

        // Lookup the recyclerView in activity layout
      //  RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // Create adapter passing the data
        newsAdapter = new NewsAdapter(this , new ArrayList<News>());
        //newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        // Attach the adapter to the recyclerView to populate items

        newsSearchResultsListView.setAdapter(newsAdapter);
//        recyclerView.setAdapter(newsAdapter);
//
//        BookListAdapter adapterForSearchResults = new BookListAdapter(this, 0, data);
//
//        // connect the adapter with the root List layout & with the ArrayList data
//        bookSearchResultsListView.setAdapter(adapterForSearchResults);
        // Set layout manager to position the items
      //  recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }


    private void handleNavigationDrawerClick(String sString, DrawerLayout dLayout, ListView dList) {
        searchSection = sString;

        dLayout.closeDrawer(dList);
        // Show progressbar, hide list view while loading
        progressBar.setVisibility(View.VISIBLE);
  //      newsSearchResultsListView.setVisibility(View.GONE);
        // Set action bar text
        if (getSupportActionBar() != null) {
            if (searchString.equals("")) {
                getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
            } else {
                getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
            }
        }
        initializeLoaderAndAdapter();

    }

    private void handleQuery(String searchStringQ) {

        noResultsView.setVisibility(View.GONE);
        // Select URL
        String url;
        if (searchSection == null) {
            url = getString(R.string.base_url) + searchStringQ  + getString(R.string.url_end);
        } else {
            url = getString(R.string.base_url) + getString(R.string.url_end) + getString(R.string.url_section) + searchSection ;
        }
        // Restart the loader
        Bundle args = new Bundle();
        args.putString("uri", url);
        loaderManager.restartLoader(1 , args , this) ;
        // Set List view to top
        newsSearchResultsListView.smoothScrollToPosition(0);
    }



    private boolean checkNetworkConnection() {
        // Check network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}

