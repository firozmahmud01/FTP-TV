package com.firoz.rafsan.ftptv;

import android.content.Context;

public class MovieRepository {

    private final MovieDao movieDao;

    public MovieRepository(Context context) {
        MovieDatabase db = MovieDatabase.getInstance(context);
        movieDao = db.movieDao();
    }

    public boolean exists(String url) {
        return movieDao.findByUrl(url) != null;
    }

    public void saveMovie(MovieEntity movie) {
        movieDao.insert(movie);
    }
}
