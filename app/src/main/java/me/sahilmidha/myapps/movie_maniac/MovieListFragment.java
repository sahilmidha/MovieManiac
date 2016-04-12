package me.sahilmidha.myapps.movie_maniac;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * This populates GridView and displays list of movie posters in 2 columns. We want fragments to talk to each other
 * independently so we create an interface MovieListListener here which any activity can implement (and can tell that
 * it's listening)and pass the data to other activity/fragment.
 */
public class MovieListFragment extends Fragment {

    private MoviesAdapter moviesAdapter;
    private List<Movies> moviesList;
    public MovieListFragment() {
        // Required empty public constructor
    }

    //Create an interface which an activity can implement so that it listens to clicks in fragments
    static interface MovieListListener{
        void itemClick(long id);
    };
    //We will initialise this listener in OnAttach method. This is going to be the activity which is going to implement this interface.
    private MovieListListener movieListListener;

    /**
     * This is where the activity gets attached to the fragment. Initiate the listener here.
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.movieListListener = (MovieListListener) activity;
    }

    /**
     * We call FetchMoviesTask here as soon as activity becomes visible and pass a string sortBy - the way user wants to sort the movies
     */
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Doubt here -- when to use below technique to get SharedPreferences
        //SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sortBy = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        new FetchMoviesTask().execute(sortBy);
    }

    /**
     * Inflate the gridView here.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //NOTE: i am using same 2 columns gridView for both phone and tablet. This looks better on tablet too.
        //What if I don't put fragment_movie_list.xml in layout-large? Will it break for the tablet?
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);


        /*
        Initialise the movieList before binding it to the adapter because AsyncTask might not have finished by now
        and therefore might not have populated moviesList. Note: We need to initialise it before The AsyncTask thread runs
        as we can never be sure when the thread stops execution.
        */
        moviesList = new ArrayList<Movies>();
        moviesAdapter = new MoviesAdapter(inflater.getContext(), moviesList);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.id_grid_view_movie_list);
        gridView.setAdapter(moviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movies movies = moviesAdapter.getItem(position);
                if(null != movieListListener) {
                    //Call the MovieListListener's itemClick method here.
                    movieListListener.itemClick(movies.getId());
                }
            }
        });
        return rootView;
    }

    /**
     * This fetches poster_url and movieId
     */
    private class FetchMoviesTask extends AsyncTask<String, Void, List<Movies>> {
        // now we use LOG_TAG variable and assign it like this below so that whenever class name changes, both would be in sync.
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<Movies> doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String sortBy = params[0]+".desc";
            try {
                // Construct the URL for the theMovieDb query

                final String MOVIE_BASE_URL = getString(R.string.base_url_discover_movies);
                final String SORT = "sort_by";
                final String APP_ID = "api_key";
                final String VOTE_COUNT = "vote_count.gte";
                Uri builtUri = null;
                if(sortBy.equalsIgnoreCase(getString(R.string.vote_average_desc))){
                    builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT,sortBy)
                            //we fetch it with minimum vote count so as to get maximum correct data.
                            .appendQueryParameter(VOTE_COUNT,getString(R.string.minimum_vote_count))
                            .appendQueryParameter(APP_ID, getString(R.string.api_movie))
                            .build();
                } else{
                    builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(SORT,sortBy)
                            .appendQueryParameter(APP_ID, getString(R.string.api_movie))
                            .build();
                }

                URL url = new URL(builtUri.toString());
                /*Log.v(LOG_TAG,builtUri.toString());*/
                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();

                /*Log.v(LOG_TAG,moviesJsonStr);*/
                try {
                    return getMoviesDataFromJson(moviesJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error :"+e.getMessage(), e);
                // If the code didn't successfully get the movies data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        /**
         * Take the String representing the movieData in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         */
        private List<Movies> getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String POSTER = "poster_path";
            final String ID = "id";

            moviesList = new ArrayList<Movies>();

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = moviesJson.getJSONArray(RESULTS);

            for(int i = 0; i < resultsArray.length() ; i++) {

                String poster_url;
                String id;
                // Get the JSON object representing the movies
                JSONObject movieData = resultsArray.getJSONObject(i);
                poster_url = movieData.getString(POSTER);
                id = movieData.getString(ID);

                poster_url=getString(R.string.poster_url).concat(poster_url);
                moviesList.add(new Movies(Long.parseLong(id),poster_url));
            }
            /*for (Movies m : moviesList) {
                Log.v(LOG_TAG, "POSTER URL: " + m.toString());
            }*/

            return moviesList;

        }

        /**
         * This gets called after doInBackgrond is finished and we update moviesAdapter with moviesList
         * @param moviesList
         */
        @Override
        protected void onPostExecute(List<Movies> moviesList) {
            //this method runs on main thread
            if(moviesList != null){
                moviesAdapter.clear();
                //This will trigger the ListView to get updated since it is tied to this ArrayAdapter
                //this calls ADAPTER.notifyDataSetChanged() method interally and tells all view attached to it that I have changed so please update yourself
                //This call to notifyDataSetChanged will go if the boolean mNotifyChanged has not been modified in our code. Read Source code of ArrayAdapter.
                moviesAdapter.addAll(moviesList);
            }

        }
    }
}
