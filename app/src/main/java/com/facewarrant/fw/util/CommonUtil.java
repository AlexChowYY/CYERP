package com.facewarrant.fw.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.facewarrant.fw.R;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.BallPulseView;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by licrynoob on 2016/guide_2/12 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 常用工具类
 */
public class CommonUtil {
    /**
     * 文本替换工具
     */
    public static String replace(String from, String to, String source) {
        if (source == null || from == null || to == null)
            return null;
        StringBuffer bf = new StringBuffer("");
        int index = -1;
        while ((index = source.indexOf(from)) != -1) {
            bf.append(source.substring(0, index) + to);
            source = source.substring(index + from.length());
            index = source.indexOf(from);
        }
        bf.append(source);
        return bf.toString();
    }

    /**
     * 判断是否为手机号
     *
     * @param phone 手机号
     * @return true
     */
    public static boolean isPhone(String phone) {
        Pattern p;
        Matcher m;
        boolean b;
        p = Pattern.compile("^[1][0-9][0-9]{9}$");
        m = p.matcher(phone);
        b = m.matches();
        return b;
    }

    /**
     * 密码验证
     *
     * @param password
     * @return
     */
    public static boolean isPassword(String password) {
        Pattern p;
        Matcher m;
        boolean b;
        p = Pattern.compile("[A-Za-z0-9~!@#$%^&*()_+;',.:<>]{6,16}");
        m = p.matcher(password);
        b = m.matches();
        return b;
    }

    /**
     * 判断网络是否连接
     *
     * @return
     */
    public static boolean isNet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断是否是移动数据连接
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 打开网络设置界面
     */
    public static void openNetSet(Activity activity, int requestCode) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public static void showKeyBorad(Context context) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }



    /**
     * 对象转为Base64位字符串
     *
     * @param object Object
     * @return base64
     */
    public static String objectToBase64(Object object) {
        String userBase64;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            userBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            return userBase64;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Base64位字符串转为对象
     *
     * @param base64Str 64位字符串
     * @return Object
     */
    public static Object base64ToObject(String base64Str) {
        Object object;
        byte[] buffer = Base64.decode(base64Str, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            object = ois.readObject();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bais.close();
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Map 转 JsonStr
     *
     * @param map Map
     * @return JSON字符串
     */
    public static String mapToJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "null";
        }
        String json = "{";
        Set<?> keySet = map.keySet();
        for (Object key : keySet) {
            json += "\"" + key + "\":\"" + map.get(key) + "\",";
        }
        json = json.substring(1, json.length() - 2);
        json += "}";
        return json;
    }

    /**
     * JsonStr 转 Map
     *
     * @param json JSON字符串
     * @return Map
     */
    public static Map jsonToMap(String json) {
        String sb = json.substring(1, json.length() - 1);
        String[] name = sb.split("\\\",\\\"");
        String[] nn;
        Map<String, String> map = new HashMap<>();
        for (String aName : name) {
            nn = aName.split("\\\":\\\"");
            map.put(nn[0], nn[1]);
        }
        return map;
    }

    /**
     * 通过资源设置文字颜色
     */
    public static void setTextColorByRes(TextView textView, int colorRes) {
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), colorRes));
    }

    /**
     * 获取非空Str
     *
     * @param tagStr     目标Str
     * @param defaultStr 默认Str
     * @return ResultStr
     */
    public static String setNoNullStr(String tagStr, String defaultStr) {
        if (!TextUtils.isEmpty(tagStr)) {
            return tagStr;
        }
        return defaultStr;
    }

    /**
     * 去除空字符
     *
     * @param param old
     * @return new
     */
    public static String replaceBlank(String param) {
        String dest = "";
        if (!TextUtils.isEmpty(param)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(param);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 获取包信息
     *
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context) throws PackageManager.NameNotFoundException {

        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

//    public static AppVersionBean getAppVersion(Context context) {
//        AppVersionBean appVersionBean = new AppVersionBean();
//        PackageManager manager = context.getPackageManager();
//        PackageInfo info = null;
//        try {
//            info = manager.getPackageInfo(context.getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        String versionName = info.versionName;
//        int versionCode = info.versionCode;
//        appVersionBean.setVersionCode(versionCode);
//        appVersionBean.setVersionName(versionName);
//        return appVersionBean;
//    }

    /**
     * 设置TabLayout的Indicator的长度
     *
     * @param tabs
     * @param leftDip
     * @param rightDip
     */
//    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
//        Class<?> tabLayout = tabs.getClass();
//        Field tabStrip = null;
//        try {
//            tabStrip = tabLayout.getDeclaredField("mTabStrip");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        tabStrip.setAccessible(true);
//        LinearLayout llTab = null;
//        try {
//            llTab = (LinearLayout) tabStrip.get(tabs);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
//        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());
//
//        for (int i = 0; i < llTab.getChildCount(); i++) {
//            View child = llTab.getChildAt(i);
//            child.setPadding(0, 0, 0, 0);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
//            params.leftMargin = left;
//            params.rightMargin = right;
//            child.setLayoutParams(params);
//            child.invalidate();
//        }
//
//    }

    /**
     * 打开外部链接
     */
    public static void linkUrl(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static String getAndroidID(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String android_id(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public  static void  setRefreshStyle(TwinklingRefreshLayout twinklingRefreshLayout,Activity activity){
        ProgressLayout headerView = new ProgressLayout(activity);
        BallPulseView loadingView = new BallPulseView(activity);
        headerView.setColorSchemeColors(ContextCompat.getColor(activity, R.color.colorBlue));
        loadingView.setAnimatingColor(ContextCompat.getColor(activity, R.color.colorBlue));
        twinklingRefreshLayout.setHeaderView(headerView);
        twinklingRefreshLayout.setBottomView(loadingView);
    }

}
