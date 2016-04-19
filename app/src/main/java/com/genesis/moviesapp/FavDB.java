package com.genesis.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FavDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "favDB";

    private static final String TABLE_MID = "table_mid";

    private static final String KEY_MID = "mid";
    private static final String KEY_PP = "p";
    private static final String KEY_BP = "b";
    private static final String KEY_P = "poster";
    private static final String KEY_B = "backdrop";
    private static final String KEY_TITLE = "title";
    private static final String KEY_RD = "release_date";
    private static final String KEY_R = "rating";
    private static final String KEY_OV = "overview";
    private static final String KEY_TR = "trailer";

    public FavDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_MID = "CREATE TABLE " + TABLE_MID + "(" + KEY_MID + " TEXT," + KEY_PP + " TEXT," + KEY_BP + " TEXT," + KEY_P + " BLOB," + KEY_B + " BLOB," + KEY_TITLE + " TEXT," + KEY_RD + " TEXT," + KEY_R + " TEXT," + KEY_OV + " TEXT," + KEY_TR + " TEXT);";
        db.execSQL(CREATE_TABLE_MID);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MID);
        onCreate(db);

    }

    public void addMovie(MovieListParcel movie) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MID, movie.getMovieId());
        values.put(KEY_PP, movie.getPosterPath());
        values.put(KEY_BP, movie.getBackdropPath());
        values.put(KEY_P, movie.getPoster());
        values.put(KEY_B, movie.getBackdrop());
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_RD, movie.getReleaseDate());
        values.put(KEY_R, movie.getRating());
        values.put(KEY_OV, movie.getOverview());
        values.put(KEY_TR, movie.getTrailerUrl());

        db.insert(TABLE_MID, null, values);
        db.close();

    }

    public void delMovie(String movieId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MID, KEY_MID + "=" + movieId, null);
        db.close();

    }

    public boolean getMovie(String movieId) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MID, new String[]{KEY_MID}, KEY_MID + "=?", new String[]{movieId}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }

        cursor.close();
        db.close();
        return false;

    }

    public ArrayList<MovieListParcel> getFavMovies() {

        ArrayList<MovieListParcel> favMovies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MID;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String[] movies = new String[8];
                movies[0] = cursor.getString(0);
                movies[1] = cursor.getString(1);
                movies[2] = cursor.getString(2);
                movies[3] = cursor.getString(5);
                movies[4] = cursor.getString(6);
                movies[5] = cursor.getString(7);
                movies[6] = cursor.getString(8);
                movies[7] = cursor.getString(9);
                favMovies.add(new MovieListParcel(movies, 1, cursor.getBlob(3), cursor.getBlob(4)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return favMovies;

    }

}
