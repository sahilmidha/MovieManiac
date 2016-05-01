package me.sahilmidha.myapps.movie_maniac;

import android.app.Application;

import me.sahilmidha.myapps.movie_maniac.service.controller.ApplicationController;

/**
 * Created by sahilmidha on 01/05/16.
 */
public class MovieManiac extends Application
{
    private static MovieManiac mInstance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance=MovieManiac.this;
        ApplicationController.getInstance().init();
    }
    //make sure that you donot assign the context to any variable outside the android, if you do so,
    // it won't be destroyed even if that activty is destroyed.
    public static MovieManiac getInstance(){
        return mInstance;
    }
}
