package com.abroad.crawllibrary.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;


class CommonUtil {

    static String getNonNullText(String text) {
        return TextUtils.isEmpty(text) ? "" : text;
    }

    //检查权限是否存在
    static boolean haveSelfPermission(Context paramContext, String paramString) {
        return (ActivityCompat.checkSelfPermission(paramContext, paramString) == PackageManager.PERMISSION_GRANTED);
    }
}
