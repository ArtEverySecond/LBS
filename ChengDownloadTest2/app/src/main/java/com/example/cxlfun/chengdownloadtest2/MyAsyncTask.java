package com.example.cxlfun.chengdownloadtest2;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cxlfun on 2017/4/14.
 */

public class MyAsyncTask extends AsyncTask<String,Integer,Integer> {
    private static  final int TYPE_DOWNLOAD_CANCEL =0;
    private static  final int TYPE_DOWNLOAD_PAUSE =1;
    private static  final int TYPE_DOWNLOAD_SUCESS =2;
    private static  final int TYPE_DOWNLOAD_FAILED =3;

    private DownloadListener mDownloadListener;
    private int lastProgerss ;

    private boolean isCance = false;
    private boolean isPause = false;



    public MyAsyncTask(DownloadListener downloadListener) {
        super();
        this.mDownloadListener = downloadListener;

    }

    @Override
    protected Integer doInBackground(String... params) {
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        File downloadFile = null;
        long downloadedLength = 0;
        try {
            String donwloadUrl = params[0];
            String fileName = donwloadUrl.substring(donwloadUrl.lastIndexOf("/") + 1);
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
             downloadFile = new File(directory + "/" + fileName);

            if (downloadFile.exists()){
                 downloadedLength = downloadFile.length();
            }
            long contentLength = getContentLength(donwloadUrl);
            if (contentLength == 0 ){
                return TYPE_DOWNLOAD_FAILED;
            }else if (contentLength == downloadedLength){
                return TYPE_DOWNLOAD_SUCESS;
            }

            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE","bytes="  + downloadedLength + "-")
                    .url(donwloadUrl)
                    .build();
            Response response = okHttpClient.newCall(request).execute();

            if (response != null){
                 inputStream = response.body().byteStream();
                 randomAccessFile = new RandomAccessFile(downloadFile, "rw");
                 randomAccessFile.seek(downloadedLength);

                byte[] b = new byte[1024];
                int len ;
                int total = 0;

                while ((len = inputStream.read(b)) != -1){
                    if (isCance){
                        return TYPE_DOWNLOAD_CANCEL;
                    }else if (isPause){
                        return TYPE_DOWNLOAD_PAUSE;
                    }else {
                        randomAccessFile.write(b,0,len);

                        total += len;

                        int progress = (int) ((total + downloadedLength) * 100 /contentLength);
                        publishProgress(progress);
                    }


                }
                response.body().close();
                return TYPE_DOWNLOAD_SUCESS;


            }




        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
                if (randomAccessFile != null){
                    randomAccessFile.close();
                }
                if (isCance && downloadFile.exists()){
                    downloadFile.delete();
                }

            } catch (IOException e) {
                e.printStackTrace();

            }

        }
        return TYPE_DOWNLOAD_FAILED;



    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response != null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }

        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Integer progress = values[0];
        if (progress > lastProgerss){
            mDownloadListener.onDowloadProgerss(progress);
            lastProgerss = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPE_DOWNLOAD_PAUSE :
                mDownloadListener.onDownloadPause();
            break;
            case TYPE_DOWNLOAD_CANCEL :
                mDownloadListener.onDownloadCancel();
            break;
            case TYPE_DOWNLOAD_SUCESS:
                mDownloadListener.onDownloadSucess();
            break;
            case TYPE_DOWNLOAD_FAILED :
                mDownloadListener.onDownloadFailed();
            break;

            default:
                break;
        }
    }

    public void onPause(){
        isPause = true;
    }
    public void onCance(){
        isCance = true;
    }

}
