package com.facewarrant.fw.util;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.facewarrant.fw.global.Constant.secretKey;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class MdTools {
    public static String sign_digest(Map<String,String> map){
        Set<String> keyset = map.keySet();
        List<String> keylist = new ArrayList<String>(keyset);
        Collections.sort(keylist);
        StringBuffer sb = new StringBuffer();
        for (int i=0;i<keylist.size();i++){
            String key = keylist.get(i);
            String value = map.get(key);
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                sb.append(key).append("=").append(value).append("&");
            }
        }
        sb.append("secret").append("=").append(secretKey);
        String sign_string = sb.toString();
        return  getHash(sign_string,"MD5");
    }
    private static String getHash(String source, String hashType) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(hashType);
            md5.update(source.getBytes("UTF-8"));
            for (byte b : md5.digest()) {
                sb.append(String.format("%02X", b)); // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
