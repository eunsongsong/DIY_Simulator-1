package com.example.diy_simulator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class PushMessage extends Service {

    NotificationManager Notifi_M;
    ServiceThread thread;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //서비스 시작시 작업
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }
    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하기 위해 null을 넣어줌
    }


    // 푸시 알림 보내기
    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            String channelId = "channel";
            String channelName = "Channel Name";

            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //채널 생성
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                notifManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            Intent notificationIntent = new Intent(getApplicationContext(), OrderActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            int requestID = (int) System.currentTimeMillis();

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle("DIY-Simulator")
                    .setContentText("읏차")
                    .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                    .setAutoCancel(true) // 알림 터치시 반응 후 삭제
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.logo_white_mint)  //아이콘 설정
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo_white_mint))
                    .setContentIntent(pendingIntent);

            notifManager.notify(0, builder.build());
        };
    }
}