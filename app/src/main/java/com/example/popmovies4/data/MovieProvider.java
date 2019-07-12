package com.example.popmovies4.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.example.popmovies4.data.Contract.FavoriteEntry;
import com.example.popmovies4.data.Contract.MovieEntry;
import com.example.popmovies4.data.Contract.PopularEntry;
import com.example.popmovies4.data.Contract.TopRatedEntry;

//  Example Project from udacity on githup
//  https://github.com/udacity/android-content-provider/blob/master/app/src/main/java/com/sam_chordas/android/androidflavors/data/FlavorsProvider.java

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    // Codes for the UriMatcher //////
    static final int MOVIE = 100;
    static final int POPULAR = 101;
    static final int TOP_RATED = 102;
    static final int FAVORITE = 103;
    static final int MOVIE_WITH_ID = 333;
/////

    private static final SQLiteQueryBuilder sFavoriteMovieQueryBuilder = getMovieQueryBuilder(FavoriteEntry.TABLE_NAME);
    private static final SQLiteQueryBuilder sPopularMovieQueryBuilder = getMovieQueryBuilder(PopularEntry.TABLE_NAME);
    private static final SQLiteQueryBuilder sTopRatedMovieQueryBuilder = getMovieQueryBuilder(TopRatedEntry.TABLE_NAME);


    private static SQLiteQueryBuilder getMovieQueryBuilder(String table) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(
                table + " INNER JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + table +
                        "." + FavoriteEntry.COLUMN_MOVIE_ID +
                        " = " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry.COLUMN_MOVIE_ID);
        return sqLiteQueryBuilder;
    }


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, Contract.PATH_TOP_RATED, TOP_RATED);
        matcher.addURI(authority, Contract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, Contract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, Contract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case POPULAR:
                return PopularEntry.CONTENT_TYPE;
            case TOP_RATED:
                return TopRatedEntry.CONTENT_TYPE;
            case FAVORITE:
                return FavoriteEntry.CONTENT_TYPE;
            case MOVIE:
                return FavoriteEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case POPULAR:
                retCursor = sPopularMovieQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TOP_RATED:
                retCursor = sTopRatedMovieQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case FAVORITE:
                retCursor = sFavoriteMovieQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case MOVIE:

                retCursor = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case MOVIE_WITH_ID:

                long id = ContentUris.parseId(uri);
                retCursor = mOpenHelper.getReadableDatabase().
                        query(MovieEntry.TABLE_NAME,
                                projection,
                                MovieEntry._ID + " = ? ",
                                new String[]{id + ""},
                                null,
                                null,
                                sortOrder);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri retUri;
        Long id;


        switch (match) {
            case POPULAR:
                id = db.insert(PopularEntry.TABLE_NAME, null, values);
                if (id != -1)
                    retUri = PopularEntry.buildPopularUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TOP_RATED:
                id = db.insert(TopRatedEntry.TABLE_NAME, null, values);
                if (id != -1)
                    retUri = TopRatedEntry.buildPopularUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case FAVORITE:
                id = db.insert(FavoriteEntry.TABLE_NAME, null, values);
                if (id != -1)
                    retUri = FavoriteEntry.buildPopularUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case MOVIE:
                id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (id != -1)
                    retUri = MovieEntry.buildPopularUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(retUri, null);
        return retUri;
    }


    private int insertBulk(String table, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {

                long _id = db.insert(table, null, value);

                if (_id != -1) {
                    returnCount++;

                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return returnCount;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);
        int ret;
        switch (match) {
            case POPULAR:
                ret = insertBulk(PopularEntry.TABLE_NAME, values);
                break;
            case TOP_RATED:
                ret = insertBulk(TopRatedEntry.TABLE_NAME, values);
                break;
            case MOVIE:
                ret = insertBulk(MovieEntry.TABLE_NAME, values);
                break;
            case FAVORITE:
                ret = insertBulk(FavoriteEntry.TABLE_NAME, values);
                break;
            default:
                return super.bulkInsert(uri, values);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int cnt = 0;
        if (selection == null) selection = "1";
        switch (match) {
            case FAVORITE:
                cnt = db.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_RATED:
                cnt = db.delete(TopRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR:
                cnt = db.delete(PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE:
                cnt = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (cnt > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return cnt;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if (selection == null) selection = "1";
        int cnt = 0;

        switch (match) {
            case FAVORITE:
                cnt = db.update(FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TOP_RATED:
                cnt = db.update(TopRatedEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case POPULAR:
                cnt = db.update(PopularEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE:
                cnt = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (cnt > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return cnt;
    }
}

