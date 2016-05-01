package me.sahilmidha.myapps.movie_maniac.service.processor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import me.sahilmidha.myapps.movie_maniac.service.model.Movie;

/**
 * Created by sahilmidha on 01/05/16.
 */
public class MovieListDataProcessor implements iDataProcessor
{
    ArrayList<Movie> _moviesArrayList;

    public MovieListDataProcessor()
    {
        _moviesArrayList = new ArrayList<Movie>();
    }


    @Override
    public boolean parseData(Object responseObject)
    {
        try
        {
            JSONObject response = new JSONObject((String) responseObject);


            Gson gson = new Gson();
            _moviesArrayList = gson.fromJson(response.optJSONArray("results").toString(), new TypeToken<ArrayList<Movie>>()
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

    public ArrayList<Movie> getMoviesArrayList()
    {
        return _moviesArrayList;
    }
}
