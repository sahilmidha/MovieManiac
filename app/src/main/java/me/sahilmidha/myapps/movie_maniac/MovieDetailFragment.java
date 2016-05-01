package me.sahilmidha.myapps.movie_maniac;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.sahilmidha.myapps.movie_maniac.service.model.Movie;


/**
 * This class receives movieId and fetches details of a movie as soon as the fragment becomes visible and populates UI.
 */
public class MovieDetailFragment extends Fragment {

    private long movieId;
    private final static String MOVIE_ID = "movieId";
    private Movie movie;
    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        //AsyncTask to fetch movie details.
        new FetchMovieDataTask().execute(movieId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if the fragment was destroyed and recreated(device rotated),
        //consider taking out movieId so that we can bring back the same screen as it was before
        if(null != savedInstanceState){
            movieId = savedInstanceState.getLong(MOVIE_ID);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*super.onSaveInstanceState(outState);*/
        //Save movieId in the bundle before the fragment gets destroyed.
        outState.putLong(MOVIE_ID, movieId);
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    private class FetchMovieDataTask extends AsyncTask<Long, Void, Movie> {
        // now we use LOG_TAG variable and assign it like this below so that whenever class name changes, both would be in sync.
        private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

        /**
         * This method runs as soon as we run execute on FetchMovieDataTask object.
         * It runs on background thread and fetch movie data from theMovieDb and returns the Movies object
         * which is received by onPostExecute which runs after this method is finished.
         * @param params
         * @return
         */
        @Override
        protected Movie doInBackground(Long... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            Long movieId= params[0];
            try {

                StringBuilder MOVIE_BASE_URL = new StringBuilder(getString(R.string.base_url_movie_with_id));
                final StringBuilder MOVIE_URL = MOVIE_BASE_URL.append(movieId).append("?");
                final String APP_ID = "api_key";
                Uri builtUri = Uri.parse(MOVIE_URL.toString()).buildUpon()
                        .appendQueryParameter(APP_ID, getString(R.string.api_movie))
                        .build();


                URL url = new URL(builtUri.toString());
                /*Log.v(LOG_TAG,builtUri.toString());*/
                // Create the request to TheMovieDb, and open the connection
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
                movieJsonStr = buffer.toString();

                /*Log.v(LOG_TAG,moviesJsonStr);*/
                try {
                    return getMovieDataFromJson(movieJsonStr);
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
         * Take the String representing the complete movieData in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * */
        private Movie getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String POSTER = "poster_path";
            final String ID = "id";
            final String TITLE = "original_title";
            final String RELEASE_DATE = "release_date";
            final String PLOT = "overview";
            final String RATING = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);

            String poster_url;
            long id;
            String title;
            String releaseDate;
            String plot;
            float rating;

            poster_url = getString(R.string.poster_url).concat(movieJson.getString(POSTER));
            id = movieJson.getLong(ID);
            title = movieJson.getString(TITLE);
            releaseDate = movieJson.getString(RELEASE_DATE);
            plot = movieJson.getString(PLOT);
            rating = movieJson.getLong(RATING);

            //movie = new Movie(id, poster_url, title, plot, rating, releaseDate);

            /*for (Movies m : moviesList) {
                Log.v(LOG_TAG, "POSTER URL: " + m.toString());
            }*/

            return movie;
        }

        /**
         * This would run when doInBackground is finished. It receives the Result.
         * We update the views here after we have all the data with us.
         * @param movie
         */
        @Override
        protected void onPostExecute(Movie movie) {
            //this method runs on main thread
            View view = getView();
            /*if(movie != null
                    && view != null){
                TextView textViewTitle = (TextView) view.findViewById(R.id.id_textView_original_title);
                textViewTitle.setText(movie.getTitle());
                TextView textViewDate = (TextView) view.findViewById(R.id.id_textView_release_date);
                textViewDate.setText(movie.getRelease_date().substring(0,4));
                TextView textViewRating = (TextView) view.findViewById(R.id.id_textView_rating);
                textViewRating.setText(Float.toString(movie.getUser_rating())+"/10");
                TextView textViewPlot = (TextView) view.findViewById(R.id.id_textView_plot);
                textViewPlot.setText(movie.getPlot());
                ImageView imageViewPoster = (ImageView) view.findViewById(R.id.id_imageView_thumbnail);
                Picasso.with(getActivity())
                        .load(movie.getPoster_url())
                        .resize(185,278)// Doubt: This doesn't seem to work. In fragment_movie_detail.xml until
                        // I specify actual width and I do wrap_content, I get a small image. Why?
                        .centerCrop()
                        .onlyScaleDown()
                        .into(imageViewPoster);
            }*/

        }
    }
}
