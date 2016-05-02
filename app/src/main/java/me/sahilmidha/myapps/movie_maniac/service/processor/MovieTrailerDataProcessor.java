package me.sahilmidha.myapps.movie_maniac.service.processor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import me.sahilmidha.myapps.movie_maniac.service.model.Movie;
import me.sahilmidha.myapps.movie_maniac.service.model.details.MovieDetail;
import me.sahilmidha.myapps.movie_maniac.service.model.details.video.Trailer;

/**
 * Created by sahilmidha on 02/05/16.
 */
public class MovieTrailerDataProcessor implements iDataProcessor
{
    ArrayList<Trailer> _moviesTrailerArrayList;

    @Override
    public boolean parseData(Object responseObject)
    {
        try
        {
            JSONObject response = new JSONObject((String) responseObject);


            Gson gson = new Gson();
            _moviesTrailerArrayList = gson.fromJson(response.optJSONArray("results").toString(), new TypeToken<ArrayList<Trailer>>()
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

    public ArrayList<Trailer> getMoviesTrailerArrayList()
    {
        return _moviesTrailerArrayList;
    }
}
