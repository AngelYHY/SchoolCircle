package com.myschool.schoolcircle.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.myschool.schoolcircle.config.Config;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Mr.R on 2016/7/22.
 */
public class QiniuCloudUtil {

    /**
     * 下载
     * @param strurl:下载地址图片
     * @param handler
     */
    public static void download(final String strurl, final Handler handler) {

        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap;
                try {
                    //下载资源
                    URL url = new URL(strurl);
                    InputStream inputStream = url.openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    //将资源发给主线程去更新UI
                    Message msg = Message.obtain();
                    msg.what = HandlerKey.DOWNLOAD_SUCCESS;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                    inputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 真正上传文件操作
     * @param fileDir:文件的本地地址
     * @param fileName:文件上传到七牛云的命名，传null的话云端会自动命名
     * @param token:要通过服务器端获得
     * @param handler
     * @param isProgress:是否需要反馈上传进度
     */
    public static void upLoad(String fileDir,String fileName,String token,
                              final Handler handler,boolean isProgress) {
        UploadManager uploadManager;
        uploadManager = new UploadManager();

        //File对象、或 文件路径、或 字节数组
        //指定七牛服务上的文件名，或 null
        //从服务端获取token
        //上传时候的回调函数
        if (!isProgress) {
            //不需要反馈上传进度
            uploadManager.put(fileDir, fileName, token, new UpCompletionHandler() {
                @Override
                public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                    Message msg = Message.obtain();
                    if (responseInfo.isOK()) {
                        //上传成功
                        try {
                            //获取上传到七牛的文件命名
                            String key = (String) jsonObject.get("key");
                            //通知UI刷新
                            msg.what = HandlerKey.UPLOAD_SUCCESS;
                            msg.obj = Config.QINIU_URL + key;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //上传失败
                        msg.what = HandlerKey.UPLOAD_FAIL;
                        handler.sendMessage(msg);
                    }
                }
            },null);
        } else {
            uploadManager.put(fileDir, fileName, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            Log.i("qiniu", info.toString());
                        }
                    }, new UploadOptions(null, null, false, new UpProgressHandler() {
                        @Override
                        public void progress(String s, double v) {
                            //上传进度
                            Message msg = Message.obtain();
                            msg.what = HandlerKey.UPLOAD_REFRESH;
                            msg.obj = (int)(v*100);
                            handler.sendMessage(msg);
                            Log.i("MainActivity", "progress: "+v);
                        }
                    },null));
        }
    }

    //上传
    //Config.BUCKETNAME：改为自己的空间名!!!!!!
    public static void upLoadImage(final String uri, final String fileName,
                                   final Handler mHandler) {
        RequestParams params = new RequestParams(Config.URL+"QiniuCloud");
        params.addQueryStringParameter("bucketname", Config.BUCKETNAME);
        params.setConnectTimeout(8000);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    //将图片上传到七牛云
                    QiniuCloudUtil.upLoad(uri,fileName,result,mHandler,false);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
