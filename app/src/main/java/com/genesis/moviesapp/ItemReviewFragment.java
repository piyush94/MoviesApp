package com.genesis.moviesapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.genesis.moviesapp.moviereviews.data.MovieReviews;
import com.genesis.moviesapp.moviereviews.remote.GetReviews;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemReviewFragment extends Fragment {

    @Bind(R.id.hide_review)
    Button hideReview;
    @Bind(R.id.review_list)
    ListView listView;
    ArrayList<List<String>> reviewsList;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mid = getArguments().getString("mid");

        GetReviews.Factory.getInstance("http://api.themoviedb.org/3/movie/" + mid + "/").getResults().enqueue(new Callback<MovieReviews>() {
            @Override
            public void onResponse(Call<MovieReviews> call, Response<MovieReviews> response) {
                reviewsList = new ArrayList<List<String>>();
                int len = response.body().getResults().size();
                for (int i = 0; i < len; i++) {
                    List<String> temp = new ArrayList<String>();
                    temp.add(response.body().getResults().get(i).getAuthor());
                    temp.add(response.body().getResults().get(i).getContent());
                    reviewsList.add(temp);
                }
                listView.setAdapter(new ReviewListAdapter(reviewsList));
            }

            @Override
            public void onFailure(Call<MovieReviews> call, Throwable t) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_review_fragment, container, false);

        ButterKnife.bind(this, view);

        hideReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    public class ReviewListAdapter extends BaseAdapter {

        private ArrayList<List<String>> mValues;

        ReviewListAdapter(ArrayList<List<String>> items) {
            mValues = items;
        }

        @Override
        public int getCount() {
            return mValues.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public class Holder {
            TextView author;
            TextView review;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reviews, parent, false);
            Holder holder = new Holder();
            holder.author = (TextView) view.findViewById(R.id.author);
            holder.review = (TextView) view.findViewById(R.id.review);
            holder.author.setText(mValues.get(position).get(0));
            holder.review.setText(mValues.get(position).get(1));
            return view;
        }
    }

}
