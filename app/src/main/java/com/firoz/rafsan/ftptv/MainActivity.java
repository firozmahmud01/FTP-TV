package com.firoz.rafsan.ftptv;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import kotlin.Triple;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Thread(() -> {
            try {
                List<FTPItem> xxxx = FTPLib.listDir("http://fmftp.net/data/disk-1/movies/hindidub/777%20Charlie%20%282022%29/", true);
                FTPMetadata dd = FTPLib.getMetaData(xxxx.get(0), xxxx.get(0).getName().substring(0, 5), true);
                int a = 10;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();


    }
}