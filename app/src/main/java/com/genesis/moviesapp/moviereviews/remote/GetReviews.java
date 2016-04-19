package com.genesis.moviesapp.moviereviews.remote;

import com.genesis.moviesapp.moviereviews.data.MovieReviews;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by Piyush on 02-Apr-16.
 */
public interface GetReviews {
    @GET("reviews?api_key=YOUR_API_KEY")
    Call<MovieReviews> getResults();

    class Factory{
        public static GetReviews service;

        public static GetReviews getInstance(String url){

            String BASE_URL = url;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(GetReviews.class);
            return service;

        }

    }
}
