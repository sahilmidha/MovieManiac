package me.sahilmidha.myapps.movie_maniac;

/**
 * Created by Sahil Midha on 3/2/2016.
 *
 */

public class Movies {
    //Doubt: Somehow I want to manage things better so that if the response changes let's say, I can easily adapt to the change.
    //The JSONObjects I get and attributes I declare here and parameters I pass the URL
    //Please help me
    long id;
    String poster_url;
    String title;
    String plot; //called overview in the api
    float user_rating; //called vote_average in the api
    String release_date;

    public Movies(long id, String url){
        this.id = id;
        this.poster_url = url;
    }
    public Movies(long id, String poster_url, String title, String plot, float user_rating, String release_date) {
        this.id = id;
        this.poster_url = poster_url;
        this.title = title;
        this.plot = plot;
        this.user_rating = user_rating;
        this.release_date = release_date;
    }

    public String toString() {
        return this.poster_url;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public String getPlot() {
        return plot;
    }

    public float getUser_rating() {
        return user_rating;
    }

    public String getRelease_date() {
        return release_date;
    }

}
