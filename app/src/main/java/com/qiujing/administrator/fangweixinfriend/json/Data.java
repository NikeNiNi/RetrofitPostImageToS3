package com.qiujing.administrator.fangweixinfriend.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/7/27 0027.
 */


public class Data {

    public static class Form {
        public String action;

        public String method;

        public String enctype;
    }

    public Form form;

    public static class Inputs {
        public String acl;
        public String key;

        @SerializedName("X-Amz-Credential")
        public String X_Amz_Credential;

        @SerializedName("X-Amz-Algorithm")
        public String X_Amz_Algorithm;

        @SerializedName("X-Amz-Date")
        public String X_Amz_Date;

        public String Policy;

        @SerializedName("X-Amz-Signature")
        public String X_Amz_Signature;
    }

    public Inputs inputs;

    public String key;

    public String cdn;
}
