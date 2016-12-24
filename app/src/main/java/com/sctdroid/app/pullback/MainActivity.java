package com.sctdroid.app.pullback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int SELECT_ORIGINAL_PIC = 1;
    private static final int CAPTURE_QR_CODE = 0;
    private TextView uploadInfo;
    private TextView resultTv;
    private ImageView selectedPhotoIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadInfo = (TextView) findViewById(R.id.upload_info);
        resultTv = (TextView) findViewById(R.id.tv_result);
        selectedPhotoIv = (ImageView) findViewById(R.id.iv_selected_photo);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (action.equals(Intent.ACTION_SEND_MULTIPLE) && type.equals("image/*")) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_capture:
                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), CAPTURE_QR_CODE);
                break;
            case R.id.btn_pick:
                selectFromGallery();
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    private void submit() {
        if (!TextUtils.isEmpty(mId) && !TextUtils.isEmpty(mFilePath)) {
            FileUploadUtils.updateFile(this, mId, mFilePath, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    uploadInfo.setText("Success " + statusCode);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    uploadInfo.setText("Success " + statusCode);

                }
            });
        } else {
            Toast.makeText(this, "id or path is empty", Toast.LENGTH_LONG).show();
        }
    }

    String mId = "";
    String mFilePath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_QR_CODE && resultCode == RESULT_OK) {
            resultTv.setText(data.getStringExtra(CaptureActivity.EXTRA_RESULT));
            mId = data.getStringExtra(CaptureActivity.EXTRA_RESULT);
        } else if (requestCode == SELECT_ORIGINAL_PIC && resultCode == RESULT_OK){
            try {
                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                mFilePath = selectedImage.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
                selectedPhotoIv.setImageBitmap(bitmap);
            } catch (Exception e) {
                // TODO Auto-generatedcatch block
                e.printStackTrace();
            }
        }
    }

    private void selectFromGallery() {
        // TODO Auto-generatedmethod stub
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_PICK);//Pick an item fromthe data
        intent.setType("image/*");//从所有图片中进行选择
        startActivityForResult(intent, SELECT_ORIGINAL_PIC);
    }
}

