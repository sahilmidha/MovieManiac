package me.sahilmidha.myapps.movie_maniac.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.sahilmidha.myapps.movie_maniac.R;
import me.sahilmidha.myapps.movie_maniac.service.controller.ApplicationController;
import me.sahilmidha.myapps.movie_maniac.service.iWebServiceResponseListener;
import me.sahilmidha.myapps.movie_maniac.service.model.Movie;
import me.sahilmidha.myapps.movie_maniac.service.model.details.MovieDetail;
import me.sahilmidha.myapps.movie_maniac.service.processor.MovieDetailDataProcessor;
import me.sahilmidha.myapps.movie_maniac.service.processor.MovieListDataProcessor;
import me.sahilmidha.myapps.movie_maniac.service.processor.iDataProcessor;
import me.sahilmidha.myapps.movie_maniac.ui.adapter.FavouriteMoviesAdapter;
import me.sahilmidha.myapps.movie_maniac.ui.adapter.MoviesAdapter;
import me.sahilmidha.myapps.movie_maniac.utils.URLBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteMoviesListFragment extends Fragment implements iWebServiceResponseListener, FavouriteMoviesAdapter.OnCardViewClickListener
{


    private FavouriteMoviesAdapter moviesAdapter;
    private List<MovieDetail> moviesList;
    private List<MovieDetail> moviesListUpdated;
    String[] strArray;
    public FavouriteMoviesListFragment()
    {
        // Required empty public constructor
    }

    //Create an interface which an activity can implement so that it listens to clicks in fragments
    public static interface MovieListListener
    {
        void itemClick(Long movieId);
    }

    ;
    //We will initialise this listener in OnAttach method. This is going to be the activity which is going to implement this interface.
    private MovieListListener movieListListener;

    /**
     * This is where the activity gets attached to the fragment. Initiate the listener here.
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.movieListListener = (MovieListListener) activity;
    }

    /**
     * We call FetchMoviesTask here as soon as activity becomes visible and pass a string sortBy - the way user wants to sort the movies
     */
    @Override
    public void onStart()
    {
        super.onStart();
        moviesListUpdated = new ArrayList<MovieDetail>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.favorite_key), Context.MODE_PRIVATE);
        String favorite = sharedPreferences.getString(getString(R.string.favorite_key),null);
        if(favorite != null
                && !favorite.isEmpty()){
            strArray = favorite.split(",");
            for(String str: strArray){
                ApplicationController.getInstance().getWebServiceManager().createGetRequest(this, new MovieDetailDataProcessor(), URLBuilder.getMovieDetailsRequest(str).concat(getString(R.string.api_key_theMovieDb)));

            }
        }else {
            onWebServiceFailed();
        }
    }

    /**
     * Inflate the gridView here.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        //NOTE: i am using same 2 columns gridView for both phone and tablet. This looks better on tablet too.
        //What if I don't put fragment_movie_list.xml in layout-large? Will it break for the tablet?
        View rootView = inflater.inflate(R.layout.fragment_favourite_movies_list, container, false);


        /*
        Initialise the movieList before binding it to the adapter because AsyncTask might not have finished by now
        and therefore might not have populated moviesList. Note: We need to initialise it before The AsyncTask thread runs
        as we can never be sure when the thread stops execution.
        */
        moviesList = new ArrayList<MovieDetail>();
        moviesAdapter = new FavouriteMoviesAdapter(moviesList, this);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.id_recycler_view_fragment_movie_list_favorite);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a grid layout manager
        RecyclerView.LayoutManager LayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(LayoutManager);

        // specify an adapter (see also next example)
        recyclerView.setAdapter(moviesAdapter);

        //If you don't want to implement OnCardViewClickListener above, you can do this here through anonymous class
        /*recyclerView.setAdapter(new MoviesAdapter(moviesList, new MoviesAdapter.OnCardViewClickListener() {
            @Override public void onItemClick(Movie movie) {
                Toast.makeText(getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
            }
        }));*/
        return rootView;
    }

    @Override
    public void onWebServiceSuccess(iDataProcessor dataProcessor)
    {
        if (dataProcessor instanceof MovieDetailDataProcessor)
        {

            MovieDetailDataProcessor processor = (MovieDetailDataProcessor) dataProcessor;
            MovieDetail movieDetail = (MovieDetail)processor.getMovieDetailObject();
            moviesListUpdated.add(movieDetail);

            if (moviesListUpdated.size() == strArray.length)
            {
                moviesList.clear();
                moviesList.addAll(moviesListUpdated);
                moviesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onWebServiceFailed()
    {
            moviesList.clear();
            moviesList.addAll(moviesListUpdated);
            moviesAdapter.notifyDataSetChanged();

        Toast.makeText(getActivity(), getString(R.string.No_favourites), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCardViewItemClick(MovieDetail movie)
    {
        movieListListener.itemClick(movie.getId().longValue());

    }
}
