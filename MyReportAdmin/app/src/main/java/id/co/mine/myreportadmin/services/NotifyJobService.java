package id.co.mine.myreportadmin.services;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import id.co.mine.myreportadmin.main.LoginActivity;
import id.co.mine.myreportadmin.utils.NotificationUtils;
import id.co.mine.myreportadmin.vo.NotificationVO;

public class NotifyJobService extends JobService {

    private static final String TAG = "MyFirebaseMsgingService";
    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String DATA = "data";
    private static final String ACTION_DESTINATION = "action_destination";

    @Override
    public boolean onStartJob(JobParameters job) {

        Bundle bundle = job.getExtras();
        String title = bundle.getString(TITLE);
        String message = bundle.getString(MESSAGE);

        String printOnstart = title + message;

        Log.e("printOnstart", printOnstart);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        Log.e("stop job", "job stopped");
        return false;
    }


    private void handleData(Bundle bundle) {
        String title = bundle.getString(TITLE);
        String message = bundle.getString(MESSAGE);
        String iconUrl = bundle.getString(IMAGE);
        String action = bundle.getString(ACTION);
        String actionDestination = bundle.getString(ACTION_DESTINATION);
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        notificationVO.setAction(action);
        notificationVO.setActionDestination(actionDestination);

        Intent resultIntent = new Intent(getApplicationContext(), LoginActivity.class);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();
    }
}
