package com.facewarrant.fw.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.facewarrant.fw.util.LogUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "PushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("cn.jpush.android.intent.REGISTRATION")){
            LogUtil.e(TAG, "chnegggg");
        }
        Bundle bundle = intent.getExtras();
        String registerId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);

    }
}
