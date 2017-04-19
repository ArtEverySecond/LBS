package com.example.cxlfun.chengdownloadtest2;

/**
 * Created by cxlfun on 2017/4/14.
 */

public interface DownloadListener {
    void onDowloadProgerss(int progress);
    void onDownloadPause();
    void onDownloadCancel();
    void onDownloadSucess();
    void onDownloadFailed();
}
