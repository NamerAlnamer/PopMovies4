package com.example.popmovies4;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.popmovies4.data.Contract;
import com.example.popmovies4.model.MoviesDB;



public class Utility {
    public static String getsortType(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortType = prefs.getString(mContext.getString(R.string.pref_sort_key),
                mContext.getString(R.string.pref_sort_default));
        return sortType;
    }


    public static ContentValues getMovieCV(MoviesDB.Movie movie) {
        ContentValues cv = new ContentValues();

        cv.put(Contract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        cv.put(Contract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginal_title());
        cv.put(Contract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(Contract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        cv.put(Contract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        cv.put(Contract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());
        cv.put(Contract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());

        return cv;
    }
}

