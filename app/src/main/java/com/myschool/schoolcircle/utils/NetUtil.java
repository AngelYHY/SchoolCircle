package com.myschool.schoolcircle.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mr.R on 2016/7/15.
 */
public class NetUtil {
    private static String URL = "http://10.50.7.50:8080/SchoolCircleServlet/";

    public static void getDataByGet(final String type, final Handler handler) {
        DoGetRunnable runnable = new DoGetRunnable(){

            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                try {

                    URL url = new URL(URL+type);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);

                    if (connection.getResponseCode() == 200) {
                        inputStream = connection.getInputStream();
                        if (inputStream != null) {
                            String resultData = convert2String(inputStream);

                            Message message = Message.obtain();//从消息池中找到一个消息对象
                            message.what = HandlerKey.GET_SUCCESS;
                            message.obj = resultData;
                            handler.sendMessage(message);
                        } else {
                            Message message = Message.obtain();
                            message.what = HandlerKey.GET_FAIL;
                            handler.sendMessage(message);
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //关闭
                    connection.disconnect();
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(runnable).start();
    }

    public static void getDataByPost(final String type,
                                     final Map<String,Object> map, final Handler handler) {
        DoPostRunnable runnable = new DoPostRunnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    URL url = new URL(URL+type);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(10000);

                    Set<Map.Entry<String,Object>> set = map.entrySet();
                    StringBuilder stringBuilder = new StringBuilder();

                    for (Map.Entry<String, Object> entry : set) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        stringBuilder.append(key+"="+value+"&");
                    }

                    outputStream = connection.getOutputStream();
                    String s = stringBuilder.toString();
                    String params = s.substring(0,s.length()-1);
                    outputStream.write(params.getBytes());

                    if (connection.getResponseCode() == 200) {

                        inputStream = connection.getInputStream();
                        String data = null;
                        if (inputStream != null) {
                            data = convert2String(inputStream);
                            Message message = Message.obtain();
                            message.what = HandlerKey.POST_SUCCESS;
                            message.obj = data;
                            handler.sendMessage(message);
                        }

                    } else {
                        Message message = Message.obtain();
                        message.what = HandlerKey.POST_FAIL;
                        handler.sendMessage(message);
                    }

                } catch (SocketTimeoutException e) {
                    Message message = Message.obtain();
                    message.what = HandlerKey.TIMEOUT;
                    handler.sendMessage(message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //关闭
                    connection.disconnect();
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        new Thread(runnable).start();
    }

    //输入流转化为字符串
    private static String convert2String(InputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;

        try {

            while ((i=inputStream.read()) != -1){
                baos.write(i);
            }

            return baos.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    abstract static class DoGetRunnable implements Runnable{};
    abstract static class DoPostRunnable implements Runnable{};

}
