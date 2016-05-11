package me.sahilmidha.myapps.movie_maniac.ui.activity;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.sahilmidha.myapps.movie_maniac.ui.fragment.MovieDetailFragment;
import me.sahilmidha.myapps.movie_maniac.R;
import me.sahilmidha.myapps.movie_maniac.SettingsActivity;
import me.sahilmidha.myapps.movie_maniac.service.model.Movie;
import me.sahilmidha.myapps.movie_maniac.ui.fragment.MoviesListFragment;

/**
 * This is the launcher Activity. It inflates 2 layouts, one each for phone and tablet.
 */
public class MainActivity extends AppCompatActivity implements MoviesListFragment.MovieListListener{

    MovieDetailFragment movieDetailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //defined 2 activity_main.xml - one for phone and other for tablet. Android will pick the right one itself.
        setContentView(R.layout.activity_main);
        //now the layout has been populated above through setContentView, we can take out it's reference of the fragment
    }
    //Override onCreateOptionsMenu and onOptionsItemSelected for handling Menu Item clicks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(R.id.action_settings == id){
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, id);
            startActivity(intent);
        }
        else if(R.id.action_favorite == id){
            Intent intent = new Intent(this, FavoriteMoviesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    //Implement MovieListFragment.MovieListListener method itemClick to handle clicks on RecyclerView on MovieListFragment.java
    //See MovieListFragment.java for more information on this.
    @Override
    public void itemClick(Long id) {
        View fragmentContainer = findViewById(R.id.id_fragment_container_movie_detail);
        //Check if the view contains 'fragmentContainer', If it does, it is a tablet layout.
        //and so initialise the MovieDetailFragment and replace it in the frameLayout in activity_main.xml
        if(null != fragmentContainer) {
            movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setMovieId(id);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.id_fragment_container_movie_detail, movieDetailFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            //Add the transaction to the backStack to unable back button functionality.
            // It takes a String param you can use to label the transaction:
            transaction.addToBackStack(null);
            transaction.commit();
        }else {
            //if it's a phone layout, pass the intent to start MovieDetailActivity along with movieId
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.MOVIE_ID, id);
            startActivity(intent);
        }
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
