package me.sahilmidha.myapps.movie_maniac.utils;

import android.net.Uri;

import me.sahilmidha.myapps.movie_maniac.R;

/**
 * Created by sahilmidha on 01/05/16.
 *
 * We need to append api_key to this URLs
 */
public class URLBuilder
{
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String MOVIES_EXPLORE = "/discover/movie";
    private static final String MOVIE_DETAILS = "/movie/";


    public static String getMoviesListRequest(String sortBy)
    {
        if(sortBy.equalsIgnoreCase("vote_average.desc"))
        {
            return String.format("%s%s?sort_by=%s&vote_count.gte=50&api_key=", BASE_URL, MOVIES_EXPLORE, sortBy);
        }
        else {
            return String.format("%s%s?sort_by=%s&api_key=", BASE_URL, MOVIES_EXPLORE, sortBy);
        }
    }

    public static String getMovieDetailsRequest(String movieId)
    {
        return String.format("%s%s/%s/%s?api_key=", BASE_URL, MOVIE_DETAILS, movieId);
    }

}
