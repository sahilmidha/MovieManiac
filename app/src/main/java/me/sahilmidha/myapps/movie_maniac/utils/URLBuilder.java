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
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";


    public static String getMoviesListRequest(String sortBy)
    {
        if(sortBy == null){
            sortBy = POPULAR;
        }
        if(sortBy.equalsIgnoreCase(POPULAR))
        {
            return String.format("%s%s?vote_count.gte=300&api_key=", BASE_URL, POPULAR);
        }
        else if(sortBy.equalsIgnoreCase(TOP_RATED)) {
            return String.format("%s%s?vote_count.gte=300&api_key=", BASE_URL, TOP_RATED);
        }
        return "";
    }

    public static String getMovieDetailsRequest(String movieId)
    {
        return String.format("%s%s?api_key=", BASE_URL, movieId);
    }

}
