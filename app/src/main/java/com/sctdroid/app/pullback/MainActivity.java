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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int SELECT_ORIGINAL_PIC = 1;
    private static final int CAPTURE_QR_CODE = 0;
    private TextView uploadInfo;
    private TextView resultTv;
    private ImageView selectedPhotoIv;
    private RecyclerView mRecyclerView;
    private ContentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadInfo = (TextView) findViewById(R.id.upload_info);
        resultTv = (TextView) findViewById(R.id.tv_result);
        selectedPhotoIv = (ImageView) findViewById(R.id.iv_selected_photo);
        initRecyclerView();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (action.equals(Intent.ACTION_SEND_MULTIPLE) && type.equals("image/*")) {
//            Uri uri= intent.getParcelableExtra(Intent.EXTRA_STREAM);
            //接收多张图片
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (uris == null) return;
            mAdapter.updateData(uris);
            mRecyclerView.setVisibility(View.VISIBLE);
            /*FileUploadUtils.updateFiles(this, uris, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    uploadInfo.setText("Success " + statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    uploadInfo.setText("onFailure " + statusCode);
                }
            });*/
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
        List<Uri> uris = mAdapter.getData();
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
        } else
        if (uris != null && uris.size() > 0) {
            FileUploadUtils.updateFiles(this, mId, uris, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    uploadInfo.setText("Success " + statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    uploadInfo.setText("onFailure " + statusCode);
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

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContentAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mDesc;
        private ImageView mImage;
        public ViewHolder(LayoutInflater inflater, int layoutId) {
            super(inflater.inflate(layoutId, null));
            mDesc = (TextView) itemView.findViewById(R.id.desc);
            mImage = (ImageView) itemView.findViewById(R.id.image);
        }

        public void bind(Uri uri) {
            Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
            mImage.setImageBitmap(bitmap);
            mDesc.setText(uri.getLastPathSegment());
        }
    }

    private static class ContentAdapter extends RecyclerView.Adapter {
        private Context mContext;
        private List<Uri> mData;

        public ContentAdapter(Context context) {
            mContext = context;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext), R.layout.listitem_gallery);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder;
            if (holder instanceof ViewHolder) {
                viewHolder = (ViewHolder) holder;
                viewHolder.bind(getItem(position));
            }
        }

        private Uri getItem(int position) {
            return position >= 0 && position < getItemCount() ? mData.get(position) : null;
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        public void updateData(List<Uri> data) {
            mData = data;
            notifyDataSetChanged();
        }

        public List<Uri> getData() {
            return mData;
        }
    }
}

