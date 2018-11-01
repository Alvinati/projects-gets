package id.co.mine.myreportadmin.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import id.co.mine.myreportadmin.main.LoginActivity;
import id.co.mine.myreportadmin.utils.NotificationUtils;
import id.co.mine.myreportadmin.vo.NotificationVO;

public class PeriodicService extends Service {

    private Timer mTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(timerTask, 10000, 2*1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{

        }catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("Log", "Running");
        }
    };

    @Override
    public void onDestroy() {
        try {
            mTimer.cancel();
            timerTask.cancel();
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.e("servicedestroy", "destroyed, intent");
        Intent intent = new Intent("com.android.techtrainner");
        sendBroadcast(intent);
    }


}
