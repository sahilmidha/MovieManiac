package me.sahilmidha.myapps.movie_maniac;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * This is the launcher Activity. It inflates 2 layouts, one each for phone and tablet.
 */
public class MainActivity extends AppCompatActivity implements MovieListFragment.MovieListListener{

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
        return super.onOptionsItemSelected(item);
    }
    //Implement MovieListFragment.MovieListListener method itemClick to handle clicks on GridView on MovieListFragment.java
    //See MovieListFragment.java for more information on this.
    @Override
    public void itemClick(long id) {
        View fragmentContainer = findViewById(R.id.id_fragment_container_movie_detail);
        //Check if the view contains 'fragmentContainer', If it does, it is a tablet layout.
        //and so initialise the MovieDetailFragment and replace it in the frameLayout in activity_main.xml
        if(null != fragmentContainer) {
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
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
}
