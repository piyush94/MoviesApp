package com.genesis.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieListParcel implements Parcelable {

    String movieId;
    String posterPath;
    String backdropPath;
    String title;
    String releaseDate;
    String rating;
    String overview;
    String trailerUrl;
    Integer SAVED = 0;
    byte[] poster;
    byte[] backdrop;


    MovieListParcel(String[] movieDetails, Integer saved, byte[] pp, byte[] bp) {

        this.movieId = movieDetails[0];
        this.posterPath = movieDetails[1];
        this.backdropPath = movieDetails[2];
        this.title = movieDetails[3];
        this.releaseDate = movieDetails[4];
        this.rating = movieDetails[5];
        this.overview = movieDetails[6];
        this.trailerUrl = movieDetails[7];
        this.SAVED = saved;
        this.poster = new byte[pp.length];
        this.poster = pp;
        this.backdrop = new byte[bp.length];
        this.backdrop = bp;

    }

    String getMovieId() {
        return this.movieId;
    }

    String getPosterPath() {
        return this.posterPath;
    }

    String getBackdropPath() {
        return this.backdropPath;
    }

    String getTitle() {
        return this.title;
    }

    String getReleaseDate() {
        return this.releaseDate;
    }

    String getRating() {
        return this.rating;
    }

    String getOverview() {
        return this.overview;
    }

    String getTrailerUrl() {
        return this.trailerUrl;
    }

    void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    byte[] getPoster() {
        return this.poster;
    }

    byte[] getBackdrop() {
        return this.backdrop;
    }

    Integer getSAVED() {
        return this.SAVED;
    }

    void setPoster(byte[] poster) {
        this.poster = new byte[poster.length];
        this.poster = poster;
    }

    void setBackdrop(byte[] backdrop) {
        this.backdrop = new byte[backdrop.length];
        this.backdrop = backdrop;
    }

    void setSAVED(Integer SAVED) {
        this.SAVED = SAVED;
    }

    protected MovieListParcel(Parcel in) {
        movieId = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        rating = in.readString();
        overview = in.readString();
        trailerUrl = in.readString();
        SAVED = in.readInt();
        poster = new byte[in.readInt()];
        in.readByteArray(poster);
        backdrop = new byte[in.readInt()];
        in.readByteArray(backdrop);
    }

    public static final Creator<MovieListParcel> CREATOR = new Creator<MovieListParcel>() {
        @Override
        public MovieListParcel createFromParcel(Parcel in) {
            return new MovieListParcel(in);
        }

        @Override
        public MovieListParcel[] newArray(int size) {
            return new MovieListParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(rating);
        dest.writeString(overview);
        dest.writeString(trailerUrl);
        dest.writeInt(SAVED);
        dest.writeInt(poster.length);
        dest.writeByteArray(poster);
        dest.writeInt(backdrop.length);
        dest.writeByteArray(backdrop);
    }
}
