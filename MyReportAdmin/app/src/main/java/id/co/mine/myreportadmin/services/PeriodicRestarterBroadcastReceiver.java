package id.co.mine.myreportadmin.services;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class PeriodicRestarterBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("PeriodicBR", "Service Stop ooooppsss");
        context.startService(new Intent(context, PeriodicService.class));
    }
}
