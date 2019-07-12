package com.example.popmovies4;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.popmovies4.model.MoviesDB;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoviesDB.Movie movie = (MoviesDB.Movie) getIntent().getSerializableExtra(MovieDetailFragment.EXTRA_MOVIE);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, MovieDetailFragment.newInstance(movie))
                    .commit();
        }
    }

    public static Intent newIntent(Context c, MoviesDB.Movie movie) {
        Intent i = new Intent(c, DetailActivity.class);
        i.putExtra(MovieDetailFragment.EXTRA_MOVIE, movie);
        return i;
    }

//////////////////Menu///////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pop_movies_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

