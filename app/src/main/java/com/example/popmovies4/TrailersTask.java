package com.example.popmovies4;

import android.content.Context;
import android.os.AsyncTask;
import com.example.popmovies4.model.Trailers;
import java.io.IOException;
import java.util.List;


public class TrailersTask extends AsyncTask<String, Void, List<Trailers.Trailer>> {
    public interface CallbacksListener {
        void onTrailersDownloaded(List<Trailers.Trailer> trailers);
    }

    private Context mContext;
    private int mPage;
    private CallbacksListener mListener;

    public TrailersTask(Context context, int page, CallbacksListener listener) {
        mContext = context;
        mPage = page;
        mListener = listener;
    }

    @Override
    protected List<Trailers.Trailer> doInBackground(String... params) {
        try {
            return new HttpFetcher(mContext, mPage).getTrailers(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trailers.Trailer> trailers) {
        mListener.onTrailersDownloaded(trailers);
    }
}
