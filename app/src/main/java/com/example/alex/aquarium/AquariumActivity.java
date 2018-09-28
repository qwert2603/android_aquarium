package com.example.alex.aquarium;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class AquariumActivity extends Activity {

    private AquariumView mAquariumView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final AquariumStepNotificationThread mAquariumStepNotificationThread = new AquariumStepNotificationThread();

    private Runnable mStepRunnable = new Runnable() {
        @Override
        public void run() {
            mAquariumView.step();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        super.onCreate(savedInstanceState);
        mAquariumView = new AquariumView(this);
        setContentView(mAquariumView);
        mAquariumStepNotificationThread.start();
    }

    @Override
    public void onDestroy() {
        mAquariumStepNotificationThread.makeStop();
        super.onDestroy();
    }

    private class AquariumStepNotificationThread extends Thread {
        private volatile boolean mStop = false;

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (this) {
                        if (mStop) {
                            break;
                        }
                    }
                    Thread.sleep(42);
                    mHandler.post(mStepRunnable);
                }
            } catch (Exception e) {
                Log.e("AASSDD", e.toString(), e);
            }
        }

        synchronized void makeStop() {
            mStop = true;
        }
    }

}