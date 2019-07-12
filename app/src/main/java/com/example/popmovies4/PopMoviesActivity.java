package com.example.popmovies4;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.popmovies4.model.MoviesDB.Movie;

public class PopMoviesActivity extends AppCompatActivity implements PopMoviesFragment.Callback {
    private String mSortType;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortType = Utility.getsortType(this);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    protected Fragment createFragment() {
        return PopMoviesFragment.newInstance();
    }


    @Override
    protected void onResume() {
        super.onResume();
        String sort = Utility.getsortType(this);
        if (sort != null && !sort.equals(mSortType)) {
            PopMoviesFragment pmf = (PopMoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_pop);
            if (null != pmf) {
                pmf.updateView();
            }

            mSortType = sort;
        }
    }

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


    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, MovieDetailFragment.newInstance(movie), DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = DetailActivity.newIntent(this, movie);
            startActivity(intent);
        }
    }
}

