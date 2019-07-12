package com.example.popmovies4;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.popmovies4.data.Contract.FavoriteEntry;
import com.example.popmovies4.model.MoviesDB.Movie;
import com.example.popmovies4.model.Reviews.Review;
import com.example.popmovies4.model.Trailers.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieDetailFragment extends Fragment {
    public static final String EXTRA_MOVIE = "MovieDetailFragment.movie";
    private RecyclerView mTrailerRecyclerView;
    private TrailersAdapter mTrailerAdapter;
    private LinearLayout mLinearLayout;
    private List<Trailer> mTrailerList = new ArrayList<>();
    private List<Review> mReviewList = new ArrayList<>();
    private LayoutInflater mlaLayoutInflater;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private TextView mRateTextView;
    private TextView mOverviewTextView;
    private ImageView mMovieImageView;
    private ImageView mMakeFavoriteStar;
    private Movie movie;
    private final static String LOG_TAG = "MovieDetailFragment";
    private boolean mFavorite;
    private final static int FAVORITE_LOADER = 1;
    private String mFirstTrailer;
    ReviewsTask.CallbacksListener mReviewsTaskListener;
    TrailersTask.CallbacksListener mTrailersTaskListener;
    private ShareActionProvider mShareActionProvider;




    public static MovieDetailFragment newInstance(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MOVIE, movie);
        MovieDetailFragment mdf = new MovieDetailFragment();
        mdf.setArguments(bundle);
        return mdf;
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mlaLayoutInflater = inflater;
        View v = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        setHasOptionsMenu(true);

        if (getArguments() == null) return v;
        Object obj = getArguments().getSerializable(EXTRA_MOVIE);
        if (obj != null)
            movie = (Movie) obj;

        mLinearLayout = (LinearLayout) v.findViewById(R.id.reviews_liner_layout);


        mTrailerRecyclerView = (RecyclerView) v.findViewById(R.id.trailers_recycler_view);
        mTrailerRecyclerView.setNestedScrollingEnabled(false);

        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrailerAdapter = new TrailersAdapter(mTrailerList);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);


        mTitleTextView = (TextView) v.findViewById(R.id.fragment_detail_movie_title);
        mDateTextView = (TextView) v.findViewById(R.id.fragment_detail_movie_year);
        mRateTextView = (TextView) v.findViewById(R.id.fragment_detail_movie_rate);
        mOverviewTextView = (TextView) v.findViewById(R.id.fragment_detail_movie_overview);
        mMovieImageView = (ImageView) v.findViewById(R.id.fragment_detail_movie_image);
        mMakeFavoriteStar = (ImageView) v.findViewById(R.id.fragment_detail_movie_make_favorite);


        // add Movie in favorite lest
        mMakeFavoriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavorite) {
                    getActivity().getContentResolver().delete(FavoriteEntry.CONTENT_URI, FavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                            new String[]{movie.getId() + ""});
                    mMakeFavoriteStar.setImageResource(R.drawable.sad_star);
                    Toast.makeText(getContext(), movie.getOriginal_title() + getString(R.string.removed_frme_favorite), Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(FavoriteEntry.COLUMN_MOVIE_ID, movie.getId());
                    getActivity().getContentResolver().insert(FavoriteEntry.CONTENT_URI, cv);

                    mMakeFavoriteStar.setImageResource(R.drawable.happy_star);
                    Toast.makeText(getContext(), movie.getOriginal_title() + getString(R.string.added_to_favorite), Toast.LENGTH_SHORT).show();

                }
                mFavorite = !mFavorite;
            }
        });


        mTitleTextView.setText(movie.getOriginal_title());
        mRateTextView.setText(movie.getVote_average());
        mOverviewTextView.setText(movie.getOverview());
        mDateTextView.setText(movie.getRelease_date());
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + movie.getPoster_path()).into(mMovieImageView);



        mReviewsTaskListener = new ReviewsTask.CallbacksListener() {
            @Override
            public void onReviewsDownloaded(List<Review> reviews) {
                if (reviews != null) {
                    mLinearLayout.removeAllViews();

                    for (int i = 0; i < reviews.size(); i++) {
                        View item = inflater.inflate(R.layout.review_list_item, mLinearLayout, false);
                        ((TextView) (item.findViewById(R.id.list_item_review_tv))).setText(reviews.get(i).getContent());
                        ((TextView) (item.findViewById(R.id.reviewer_name))).setText(reviews.get(i).getAuthor() + " : ");
                        mLinearLayout.addView(item);
                    }
                    mReviewList = reviews;
                }
            }
        };


        mTrailersTaskListener = new TrailersTask.CallbacksListener() {
            @Override
            public void onTrailersDownloaded(List<Trailer> trailers) {
                if (trailers != null) {

                    if (trailers.size() > 0) {
                        mFirstTrailer = "http://www.youtube.com/watch?v=" + trailers.get(0).getKey();
                        if (mShareActionProvider != null)
                            mShareActionProvider.setShareIntent(createShareTrailerIntent());
                    }
                    mTrailerAdapter.setList(trailers);
                    mTrailerAdapter.notifyDataSetChanged();

                }
            }
        };

        new TrailersTask(getActivity(), 1, mTrailersTaskListener).execute(movie.getId());
        new ReviewsTask(getActivity(), 1, mReviewsTaskListener).execute(movie.getId());


        return v;
    }


    private void checkFavoriteStar() {
        Cursor c = getActivity().getContentResolver().query(FavoriteEntry.CONTENT_URI,
                null,
                FavoriteEntry.TABLE_NAME + "." + FavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie.getId() + ""}, null);

        mFavorite = c.moveToFirst();
        c.close();
        if (mFavorite) mMakeFavoriteStar.setImageResource(R.drawable.happy_star);
        else mMakeFavoriteStar.setImageResource(R.drawable.sad_star);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (movie != null) {
            checkFavoriteStar();
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mFirstTrailer != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mFirstTrailer);
        return shareIntent;
    }


    private class TrailersAdapter extends RecyclerView.Adapter<TrailerHolder> {
        List<Trailer> mList;

        public void setList(List<Trailer> mList) {
            this.mList = mList;
        }

        public TrailersAdapter(List<Trailer> m) {
            mList = m;
        }

        @Override
        public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.trailer_list_item, parent, false);
            return new TrailerHolder(v);
        }

        @Override
        public void onBindViewHolder(TrailerHolder holder, int position) {
            holder.bindData(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }


    private class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private Trailer mTrailer;

        public TrailerHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        public void bindData(Trailer t) {
            mTrailer = t;
            name.setText(t.getName());
        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + mTrailer.getKey()));
            if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(i);
            } else {
                Log.d(LOG_TAG, "Couldn't play video, no receiving apps installed!");
            }

        }
    }

}

