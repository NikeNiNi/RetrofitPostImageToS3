package com.qiujing.administrator.fangweixinfriend.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiujing.administrator.fangweixinfriend.Bimp;
import com.qiujing.administrator.fangweixinfriend.adapter.ImageAdapter;
import com.qiujing.administrator.fangweixinfriend.R;
import com.qiujing.administrator.fangweixinfriend.S3UploadService;
import com.qiujing.administrator.fangweixinfriend.json.Data;
import com.qiujing.administrator.fangweixinfriend.json.S3RequestParams;

import java.io.ByteArrayOutputStream;
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

public class RetrofitUploadImageToS3Activity extends AppCompatActivity implements Callback<S3RequestParams> {


    private Call<S3RequestParams> call;
    private S3UploadService s3UploadService;
    private LinearLayout progress;
    private OkHttpClient client;
    private TextView tvProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_s3);
        progress = (LinearLayout) findViewById(R.id.ll_retrofit);
        tvProgress = (TextView) findViewById(R.id.tv_progress_retrofit);
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
        int i = 0;
        for (String filePath: ImageAdapter.mSelectImg){
            i++;
            tvProgress.setText("正在上传第"+i+"张图片");
            Bitmap bmp = null;
            try {
                bmp = Bimp.revisionImageSize(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            boolean compress = bmp.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            bmp.recycle();
            String fileName = uuid.toString() + ".jpg";
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", fileName, requestFile);
            Log.i("test","file.length()"+compress+byteArray.length);

            Call<S3RequestParams> call1 =
                    s3UploadService.uploadImage(form.action,
                            RequestBody.create(MediaType.parse("text/plain"),inputs.acl),
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
                    Log.i("test","body....."+response.body().toString().length());
                    Data data = response.body().data;
                    Toast.makeText(RetrofitUploadImageToS3Activity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                    Log.i("test","body....."+response.body().toString().length());
                }

                @Override
                public void onFailure(Call<S3RequestParams> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.i("test","!!!!!!!!!..."+call.toString()+","+t.toString());
                    Toast.makeText(RetrofitUploadImageToS3Activity.this, "上传失败！"+call.toString()+","+t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onFailure(Call<S3RequestParams> call, Throwable t) {
        Log.i("test","!!!!!!!!!"+call.toString()+","+t.toString());
    }
}
