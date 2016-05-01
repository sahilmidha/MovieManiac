package me.sahilmidha.myapps.movie_maniac.service.controller;

import me.sahilmidha.myapps.movie_maniac.service.iWebServiceResponseListener;
import me.sahilmidha.myapps.movie_maniac.service.network.DataFetcher;
import me.sahilmidha.myapps.movie_maniac.service.processor.iDataProcessor;
import me.sahilmidha.myapps.movie_maniac.utils.URLHolder;

/**
 * Created by sahilmidha on 01/05/16.
 *
 * This class is responsible for making web requests.
 */
public class WebServiceManager
{
    public void createGetRequest(iWebServiceResponseListener listener, iDataProcessor responseProcessor, String url)
    {
        this.createRequest(listener, responseProcessor, url, null);
    }

    public void createPostRequest(iWebServiceResponseListener listener, iDataProcessor responseProcessor, String url, String requestBody)
    {
        this.createRequest(listener, responseProcessor, url, requestBody);
    }

    private void createRequest(iWebServiceResponseListener listener, iDataProcessor responseProcessor, String url, String requestBody)
    {
        URLHolder requestURL = new URLHolder().holdURL(url, requestBody);
        new DataFetcher().fetchData(requestURL, responseProcessor, listener);
    }
}
