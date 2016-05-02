package me.sahilmidha.myapps.movie_maniac.service.processor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import me.sahilmidha.myapps.movie_maniac.service.model.Movie;
import me.sahilmidha.myapps.movie_maniac.service.model.details.MovieDetail;

/**
 * Created by sahilmidha on 02/05/16.
 */
public class MovieDetailDataProcessor implements iDataProcessor
{
    MovieDetail _movieDetail;
    public MovieDetailDataProcessor()
    {

    }


    @Override
    public boolean parseData(Object responseObject)
    {
        try
        {
            JSONObject response = new JSONObject((String) responseObject);


            Gson gson = new Gson();
            _movieDetail = gson.fromJson(response.toString(), new TypeToken<MovieDetail>()
            {
            }.getType());

            return true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

    }

    public MovieDetail getMovieDetailObject()
    {
        return _movieDetail;
    }
}
