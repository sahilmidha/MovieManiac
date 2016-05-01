package me.sahilmidha.myapps.movie_maniac.ui.fragment;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import me.sahilmidha.myapps.movie_maniac.ui.adapter.MoviesAdapter;
import me.sahilmidha.myapps.movie_maniac.R;
import me.sahilmidha.myapps.movie_maniac.service.controller.ApplicationController;
import me.sahilmidha.myapps.movie_maniac.service.iWebServiceResponseListener;
import me.sahilmidha.myapps.movie_maniac.service.model.Movie;
import me.sahilmidha.myapps.movie_maniac.service.processor.MovieListDataProcessor;
import me.sahilmidha.myapps.movie_maniac.service.processor.iDataProcessor;
import me.sahilmidha.myapps.movie_maniac.utils.URLBuilder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesListFragment extends Fragment implements iWebServiceResponseListener
{


    private MoviesAdapter moviesAdapter;
    private List<Movie> moviesList;
    public MoviesListFragment() {
        // Required empty public constructor
    }

    //Create an interface which an activity can implement so that it listens to clicks in fragments
    public static interface MovieListListener{
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

        ApplicationController.getInstance().getWebServiceManager().createGetRequest(this, new MovieListDataProcessor(), URLBuilder.getMoviesListRequest("popularity")+getString(R.string.api_key_theMovieDb));
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
        moviesList = new ArrayList<Movie>();
        moviesAdapter = new MoviesAdapter(inflater.getContext(), moviesList);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.id_grid_view_movie_list);
        gridView.setAdapter(moviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie movies = moviesAdapter.getItem(position);
                if(null != movieListListener) {
                    //Call the MovieListListener's itemClick method here.
                    movieListListener.itemClick(movies.getId());
                }
            }
        });
        return rootView;
    }

    @Override
    public void onWebServiceSuccess(iDataProcessor dataProcessor)
    {
        if (dataProcessor instanceof MovieListDataProcessor)
        {

            MovieListDataProcessor processor = (MovieListDataProcessor) dataProcessor;
            if(null != processor.getMoviesArrayList()){
                moviesAdapter.clear();
                moviesAdapter.addAll(processor.getMoviesArrayList());
                //moviesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onWebServiceFailed()
    {

    }

}
