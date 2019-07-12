package com.example.popmovies4;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

// Example Project from udacity on githup
//   https://github.com/udacity/android-content-provider/blob/master/app/src/main/java/com/sam_chordas/android/androidflavors/FlavorAdapter.java

public class MovieAdapter extends CursorAdapter {
    private Context mContext;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false);
        MovieHolder m = new MovieHolder(v);
        v.setTag(m);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MovieHolder movieHolder = (MovieHolder) view.getTag();
        movieHolder.bindMoviePoster(cursor.getString(PopMoviesFragment.POSTER_PATH));
    }


    private class MovieHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public MovieHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.movie_holder_image_view);
        }

        public void bindMoviePoster(String poster_path) {

            Picasso.with(mContext)
                    .load("http://image.tmdb.org/t/p/w185/" + poster_path)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.erro)
                    .into(mImageView);

//            ImageView imageView = (ImageView) findViewById(R.id.imageView);
//
//            Picasso.with(this)
//                    .load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg")
//                    .into(imageView);

        }

    }
}

