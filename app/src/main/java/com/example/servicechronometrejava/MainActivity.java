package com.example.servicechronometrejava;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvTemps;
    private Button btnStart, btnStop;

    private ChronometreService chronometreService;
    private boolean serviceBound = false;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;

    // Connexion au service (Bound Service)
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChronometreService.LocalBinder binder =
                    (ChronometreService.LocalBinder) service;
            chronometreService = binder.getService();
            serviceBound = true;
            startUIUpdate();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
            stopUIUpdate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemps  = findViewById(R.id.tvTemps);
        btnStart = findViewById(R.id.btnStart);
        btnStop  = findViewById(R.id.btnStop);

        // Demande permission notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        btnStart.setOnClickListener(v -> demarrerService());
        btnStop.setOnClickListener(v -> arreterService());
    }

    private void demarrerService() {
        Intent intent = new Intent(this, ChronometreService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void arreterService() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        Intent intent = new Intent(this, ChronometreService.class);
        intent.setAction("STOP");
        startService(intent);
        stopUIUpdate();
        tvTemps.setText("00:00");
    }

    private void startUIUpdate() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (serviceBound && chronometreService != null) {
                    tvTemps.setText(
                            chronometreService.formatTemps(
                                    chronometreService.getSecondes()
                            )
                    );
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(updateRunnable);
    }

    private void stopUIUpdate() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        stopUIUpdate();
        super.onDestroy();
    }
}