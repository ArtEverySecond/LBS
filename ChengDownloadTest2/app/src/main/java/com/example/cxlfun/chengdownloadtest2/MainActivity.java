package com.example.cxlfun.chengdownloadtest2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MyDownloadService.MyBinder myBinder;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder= (MyDownloadService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start= (Button) findViewById(R.id.main_start_bt);
        Button pause= (Button) findViewById(R.id.main_pause_bt);
        Button cancel= (Button) findViewById(R.id.main_cancel_bt);

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);

        Intent intent = new Intent(this, MyDownloadService.class);
        startService(intent);

        bindService(intent,connection,BIND_AUTO_CREATE);

        if (( ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }


    }

    @Override
    public void onClick(View v) {
        if (myBinder == null){
            return;
        }
        switch (v.getId()){
            case  R.id.main_start_bt:
                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                myBinder.startDownloade(url);
            break;
            case  R.id.main_pause_bt:

                myBinder.pauseDonwnload();
            break;
            case  R.id.main_cancel_bt:

                myBinder.cancelDownload();
            break;


            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1 :
                if (grantResults[0] > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                }

            break;


            default:
                break;
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
