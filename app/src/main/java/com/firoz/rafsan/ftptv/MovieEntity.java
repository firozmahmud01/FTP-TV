package com.firoz.rafsan.ftptv;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class MovieEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String itemURL;
    public boolean isDir;
    public boolean isFM;

    public String imageURL;
    public String plot;
    public String rating;
}

