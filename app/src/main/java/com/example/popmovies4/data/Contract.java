package com.example.popmovies4.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

// Example Project from udacity on githup
//https://github.com/udacity/android-content-provider/blob/master/app/src/main/java/com/sam_chordas/android/androidflavors/data/FlavorsContract.java

public class Contract {
    public static final String CONTENT_AUTHORITY = "com.example.popmovies4";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_MOVIE = "movie";


    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_MOVIE_ID = "movie_id";


        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

// Popular Movies

    public static final class PopularEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        public static final String TABLE_NAME = "popular";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

// Top Rate Movies
    public static final class TopRatedEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;


        public static final String TABLE_NAME = "top_rated";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

// Favorite Movies
    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;


        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }


}
