package com.qiujing.administrator.fangweixinfriend.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qiujing.administrator.fangweixinfriend.Bimp;
import com.qiujing.administrator.fangweixinfriend.adapter.ImageAdapter;
import com.qiujing.administrator.fangweixinfriend.R;
import com.qiujing.administrator.fangweixinfriend.S3UploadService;
import com.qiujing.administrator.fangweixinfriend.json.Data;
import com.qiujing.administrator.fangweixinfriend.json.S3RequestParams;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowPhotoActivity1 extends Activity implements Callback<S3RequestParams> {

    private ViewPager pager;
    private MyPageAdapter adapter;
    private int count;

    public List<Bitmap> bmp = new ArrayList<Bitmap>();

    private TextView imgCounts;
    private ImageView back;
    private ImageView delectImg;
    private TextView uploadImage;
    private ProgressDialog progressDialog;
    private int flag = 0;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo1);
        initView();
        initEvents();
        for (int i = 0; i < ImageAdapter.mSelectImg.size(); i++) {
            try {
                bmp.add(Bimp.revisionImageSize(ImageAdapter.mSelectImg.get(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pager.setOnPageChangeListener(pageChangeListener);
        adapter = new MyPageAdapter();// 构造adapter
        pager.setAdapter(adapter);// 设置适配器
        Intent intent = getIntent();
        int id = intent.getIntExtra("position", 0);
        pager.setCurrentItem(id);
        imgCounts.setText(pager.getCurrentItem() + 1 + "/" + ImageAdapter.mSelectImg.size());
        delectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showingDialog(pager.getCurrentItem());
            }
        });
    }

    private void initEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMainActivity();
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadDialogProgress();
                uploadImageToS3();
            }
        });
    }


    private Call<S3RequestParams> call;
    private S3UploadService s3UploadService;
    private OkHttpClient client;

    private void uploadImageToS3() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(interceptor);

        client = builder.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://test-app.cozystay.com/")
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        s3UploadService = retrofit.create(S3UploadService.class);
        call = s3UploadService.getS3RequestParams();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<S3RequestParams> call, Response<S3RequestParams> response) {
        S3RequestParams s3RequestParams = response.body();
        Data.Form form = s3RequestParams.data.form;
        Data.Inputs inputs = s3RequestParams.data.inputs;
        UUID uuid = UUID.randomUUID();
        for (String filePath : ImageAdapter.mSelectImg) {
            changeUploadDialogProgress();
            Bitmap bmp = null;
            try {
                bmp = Bimp.revisionImageSize(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            bmp.recycle();
            String fileName = uuid.toString() + ".jpg";
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", fileName, requestFile);

            Call<S3RequestParams> call1 =
                    s3UploadService.uploadImage(form.action,
                            RequestBody.create(MediaType.parse("text/plain"), inputs.acl),
                            RequestBody.create(MediaType.parse("text/plain"), s3RequestParams.data.key + fileName),
                            RequestBody.create(MediaType.parse("text/plain"), inputs.X_Amz_Credential),
                            RequestBody.create(MediaType.parse("text/plain"), inputs.X_Amz_Algorithm),
                            RequestBody.create(MediaType.parse("text/plain"), inputs.X_Amz_Date),
                            RequestBody.create(MediaType.parse("text/plain"), inputs.Policy),
                            RequestBody.create(MediaType.parse("text/plain"), inputs.X_Amz_Signature),
                            body);

            call1.enqueue(new Callback<S3RequestParams>() {
                @Override
                public void onResponse(Call<S3RequestParams> call, Response<S3RequestParams> response) {

                    progressDialog.cancel();
                    Toast.makeText(ShowPhotoActivity1.this, "上传成功！", Toast.LENGTH_SHORT).show();
//
                }


                @Override
                public void onFailure(Call<S3RequestParams> call, Throwable t) {
                    Log.i("test", "!!!!!!!!!..." + call.toString() + "," + t.toString());
                    Toast.makeText(ShowPhotoActivity1.this, "上传失败：" + call.toString() + "," + t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showUploadDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("正在初始化");
        progressDialog.setMessage("请稍候...");
        progressDialog.show();
    }

    private void changeUploadDialogProgress() {
        progressDialog.setTitle("正在上传");
        progressDialog.setMessage("请稍候...");
        progressDialog.show();

    }

    @Override
    public void onFailure(Call<S3RequestParams> call, Throwable t) {
        Toast.makeText(ShowPhotoActivity1.this, "S3GET解析失败：" + call.toString() + "," + t.toString(), Toast.LENGTH_SHORT).show();
        Log.i("test", "!!!!!!!!!" + call.toString() + "," + t.toString());
    }

    private void initView() {
        pager = (ViewPager) findViewById(R.id.viewpager);
        back = (ImageView) findViewById(R.id.iv_back_show_photo1);
        imgCounts = (TextView) findViewById(R.id.tv_img_counts_show_photo1);
        delectImg = (ImageView) findViewById(R.id.iv_delect_show_photo);
        uploadImage = (TextView) findViewById(R.id.tv_upload_image);
    }


    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(final int arg0) {// 页面选择响应函数
            count = arg0;

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。
        }

        public void onPageScrollStateChanged(int arg0) {// 滑动状态改变
            if (arg0 == 2) {
                imgCounts.setText(pager.getCurrentItem() + 1 + "/" + ImageAdapter.mSelectImg.size());
            }
        }
    };


    public void showingDialog(final int p0) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowPhotoActivity1.this);
        builder.setMessage("要删除这张照片吗？");
        builder.setTitle("提示");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ImageAdapter.mSelectImg.remove(p0);
                if (ImageAdapter.mSelectImg.size() == 0) {
                    toMainActivity();
                }
                adapter.notifyDataSetChanged();
                imgCounts.setText(pager.getCurrentItem() + 1 + "/" + ImageAdapter.mSelectImg.size());
            }
        });
        builder.create().show();
    }

    class MyPageAdapter extends PagerAdapter {


        public MyPageAdapter() {// 构造函数

        }

        public int getCount() {

            // 返回数量
            return ImageAdapter.mSelectImg.size();
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(ViewGroup container, int position) {// 返回view对象
            ImageView content = new ImageView(container.getContext());
            try {

                content.setScaleType(ImageView.ScaleType.FIT_CENTER);
                content.setImageBitmap(Bimp.revisionImageSize(ImageAdapter.mSelectImg.get(position)));
                content.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                //使图片实现可以放大缩小的功能
                PhotoViewAttacher mAttacher = new PhotoViewAttacher(content);
                container.addView(content);


            } catch (Exception e) {
                Log.e("crash", "fail", e);
            }
            return content;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    private void toMainActivity() {
        startActivity(new Intent(ShowPhotoActivity1.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            toMainActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
