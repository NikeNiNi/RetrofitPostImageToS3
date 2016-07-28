package com.qiujing.administrator.fangweixinfriend.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/7/27 0027.
 */
public class Inputs {
    @SerializedName("acl")
    private String acl;
    @SerializedName("key")
    private String key;
//    private String X-Amz-Credential;
//    private String X-Amz-Algorithm;
//    private String X-Amz-Date;
@SerializedName("Policy")
    private String Policy;
//    private String X-Amz-Signature;

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = acl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPolicy() {
        return Policy;
    }

    public void setPolicy(String policy) {
        Policy = policy;
    }
}
