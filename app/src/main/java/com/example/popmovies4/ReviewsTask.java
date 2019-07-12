package com.example.popmovies4;

import android.content.Context;
import android.os.AsyncTask;
import com.example.popmovies4.model.Reviews;
import java.io.IOException;
import java.util.List;


public class ReviewsTask extends AsyncTask<String, Void, List<Reviews.Review>> {

    public interface CallbacksListener {
        void onReviewsDownloaded(List<Reviews.Review> reviews);
    }


    private Context mContext;
    private int mPage;
    private CallbacksListener mListener;

    public ReviewsTask(Context context, int page,CallbacksListener listener) {
        mContext = context;
        mPage = page;
        mListener=listener;
    }

    @Override
    protected List<Reviews.Review> doInBackground(String... params) {
        try {
            return new HttpFetcher(mContext, mPage).getReviews(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Reviews.Review> reviews) {
        mListener.onReviewsDownloaded(reviews);
    }
}
