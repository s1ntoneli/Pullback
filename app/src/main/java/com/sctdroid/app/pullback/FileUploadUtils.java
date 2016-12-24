package com.sctdroid.app.pullback;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by lixindong on 16/12/25.
 */
public class FileUploadUtils {
    public static final String URL
            = "http://192.168.1.117:8088/fileUpload";

    public static void updateFiles(final Context context, String id, List<Uri> uris, AsyncHttpResponseHandler handler) {
        //服务器端地址
        //手机端要上传的文件，首先要保存你手机上存在该文件

        AsyncHttpClient httpClient = new AsyncHttpClient();

        RequestParams param = new RequestParams();
        try {
            if (uris != null) {
                File[] files = new File[uris.size()];
                for (int i = 0; i < uris.size(); i++) {
                    files[i] = new File(uris.get(i).getPath());
                }
                param.put("file", files);
                param.put("id", id);
            }

            httpClient.post(URL, param, handler);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "上传文件不存在！", Toast.LENGTH_LONG).show();
        }
    }

    public static void updateFile(Context context, String mId, String mFilePath, AsyncHttpResponseHandler handler) {
        AsyncHttpClient httpClient = new AsyncHttpClient();

        RequestParams param = new RequestParams();
        try {
            if (!TextUtils.isEmpty(mFilePath)) {
                File file = new File(mFilePath);
                param.put("file", file);
                param.put("id", mId);
            }

            httpClient.post(URL, param, handler);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "上传文件不存在！", Toast.LENGTH_LONG).show();
        }
    }
}
