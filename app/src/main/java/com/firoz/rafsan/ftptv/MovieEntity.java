package com.firoz.rafsan.ftptv;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class MovieEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String itemURL;
    public String imdbid;
    public String genre;

    public String websiteid;
    public String category;
    public String upload_date;
    public String imageURL;
    public String plot;
    public String rating;
}

