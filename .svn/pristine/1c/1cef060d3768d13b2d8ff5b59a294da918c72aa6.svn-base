package com.facewarrant.fw.net;


import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.util.MdTools;
import com.facewarrant.fw.util.SPUtil;

import java.util.Map;

/**
 * Created by maqing on 2017/8/11.
 * Email:2856992713@qq.com
 * RequestManager
 */
public class RequestManager {
    public static RetrofitManager mRetrofitManager;

    //public static final String mBaseUrl = "http://47.106.166.255:9080/";
    public static final String mBaseUrl = "https://test.facewarrant.com.cn/";
    public static final String mYouLocalUrl = "http://10.5.21.95:8081/";
    public static final String mGangLocalUrl = "http://10.5.63.249:1010/";
    // public static final String mInterfacePrefix = "fwms/";

    public static final String mInterfacePrefix = "fwms-gateway/";

    public static Map<String, String> encryptParams(Map<String, String> map) {
        map.put("uuid", (String) SPUtil.get(Constant.UUID, ""));
        map.put("sign", MdTools.sign_digest(map));
        return map;
    }
}
