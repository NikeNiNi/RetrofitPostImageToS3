package com.qiujing.administrator.fangweixinfriend;


import com.qiujing.administrator.fangweixinfriend.json.S3RequestParams;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public interface S3UploadService {
    @GET("/")
    Call<String> getBaidu();

    @GET("api/v1/util/s3_post")//网址下面的子目录   category表示分类，因为子目录只有一点不一样
    Call<S3RequestParams> getS3RequestParams();

    @Multipart
    @POST
    Call<S3RequestParams> uploadImage(
            @Url String url,
            @Part("acl") RequestBody acl,
            @Part("key") RequestBody key,
            @Part("X-Amz-Credential") RequestBody credential,
            @Part("X-Amz-Algorithm") RequestBody algorithm,
            @Part("X-Amz-Date") RequestBody date,
            @Part("Policy") RequestBody policy,
            @Part("X-Amz-Signature") RequestBody signature,
            @Part MultipartBody.Part file
    );

}
