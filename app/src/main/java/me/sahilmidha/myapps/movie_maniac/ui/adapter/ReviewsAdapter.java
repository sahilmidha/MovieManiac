package me.sahilmidha.myapps.movie_maniac.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import me.sahilmidha.myapps.movie_maniac.R;
import me.sahilmidha.myapps.movie_maniac.service.model.details.reviews.Reviews;

/**
 * Created by sahilmidha on 03/05/16.
 */
public class ReviewsAdapter extends BaseAdapter
{
    List<Reviews> _reviewsList;

    public ReviewsAdapter(List<Reviews> reviewsList)
    {
        _reviewsList = reviewsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount()
    {
        return _reviewsList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position)
    {
        return _reviewsList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if(null == convertView){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews, parent, false);
            TextView textViewReviews = (TextView)convertView.findViewById(R.id.textView_reviews);
            TextView textViewReview = (TextView)convertView.findViewById(R.id.textView_review);
            TextView textViewReviewByText = (TextView)convertView.findViewById(R.id.textView_reviewByText);
            TextView textViewReviewBy = (TextView)convertView.findViewById(R.id.textView_reviewBy);
            viewHolder = new ViewHolder(textViewReview, textViewReviewBy, textViewReviewByText, textViewReviews);
            convertView.setTag(viewHolder);
        } else {
             viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder._textViewReviews.setText("Reviews");
        viewHolder._textViewReview.setText(((Reviews)_reviewsList.get(position)).getContent());
        viewHolder._textViewReviewByText.setText("Review by : ");
        viewHolder._textViewReviewBy.setText(((Reviews)_reviewsList.get(position)).getAuthor());

        return convertView;
    }

    public class ViewHolder{
        TextView _textViewReview;
        TextView _textViewReviewByText;
        TextView _textViewReviewBy;
        TextView _textViewReviews;

        public ViewHolder(TextView textViewReview, TextView textViewReviewBy, TextView textViewReviewByText, TextView textViewReviews)
        {
            _textViewReview = textViewReview;
            _textViewReviewByText = textViewReviewByText;
            _textViewReviewBy = textViewReviewBy;
            _textViewReviews = textViewReviews;
        }
    }
}
