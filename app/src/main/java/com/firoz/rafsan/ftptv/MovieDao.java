package com.firoz.rafsan.ftptv;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies WHERE itemURL = :url LIMIT 1")
    MovieEntity findByUrl(String url);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MovieEntity movie);
}
