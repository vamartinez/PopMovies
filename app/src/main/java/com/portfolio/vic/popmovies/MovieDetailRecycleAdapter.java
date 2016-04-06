package com.portfolio.vic.popmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.portfolio.vic.popmovies.db.Movie;
import com.portfolio.vic.popmovies.db.Reviews;
import com.portfolio.vic.popmovies.db.Trailer;
import com.raizlabs.android.dbflow.list.FlowCursorList;

import org.w3c.dom.Text;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by vic on 05/04/2016.
 */
public class MovieDetailRecycleAdapter extends RecyclerView.Adapter<MovieDetailRecycleAdapter.ViewHolder> {

    private FlowCursorList<Reviews> mFlowCursorReviewList;
    private FlowCursorList<Trailer> mFlowCursorTrailerList;

    public MovieDetailRecycleAdapter() {
        if (mFlowCursorReviewList == null)
            mFlowCursorReviewList = new FlowCursorList<>(true, Reviews.class);
        if (mFlowCursorTrailerList == null)
            mFlowCursorTrailerList = new FlowCursorList<>(true, Trailer.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_detail_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < mFlowCursorTrailerList.getCount()) {
            final Trailer trailer = mFlowCursorTrailerList.getItem(position);
            holder.titleTV.setText(trailer.getSite());
            holder.detailTV.setText(R.string.show_trailer);
            holder.iconIV.setImageResource(android.R.drawable.presence_video_online);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(null, Uri.parse("vnd.youtube:" + trailer.getKey()));
                    List<ResolveInfo> list = v.getContext().getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() == 0) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse(v.getContext().getString(R.string.youtube_com)+trailer.getKey()));
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        v.getContext().startActivity(i);
                    } else
                        v.getContext().startActivity(i);
                }
            });
        } else {
            Reviews reviews = mFlowCursorReviewList.getItem(position - mFlowCursorTrailerList.getCount());
            holder.titleTV.setText(reviews.getAuthor());
            holder.detailTV.setText(reviews.getContent());
            holder.iconIV.setImageResource(android.R.drawable.ic_dialog_info);
        }

    }

    @Override
    public int getItemCount() {
        return mFlowCursorReviewList.getCount() + mFlowCursorTrailerList.getCount();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView iconIV;
        public final TextView titleTV;
        public final TextView detailTV;
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            iconIV = (ImageView) view.findViewById(R.id.iconIV);
            titleTV = (TextView) view.findViewById(R.id.titleTV);
            detailTV = (TextView) view.findViewById(R.id.detailTV);
        }

    }
}
