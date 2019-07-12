package com.example.popmovies4;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.example.popmovies4.data.Contract;
import com.example.popmovies4.model.MoviesDB.Movie;
import java.io.IOException;

// Example Project from udacity on githup
// https://github.com/udacity/android-content-provider/blob/master/app/src/main/java/com/sam_chordas/android/androidflavors/DetailFragment.java

public class PopMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private GridView gridView;
    private MovieAdapter movieAdapter;
    public static boolean island = false;
    public static int w;
    public static int h;
    private GridLayoutManager glm;
    private static final int MOVIE_LOADER = 0;

    public final static String Cols[] = {
            Contract.MovieEntry.TABLE_NAME + "." + Contract.MovieEntry._ID,
            Contract.MovieEntry.TABLE_NAME + "." + Contract.MovieEntry.COLUMN_MOVIE_ID,
            Contract.MovieEntry.COLUMN_POSTER_PATH,
            Contract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            Contract.MovieEntry.COLUMN_OVERVIEW,
            Contract.MovieEntry.COLUMN_RELEASE_DATE,
            Contract.MovieEntry.COLUMN_VOTE_AVERAGE,
            Contract.MovieEntry.COLUMN_POPULARITY
    };

    public static int MOVIE_ID = 0;
    public static int MOVIE_LINK_ID = 1;
    public static int POSTER_PATH = 2;
    public static int ORIGINAL_TITLE = 3;
    public static int OVERVIEW = 4;
    public static int RELEASE_DATE = 5;
    public static int VOTE_AVERAGE = 6;
    public static int POPULARITY = 7;


    public static PopMoviesFragment newInstance() {
        return new PopMoviesFragment();
    }

    public interface Callback {
        void onItemSelected(Movie movie);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pop_movies, container, false);
        gridView = (GridView) v.findViewById(R.id.pop_movies_grid_view);

        w = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        h = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        gridView.setNumColumns(w > h ? 3 : 2);
        island = (w > h);

        movieAdapter = new MovieAdapter(getActivity(), null, 0);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {

                    Movie movie = getMovieFromCursor(cursor);

                    ((Callback) getActivity())
                            .onItemSelected(movie);

                }
            }
        });

        new MoviesFetcherTask().execute(1 + "");

        return v;

    }

    private Movie getMovieFromCursor(Cursor cursor) {
        Movie movie = new Movie();
        movie.set_id(cursor.getLong(MOVIE_ID));
        movie.setId(cursor.getString(MOVIE_LINK_ID));
        movie.setPoster_path(cursor.getString(POSTER_PATH));
        movie.setOriginal_title(cursor.getString(ORIGINAL_TITLE));
        movie.setOverview(cursor.getString(OVERVIEW));
        movie.setRelease_date(cursor.getString(RELEASE_DATE));
        movie.setVote_average(cursor.getDouble(VOTE_AVERAGE));
        movie.setPopularity(cursor.getDouble(POPULARITY));
        return movie;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = null;
        Uri tableUri = Contract.PopularEntry.CONTENT_URI;

        if (Utility.getsortType(getContext()).equals(getString(R.string.pref_sort_popular))) {
            tableUri = Contract.PopularEntry.CONTENT_URI;
            sortOrder = Contract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else if (Utility.getsortType(getContext()).equals(getString(R.string.pref_sort_rated))) {
            tableUri = Contract.TopRatedEntry.CONTENT_URI;
            sortOrder = Contract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        } else if (Utility.getsortType(getContext()).equals(getString(R.string.pref_sort_favorite))) {
            tableUri = Contract.FavoriteEntry.CONTENT_URI;
        }

        Log.d("errr", (tableUri == null) + "ghh");
        return new CursorLoader(getActivity(),
                tableUri,
                Cols,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }

    public void updateView() {
        if (!Utility.getsortType(getContext()).equals(getString(R.string.pref_sort_favorite)))
            new MoviesFetcherTask().execute(1 + "");

         getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }


    private class MoviesFetcherTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                new HttpFetcher(getContext(), Integer.parseInt(params[0])).getData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }
}

