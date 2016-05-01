package me.sahilmidha.myapps.movie_maniac.service;

import me.sahilmidha.myapps.movie_maniac.service.processor.iDataProcessor;

/**
 * Created by sahilmidha on 01/05/16.
 */
public interface iWebServiceResponseListener
{
    public void onWebServiceSuccess(iDataProcessor dataProcessor);

    public void onWebServiceFailed();
}
