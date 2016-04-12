package me.sahilmidha.myapps.movie_maniac;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This contains only a fragment to display details of a movie. This activity is only called for phone via an Intent passed by MainActivity.
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_ID = "movieId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Our activity will receive the Intent and pass it to DetailFragment
        MovieDetailFragment movieDetailFragment = (MovieDetailFragment) getFragmentManager().findFragmentById(R.id.id_fragment_movie_detail);
        long movieId = (long) getIntent().getExtras().get(MOVIE_ID);
        movieDetailFragment.setMovieId(movieId);
    }
}
