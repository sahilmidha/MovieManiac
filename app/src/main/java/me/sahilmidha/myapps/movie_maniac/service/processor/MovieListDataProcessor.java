package me.sahilmidha.myapps.movie_maniac.service.processor;

import java.util.ArrayList;

import me.sahilmidha.myapps.movie_maniac.Movies;

/**
 * Created by sahilmidha on 01/05/16.
 */
public class MovieListDataProcessor implements iDataProcessor
{
    ArrayList<Movies> _moviesArrayList;

    public MovieListDataProcessor(ArrayList<Movies> moviesArrayList)
    {
        _moviesArrayList = moviesArrayList;
    }


    @Override
    public boolean parseData(Object responseObject)
    {

        return false;
    }
}
