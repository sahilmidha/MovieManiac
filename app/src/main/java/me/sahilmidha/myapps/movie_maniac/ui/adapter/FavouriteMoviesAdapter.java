package me.sahilmidha.myapps.movie_maniac.ui.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.sahilmidha.myapps.movie_maniac.R;
import me.sahilmidha.myapps.movie_maniac.service.model.Movie;
import me.sahilmidha.myapps.movie_maniac.service.model.details.MovieDetail;

/**
 * Created by sahilmidha on 03/05/16.
 */
public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.ViewHolder>
{
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    public static interface OnCardViewClickListener
    {
        void onCardViewItemClick(MovieDetail movieDetail);
    }

    private final OnCardViewClickListener mListener;

    private List<MovieDetail> mDataset;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public CardView mCardView;

        public ViewHolder(View v)
        {
            super(v);
            mCardView = (CardView) v;
        }

        public void bindListener(final MovieDetail movieDetail, final OnCardViewClickListener listener)
        {

            mCardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onCardViewItemClick(movieDetail);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FavouriteMoviesAdapter(List<MovieDetail> myDataset, OnCardViewClickListener listener)
    {
        mDataset = myDataset;
        mListener = listener;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public FavouriteMoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        MovieDetail movie = mDataset.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ImageView imageView = (ImageView) holder.mCardView.findViewById(R.id.id_image_view_movie_list);
        //imageView.setBackgroundColor(Color.rgb(210, 210, 210));
        String poster_url = imageView.getContext().getString(R.string.poster_url);
        poster_url = poster_url.concat(movie.getPosterPath());
        Picasso.with(imageView.getContext())
                .load(poster_url)
                .into(imageView);

        TextView textViewTitle = (TextView) holder.mCardView.findViewById(R.id.id_textView_original_title);
        textViewTitle.setText(movie.getOriginalTitle());

        TextView textViewRating = (TextView) holder.mCardView.findViewById(R.id.id_textView_rating);
        textViewRating.setText(movie.getVoteAverage().toString());

        TextView textViewTotalRating = (TextView) holder.mCardView.findViewById(R.id.id_textView_RatingTotal);
        textViewTotalRating.setText("/10");

        TextView textViewTotalVotes = (TextView) holder.mCardView.findViewById(R.id.id_textView_totalVotes);
        textViewTotalVotes.setText(movie.getVoteCount().toString());

        TextView textViewReleaseDate = (TextView) holder.mCardView.findViewById(R.id.id_textView_release_date);
        textViewReleaseDate.setText(movie.getReleaseDate().substring(0, 4));

        holder.bindListener(movie, mListener);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

}
