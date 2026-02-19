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
    private void scanPath(String rootPath, boolean isFM, MovieRepository repo) {
        try {
            java.util.Deque<String> stack = new java.util.ArrayDeque<>();
            stack.push(rootPath);

            while (!stack.isEmpty() && running) {

                String currentPath = stack.pop();

                List<FTPItem> items = FTPLib.listDir(currentPath, isFM );

                for (FTPItem item : items) {

                    if (!running) return;

                    if (item.isDir()) {
                        // 📂 push folder to stack (DFS)
                        if (item.getItemURL() != null) {
                            stack.push(item.getItemURL());
                        }
                    } else {

                        // 🔍 skip if already exists
                        if (repo.exists(item.getItemURL())) {
                            continue;
                        }

                        // 📡 fetch metadata
                        FTPMetadata meta = FTPLib.getMetaData(item,item.getName().substring(0,5), isFM);

                        // 💾 save entity
                        MovieEntity movie = new MovieEntity();
                        movie.name = item.getName();
                        movie.itemURL = item.getItemURL();
                        movie.isDir = false;
                        movie.isFM = isFM;

                        if (meta != null) {
                            movie.imageURL = meta.getImageURL();
                            movie.plot = meta.getPlot();
                            movie.rating = meta.getRating();
                        }

                        repo.saveMovie(movie);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
