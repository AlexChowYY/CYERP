package com.facewarrant.fw.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

/**
 * Created by licrynoob on 2016/guide_2/26 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 弹窗工具类
 */
public class DialogUtil {

    /**
     * 进度弹窗
     */
    private static ProgressDialog sProgressDialog = null;

    /**
     * 显示进度弹窗
     *
     * @param context 上下文
     * @param message message
     */
    public static void showProgress(Context context, String message) {
        if (sProgressDialog == null) {
            sProgressDialog = new ProgressDialog(context);
        }
        if (!TextUtils.isEmpty(message)) {
            sProgressDialog.setMessage(message);
        }
        sProgressDialog.show();
    }

    /**
     * 显示进度弹窗 默认显示 加载中
     *
     * @param context 上下文
     */
    public static void showProgress(Context context) {
        showProgress(context, "加载中");
    }

    /**
     * 显示一个无法被取消的弹窗
     *
     * @param context 上下文
     * @param message 信息
     */
    public static void showUnCancelableProgress(Context context, String message) {
        if (sProgressDialog == null) {
            sProgressDialog = new ProgressDialog(context);
        }
        if (!TextUtils.isEmpty(message)) {
            sProgressDialog.setMessage(message);
        }
        //窗口外无法取消
        sProgressDialog.setCanceledOnTouchOutside(false);
        //返回键无法取消
        sProgressDialog.setCancelable(false);
        sProgressDialog.show();
    }

    /**
     * 隐藏进度弹窗
     */
    public static void hideProgress() {
        if (sProgressDialog != null) {
            sProgressDialog.dismiss();
            sProgressDialog = null;
        }
    }

    /**
     * 获取Builder
     *
     * @param context 上下文
     * @return Builder
     */
    public static AlertDialog.Builder getBuilder(Context context) {
        return new AlertDialog.Builder(context);
    }

    /**
     * 显示文本弹窗
     *
     * @param context 上下文
     * @param message 信息
     */
    public static void showMDialog(Context context, String message) {
        getBuilder(context)
                .setMessage(message)
                .show();
    }

    /**
     * 显示确认弹窗
     *
     * @param context   context
     * @param message   信息
     * @param pListener 确认
     */
    public static void showPDialog(Context context, String message, DialogInterface.OnClickListener pListener) {
        getBuilder(context)
                .setMessage(message)
                .setPositiveButton("确认", pListener)
                .show();
    }

    /**
     * 显示确认取消弹窗
     *
     * @param context   context
     * @param message   信息
     * @param pListener 确认
     * @param nListener 取消
     */
    public static void showUnCancelablePNDialog(
            Context context,
            String message,
            DialogInterface.OnClickListener pListener,
            DialogInterface.OnClickListener nListener) {
        getBuilder(context)
                .setMessage(message)
                .setPositiveButton("确认", pListener)
                .setNegativeButton("取消", nListener).setCancelable(false)
                .show();
    }

    public static void showPNDialog(
            Context context,
            String message,
            DialogInterface.OnClickListener pListener,
            DialogInterface.OnClickListener nListener) {
        getBuilder(context)
                .setMessage(message)
                .setPositiveButton("确认", pListener)
                .setNegativeButton("取消", nListener)
                .show();
    }


    /**
     * 显示弹窗
     *
     * @param context   context
     * @param title     标题
     * @param message   信息
     * @param positive  确认
     * @param pListener 确认接口
     * @param negative  取消
     * @param nListener 取消接口
     */
    public static void showDialog(
            Context context,
            String title,
            String message,
            String positive, DialogInterface.OnClickListener pListener,
            String negative, DialogInterface.OnClickListener nListener) {
        AlertDialog.Builder builder = getBuilder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (!TextUtils.isEmpty(positive) && pListener != null) {
            builder.setPositiveButton(positive, pListener);
        }
        if (!TextUtils.isEmpty(negative) && nListener != null) {
            builder.setNegativeButton(negative, nListener);
        }
        builder.show();
    }


    /**
     * 显示弹窗
     *
     * @param context   context
     * @param title     标题
     * @param message   信息
     * @param positive  确认
     * @param pListener 确认接口
     * @param negative  取消
     * @param nListener 取消接口
     */
    public static void showUnCancelableDialog(
            Context context,
            String title,
            String message,
            String positive, DialogInterface.OnClickListener pListener,
            String negative, DialogInterface.OnClickListener nListener) {
        AlertDialog.Builder builder = getBuilder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        if (!TextUtils.isEmpty(positive) && pListener != null) {
            builder.setPositiveButton(positive, pListener).setCancelable(false);
        }
        if (!TextUtils.isEmpty(negative) && nListener != null) {
            builder.setNegativeButton(negative, nListener).setCancelable(false);
        }
        builder.show();
    }


}
