package com.example.cxlfun.chengdownloadtest2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

public class MyDownloadService extends Service {

    MyBinder mBinder = new MyBinder();
    private MyAsyncTask myAsyncTask;
    private String mDownloaUrl ;

    public MyDownloadService() {
    }

    DownloadListener listener = new DownloadListener() {
        @Override
        public void onDowloadProgerss(int progress) {
            getNotificationManager().notify(1,getNotification("downloading",progress));

        }

        @Override
        public void onDownloadPause() {
            myAsyncTask = null;
//            stopForeground(true);
            getNotificationManager().notify(1,getNotification("pause",-1));
            Toast.makeText(MyDownloadService.this,"pause",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDownloadCancel() {
            myAsyncTask = null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("cancel",-1));
            Toast.makeText(MyDownloadService.this,"cancel",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDownloadSucess() {
            myAsyncTask = null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("success",-1));
            Toast.makeText(MyDownloadService.this,"success",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDownloadFailed() {
            myAsyncTask = null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("failede",-1));
            Toast.makeText(MyDownloadService.this,"failede",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }


    public  class MyBinder extends Binder{

        public  void startDownloade( String downloadUrl){
            mDownloaUrl = downloadUrl;
            if (myAsyncTask == null){
                myAsyncTask = new MyAsyncTask(listener);
                myAsyncTask.execute(mDownloaUrl);
                startForeground(1,getNotification("下载",0));
                Toast.makeText(MyDownloadService.this,"downloading",Toast.LENGTH_SHORT).show();
            }


        }

        public  void pauseDonwnload(){
          if (myAsyncTask != null){

              myAsyncTask.onPause();
          }

        }

        public void cancelDownload(){
            if (myAsyncTask != null){

                myAsyncTask.onCance();
            }else if (mDownloaUrl !=null){
                String fileName = mDownloaUrl.substring(mDownloaUrl.lastIndexOf("/") + 1);
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory + "/" + fileName);
                if (file.exists()){
                    file.delete();
                }
                getNotificationManager().notify(1,getNotification("cancel",-1));
                stopForeground(true);
            }
        }



    }

    private NotificationManager getNotificationManager() {
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    private Notification getNotification(String s, int progress) {
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0) ;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentText(s);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        if (progress > 0){
            builder.setContentText(progress + "%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }


}
