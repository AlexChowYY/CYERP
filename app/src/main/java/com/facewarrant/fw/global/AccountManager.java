package com.facewarrant.fw.global;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facewarrant.fw.bean.UserBean;
import com.facewarrant.fw.ui.account.LoginActivity;
import com.facewarrant.fw.util.ActivityUtil;
import com.facewarrant.fw.util.SPUtil;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class AccountManager {
    public static UserBean sUserBean;


    public static void  loginOut(Context context){
        SPUtil.remove(Constant.USER);
        AccountManager.sUserBean = null;
        context.startActivity(new Intent(context, LoginActivity.class));
        ActivityUtil.getInstance().finishAllActivityExcept(LoginActivity.class);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }

    }


}
