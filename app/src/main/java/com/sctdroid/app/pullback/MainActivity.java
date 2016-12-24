package com.sctdroid.app.pullback;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity
{
    private TextView uploadInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadInfo = (TextView) findViewById(R.id.upload_info);

        Intent intent = getIntent();
        String action=intent.getAction();
        String type=intent.getType();
        if(action.equals(Intent.ACTION_SEND_MULTIPLE)&&type.equals("image/*")){
//            Uri uri= intent.getParcelableExtra(Intent.EXTRA_STREAM);
            //接收多张图片
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (uris == null) return;
            FileUploadUtils.updateFiles(this, uris, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    uploadInfo.setText("Success " + statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    uploadInfo.setText("onFailure " + statusCode);
                }
            });
        }
    }


}
