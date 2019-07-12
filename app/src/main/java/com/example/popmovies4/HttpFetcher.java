package com.example.popmovies4;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.popmovies4.data.Contract;
import com.example.popmovies4.model.MoviesDB;
import com.example.popmovies4.model.Reviews;
import com.example.popmovies4.model.Reviews.Review;
import com.example.popmovies4.model.Trailers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;


public class HttpFetcher {
    private Context mContext;

    private static final String TAG = "HttpFetcher";
    private static final String PAGE_PARAM = "page";
    private static final String APIKEY_PARAM = "api_key";
    private static int mPage = 1;


    private static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public HttpFetcher(Context context, int page) {
        mContext = context;
        mPage = page;
    }


    private String buildUrl(String sort_by) {
        String url = Uri.parse(THE_MOVIE_DB_BASE_URL + sort_by).buildUpon()
                .appendQueryParameter(PAGE_PARAM, Integer.toString(mPage))
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.POP_MOVIES_API_KEY)
                .build().toString();
        return url;
    }


    private String buildTrailersUrl(String movieId) {
        String url = Uri.parse(THE_MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.POP_MOVIES_API_KEY)
                .build().toString();
        return url;
    }


    private String buildReviewsUrl(String movieId) {
        String url = Uri.parse(THE_MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.POP_MOVIES_API_KEY)
                .build().toString();
        return url;
    }


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }


            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }


    public void getData() throws IOException {
        String url = buildUrl(getsortType());
        String data = new String(getUrlBytes(url));
        Log.d(TAG, data);
        if (data != null && data.length() > 0) {
            Gson gson = new GsonBuilder().create();
            MoviesDB moviesDB = gson.fromJson(data, MoviesDB.class);

            Vector<ContentValues> lcv = new Vector<>();
            Vector<ContentValues> lid = new Vector<>();

            List<MoviesDB.Movie> movies = moviesDB.getResults();

            for (int i = 0; i < movies.size(); i++) {
                ContentValues cv = Utility.getMovieCV(movies.get(i));

                ContentValues cid = new ContentValues();
                cid.put(Contract.MovieEntry.COLUMN_MOVIE_ID, movies.get(i).getId());
                lcv.add(cv);
                lid.add(cid);
            }

            if (lcv.size() > 0) {
                ContentValues[] cvArray = new ContentValues[lcv.size()];
                lcv.toArray(cvArray);

                ContentValues[] cvids = new ContentValues[lid.size()];
                lid.toArray(cvids);

                mContext.getContentResolver().bulkInsert(Contract.MovieEntry.CONTENT_URI, cvArray);


                int ins;
                if (getsortType().equals(mContext.getResources().getString(R.string.pref_sort_popular)))
                    ins = mContext.getContentResolver().bulkInsert(Contract.PopularEntry.CONTENT_URI, cvids);
                else
                    ins = mContext.getContentResolver().bulkInsert(Contract.TopRatedEntry.CONTENT_URI, cvids);
                Log.d("haha", ins + " ins");
            }

        }


    }

    public String getsortType() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortType = prefs.getString(mContext.getString(R.string.pref_sort_key),
                mContext.getString(R.string.pref_sort_default));
        return sortType;
    }


    public List<Trailers.Trailer> getTrailers(String movieId) throws IOException {

        String trail = buildTrailersUrl(movieId);
        String traildata = new String(getUrlBytes(trail));
        if (traildata != null && traildata.length() > 0) {
            Gson gson = new GsonBuilder().create();
            List<Trailers.Trailer> ltr = gson.fromJson(traildata, Trailers.class).getResults();

            return ltr;
        }
        return null;
    }

    public List<Review> getReviews(String movieId) throws IOException {

        String rev = buildReviewsUrl(movieId);
        String data = new String(getUrlBytes(rev));
        Log.d(TAG, data);
        if (data != null && data.length() > 0) {
            Gson gson = new GsonBuilder().create();
            List<Review> lr = gson.fromJson(data, Reviews.class).getResults();
            return lr;
        }
        return null;
    }


}

