package com.firoz.rafsan.ftptv;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncService extends Service {

    private ExecutorService executor;
    private volatile boolean running = true;

    // 🔴 YOUR ROOT PATHS
    private static final String FM_ROOT = "https://fmftp.net/data/";
    private static final String TIMEPASS_ROOT = "http://ftp.timepassbd.live/";

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this::syncLoop);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        executor.shutdownNow();
        super.onDestroy();
    }

    // ================================
    // 🔁 MAIN LOOP
    // ================================
    private void syncLoop() {
        MovieRepository repo = new MovieRepository(getApplicationContext());

        while (running) {
            try {
                // Scan both roots
                scanPath(FM_ROOT, true, repo);
                scanPath(TIMEPASS_ROOT, false, repo);

                // Sleep 30 minutes
                Thread.sleep(30 * 60 * 1000);

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    // ================================
    // 📂 RECURSIVE SCAN
    // ================================
    private void scanPath(String path, boolean isFM, MovieRepository repo) {
        try {
            List<FTPItem> items = FTPLib.listDir(path, isFM );

            for (FTPItem item : items) {

                if (!running) return;

                if (item.isDir()) {
                    // 🔁 recurse into folder
                    scanPath(item.getItemURL(), isFM, repo);
                } else {

                    // 🔍 check database
                    if (repo.exists(item.getItemURL())) {
                        continue;
                    }

                    // 📡 fetch metadata
                    FTPMetadata meta = FTPLib.getMetaData(item, isFM);

                    // 💾 save
                    MovieEntity movie = new MovieEntity();
                    movie.name = item.getName();
                    movie.itemURL = item.getItemURL();
                    movie.isDir = false;
                    movie.isFM = isFM;

                    movie.imageURL = meta.getImageURL();
                    movie.plot = meta.getPlot();
                    movie.rating = meta.getRating();

                    repo.saveMovie(movie);
                }
            }

        } catch (Exception e) {

        }
    }
}
