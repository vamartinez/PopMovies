package com.portfolio.vic.popmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.portfolio.vic.popmovies.db.Favorite;
import com.portfolio.vic.popmovies.db.Favorite_Table;
import com.portfolio.vic.popmovies.db.Movie;
import com.portfolio.vic.popmovies.db.Movie_Table;
import com.portfolio.vic.popmovies.db.Reviews;
import com.portfolio.vic.popmovies.db.Trailer;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;
import com.squareup.picasso.Picasso;

import java.util.Timer;

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
    public static final int INTERVAL = 2000;

    /**
     * The dummy content this fragment is presenting.
     */
    private long id;
    private Movie movie;
    private MovieService movieService;
    private boolean startCount = true;
    FlowContentObserver reviewObserver;
    FlowContentObserver trailerObserver;
    private countDownTimer countDownTimer;
    private MovieDetailRecycleAdapter adapter;
    View rootView;

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

        super.startActivity(intent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.movie_detail, container, false);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            id = getArguments().getLong(ARG_ITEM_ID);
        }
        movie = SQLite.select()
                .from(Movie.class)
                .where(Movie_Table.id.eq(id))
                .querySingle();
        Favorite favorite = SQLite.select()
                .from(Favorite.class)
                .where(Favorite_Table.movi_id.eq(id))
                .querySingle();
        if (favorite != null) {
            Log.e(this.toString(),favorite.toString());
            ((Button) rootView.findViewById(R.id.fabBT)).setText(R.string.remove_favorite);
        } else{
        }
        ((Button) rootView.findViewById(R.id.fabBT)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Button) v).getText().equals(v.getContext().getResources().getString(R.string.remove_favorite))) {
                    ((Button) v).setText(R.string.mark_as_favorite);
                    new Delete().from(Favorite.class).where(Favorite_Table.movi_id.eq(movie.getId())).query();
                } else {
                    Favorite tmpfavorite = new Favorite();
                    tmpfavorite.setMovi_id(id);
                    tmpfavorite.save();
                    tmpfavorite.insert();
                    ((Button) v).setText(R.string.remove_favorite);

                }
            }
        });

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && movie != null) {
            appBarLayout.setTitle(movie.getTitle());
        }
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
            movieService = new MovieService(getContext());
            new Delete().from(Reviews.class).where().query();
            new Delete().from(Trailer.class).where().query();
            if (reviewObserver == null)
                reviewObserver = new FlowContentObserver();
            reviewObserver.registerForContentChanges(getContext(), Reviews.class);
            countDownTimer = new countDownTimer(0, INTERVAL);
            FlowContentObserver.OnModelStateChangedListener modelChangeListener = new FlowContentObserver.OnModelStateChangedListener() {
                @Override
                public void onModelStateChanged(@Nullable Class<? extends Model> table, BaseModel.Action action, @NonNull SQLCondition[] primaryKeyValues) {
                    startTimer();
                }
            };
            reviewObserver.endTransactionAndNotify();
            reviewObserver.addModelChangeListener(modelChangeListener);
            if (trailerObserver == null)
                trailerObserver = new FlowContentObserver();
            trailerObserver.registerForContentChanges(getContext(), Trailer.class);
            FlowContentObserver.OnModelStateChangedListener trailerChangeListener = new FlowContentObserver.OnModelStateChangedListener() {
                @Override
                public void onModelStateChanged(@Nullable Class<? extends Model> table, BaseModel.Action action, @NonNull SQLCondition[] primaryKeyValues) {
                    startTimer();
                }
            };
            trailerObserver.endTransactionAndNotify();
            trailerObserver.addModelChangeListener(trailerChangeListener);
            movieService.getExtraContent(reviewObserver, trailerObserver, id);

        }

        return rootView;
    }

    private void startTimer() {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (startCount) {
                        startCount = false;
                        countDownTimer.start();
                    } else {
                        countDownTimer.cancel();
                        countDownTimer = new countDownTimer(0, INTERVAL);
                    }
                }
            });
    }


    //getActivity().runOnUiThread(new Runnable() {
    //        public void run() {

    //        }
    //     });


    public class countDownTimer extends CountDownTimer {

        public countDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            startCount = true;

            if (getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.detailRV);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new MovieDetailRecycleAdapter();
                        ((RecyclerView) rootView.findViewById(R.id.detailRV)).setAdapter(adapter);
                        ((RecyclerView) rootView.findViewById(R.id.detailRV)).getAdapter().notifyDataSetChanged();
                        recyclerView.setItemAnimator(new DefaultItemAnimator());

                    }
                });


        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }
}
