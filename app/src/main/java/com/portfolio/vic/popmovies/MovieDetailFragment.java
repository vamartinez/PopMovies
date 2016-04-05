package com.portfolio.vic.popmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.portfolio.vic.popmovies.db.Movie;
import com.portfolio.vic.popmovies.db.Movie_Table;
import com.portfolio.vic.popmovies.dummy.DummyContent;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private long id;
    private Movie movie;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void startActivity(Intent intent) {
        if (getArguments().containsKey(ARG_ITEM_ID)) {
        id = getArguments().getLong(ARG_ITEM_ID);
        }
        movie = SQLite.select()
                .from(Movie.class)
                .where(Movie_Table.id.eq(id))
                .querySingle();

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && movie!= null) {
            appBarLayout.setTitle(movie.getTitle());
        }
        super.startActivity(intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (movie != null) {
            ((TextView) rootView.findViewById(R.id.movie_detail)).setText(movie.getTitle());
            ImageView image = (ImageView) rootView.findViewById(R.id.photoIV);
            Picasso.with(getContext())
                    .load(movie.getImageFullPath(getContext()))
                    .placeholder(R.drawable.ic_sync_black_24dp)
                    .error(R.drawable.ic_error)
                    .into(image);
            ((TextView) rootView.findViewById(R.id.yearTV)).setText(movie.getRelease_date());
            ((TextView) rootView.findViewById(R.id.voteTV)).setText(String.valueOf(movie.getVote_average()));
            ((TextView) rootView.findViewById(R.id.descriptionTV)).setText(movie.getOverview());
        }

        return rootView;
    }
}
