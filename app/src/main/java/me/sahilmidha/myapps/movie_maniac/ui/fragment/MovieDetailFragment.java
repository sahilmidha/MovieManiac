package me.sahilmidha.myapps.movie_maniac.ui.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.sahilmidha.myapps.movie_maniac.R;
import me.sahilmidha.myapps.movie_maniac.service.controller.ApplicationController;
import me.sahilmidha.myapps.movie_maniac.service.iWebServiceResponseListener;
import me.sahilmidha.myapps.movie_maniac.service.model.details.MovieDetail;
import me.sahilmidha.myapps.movie_maniac.service.model.details.reviews.Reviews;
import me.sahilmidha.myapps.movie_maniac.service.model.details.video.Trailer;
import me.sahilmidha.myapps.movie_maniac.service.processor.MovieDetailDataProcessor;
import me.sahilmidha.myapps.movie_maniac.service.processor.MovieReviewsDataProcessor;
import me.sahilmidha.myapps.movie_maniac.service.processor.MovieTrailerDataProcessor;
import me.sahilmidha.myapps.movie_maniac.service.processor.iDataProcessor;
import me.sahilmidha.myapps.movie_maniac.ui.adapter.ReviewsAdapter;
import me.sahilmidha.myapps.movie_maniac.utils.URLBuilder;


/**
 * This class receives movieId and fetches details of a movie as soon as the fragment becomes visible and populates UI.
 */
public class MovieDetailFragment extends Fragment implements iWebServiceResponseListener {

    private Long movieId;
    private final static String MOVIE_ID = "movieId";
    private String mediaUrl;
    private List<Reviews> _reviewsList;
    private ReviewsAdapter _reviewsAdapter;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        ApplicationController.getInstance().getWebServiceManager().createGetRequest(this, new MovieDetailDataProcessor(), URLBuilder.getMovieDetailsRequest(movieId.toString()).concat(getString(R.string.api_key_theMovieDb)));
        ApplicationController.getInstance().getWebServiceManager().createGetRequest(this, new MovieReviewsDataProcessor(), URLBuilder.getMovieReviewsRequest(movieId.toString()).concat(getString(R.string.api_key_theMovieDb)));
        ApplicationController.getInstance().getWebServiceManager().createGetRequest(this, new MovieTrailerDataProcessor(), URLBuilder.getMovieTrailerRequest(movieId.toString()).concat(getString(R.string.api_key_theMovieDb)));
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
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        _reviewsList = new ArrayList<Reviews>();
        _reviewsAdapter = new ReviewsAdapter(_reviewsList);
        ListView listView = (ListView) view.findViewById(R.id.list_view_reviews);
        listView.setAdapter(_reviewsAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*super.onSaveInstanceState(outState);*/
        //Save movieId in the bundle before the fragment gets destroyed.
        outState.putLong(MOVIE_ID, movieId);
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    public Long getMovieId()
    {
        return movieId;
    }
    public String getMediaUrl()
    {
        return mediaUrl;
    }

    @Override
    public void onWebServiceSuccess(iDataProcessor dataProcessor)
    {
        View view = getView();
        if(view != null)
        {
            if (dataProcessor instanceof MovieDetailDataProcessor)
            {
                MovieDetail movieDetail = ((MovieDetailDataProcessor) dataProcessor).getMovieDetailObject();
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(movieDetail.getTitle());
                TextView textViewDate = (TextView) view.findViewById(R.id.id_textView_release_date);
                textViewDate.setText(movieDetail.getReleaseDate().substring(0, 4));
                TextView textViewRating = (TextView) view.findViewById(R.id.id_textView_rating);
                textViewRating.setText(Float.toString(movieDetail.getVoteAverage()) + "/10");
                TextView textViewPlot = (TextView) view.findViewById(R.id.id_textView_plot);
                textViewPlot.setText(movieDetail.getOverview());

                ImageView imageViewPoster = (ImageView) view.findViewById(R.id.id_imageView_thumbnail);
                String poster_url = imageViewPoster.getContext().getString(R.string.poster_url);
                poster_url = poster_url.concat(movieDetail.getPosterPath());
                Picasso.with(getActivity())
                        .load(poster_url)
                        .resize(185, 278)// Doubt: This doesn't seem to work. In fragment_movie_detail.xml until
                                // I specify actual width and I do wrap_content, I get a small image. Why?
                        .centerCrop()
                        .onlyScaleDown()
                        .into(imageViewPoster);
            }
            else if (dataProcessor instanceof MovieReviewsDataProcessor)
            {
                _reviewsList.clear();
                _reviewsList.addAll(((MovieReviewsDataProcessor)dataProcessor).getMoviesReviewsArrayList());
                _reviewsAdapter.notifyDataSetChanged();
            }

            else if (dataProcessor instanceof MovieTrailerDataProcessor)
            {

                Trailer trailer = ((MovieTrailerDataProcessor) dataProcessor).getMoviesTrailerArrayList().get(0);
                mediaUrl = getString(R.string.youtube_url).concat(trailer.getKey());

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.favorite_key),Context.MODE_PRIVATE);
                String favorite = sharedPreferences.getString(getString(R.string.favorite_key), null);
                if(favorite != null
                        && favorite.contains(getMovieId().toString())){
                    ((CheckBox) view.findViewById(R.id.id_checkbox_mark_favorite)).setChecked(true);
                }
            }
        }
    }

    @Override
    public void onWebServiceFailed()
    {
        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
    }

}
