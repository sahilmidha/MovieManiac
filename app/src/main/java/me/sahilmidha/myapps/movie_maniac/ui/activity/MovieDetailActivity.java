package me.sahilmidha.myapps.movie_maniac.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import me.sahilmidha.myapps.movie_maniac.R;
import me.sahilmidha.myapps.movie_maniac.ui.fragment.MovieDetailFragment;

/**
 * This contains only a fragment to display details of a movie. This activity is only called for phone via an Intent passed by MainActivity.
 */
public class MovieDetailActivity extends AppCompatActivity
{

    public static final String MOVIE_ID = "movieId";
    MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Our activity will receive the Intent and pass it to DetailFragment
        movieDetailFragment = (MovieDetailFragment) getFragmentManager().findFragmentById(R.id.id_fragment_movie_detail);
        long movieId = (long) getIntent().getExtras().get(MOVIE_ID);
        movieDetailFragment.setMovieId(movieId);
    }

    public void sendIntentYouTube(View v)
    {
        Uri uri = Uri.parse(movieDetailFragment.getMediaUrl());

        Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps)
        {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.google.android.youtube"))
            {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.setComponent(componentName);
                startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound)
        {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.EMPTY);
            startActivity(webIntent);
        }
    }

    public void onClickMarkFavorite(View v){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.favorite_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String value = sharedPreferences.getString(getString(R.string.favorite_key), null);
        if(value != null
                && !value.isEmpty()){
            if(((CheckBox)v).isChecked()
                    && !value.contains(movieDetailFragment.getMovieId().toString())){
                value = value.concat("," + movieDetailFragment.getMovieId().toString());
            }
            else if(value.contains(movieDetailFragment.getMovieId().toString())){
                List<String> myList = new ArrayList<String>(Arrays.asList(value.split(",")));
                myList.remove(movieDetailFragment.getMovieId().toString());
                value = TextUtils.join(",", myList);
            }

            editor.putString(getString(R.string.favorite_key), value);
            editor.commit();
        }
        else {
            editor.putString(getString(R.string.favorite_key), movieDetailFragment.getMovieId().toString());
            editor.commit();
        }

    }
}
