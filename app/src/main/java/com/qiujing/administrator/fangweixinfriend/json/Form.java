package com.qiujing.administrator.fangweixinfriend.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/7/27 0027.
 */
public class Form {
    @SerializedName("action")
    private String action;
    @SerializedName("method")
    private String method;
    @SerializedName("enctype")
    private String enctype;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEnctype() {
        return enctype;
    }

    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }
}
