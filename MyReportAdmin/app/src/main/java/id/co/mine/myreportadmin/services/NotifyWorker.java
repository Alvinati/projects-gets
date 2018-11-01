package id.co.mine.myreportadmin.services;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import id.co.mine.myreportadmin.main.LoginActivity;
import id.co.mine.myreportadmin.utils.NotificationUtils;
import id.co.mine.myreportadmin.vo.NotificationVO;

public class NotifyWorker extends Worker {

    private static final String TITLE = "title";
    private static final String MESSAGE = "message";

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Worker.Result doWork() {

        String title = getInputData().getString(TITLE);
        String message = getInputData().getString(MESSAGE);
        String printini = title + message;

        handleData(title, message);

        return Worker.Result.SUCCESS;
    }

    private void handleData(String title, String message) {

        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);

        Intent resultIntent = new Intent(getApplicationContext(), LoginActivity.class);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();
    }
}
