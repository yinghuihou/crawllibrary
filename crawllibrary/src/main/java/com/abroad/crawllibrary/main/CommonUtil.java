package com.abroad.crawllibrary.main;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;


public class CommonUtil {

    static String getNonNullText(String text) {
        return TextUtils.isEmpty(text) ? "" : text;
    }

    //检查权限是否存在
    static boolean haveSelfPermission(Context paramContext, String paramString) {
        return (ActivityCompat.checkSelfPermission(paramContext, paramString) == PackageManager.PERMISSION_GRANTED);
    }

    static JSONArray getContactList() {
        JSONArray jSONArray = new JSONArray();
        try {
            if (haveSelfPermission(CrawlMainHandler.getApplication(), "android.permission.READ_CONTACTS")) {
                Uri uri = ContactsContract.Contacts.CONTENT_URI;
                ContentResolver contentResolver = CrawlMainHandler.getApplication().getContentResolver();
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    JSONObject jSONObject = new JSONObject();
                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String lastTimeContacted = cursor.getString(cursor.getColumnIndex("last_time_contacted"));
                    String timeContacted = cursor.getString(cursor.getColumnIndex("times_contacted"));
                    String name = cursor.getString(cursor.getColumnIndex("display_name"));
                    String updateTime = cursor.getString(cursor.getColumnIndex("contact_last_updated_timestamp"));

                    StringBuilder stringBuffer = new StringBuilder();
                    Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                    Cursor cursor1 = contentResolver.query(uri1, null, "contact_id = " + id, null, null);
                    ArrayList<String> phoneNumberList = new ArrayList();
                    while (cursor1 != null && cursor1.moveToNext()) {
                        String number = cursor1.getString(cursor1.getColumnIndex("data1"));
                        if (!phoneNumberList.contains(number)) {
                            phoneNumberList.add(number);
                        }
                    }
                    if (phoneNumberList.size() > 0) {
                        int i = 0;
                        while (i < phoneNumberList.size()) {
                            stringBuffer.append(phoneNumberList.get(i));
                            stringBuffer.append(",");
                            i++;
                        }
                    }

                    String phoneNumber = stringBuffer.toString();

                    //去掉最后一个分号，每个号码最后都会带有一个
                    if (phoneNumber.contains(",")) {
                        phoneNumber = stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString();
                    }

                    // 把电话号码中的  -  符号 替换成空格
                    phoneNumber = phoneNumber.replaceAll("-", " ");
                    // 空格去掉  为什么不直接-替换成"" 因为测试的时候发现还是会有空格 只能这么处理
                    phoneNumber = phoneNumber.replaceAll(" ", "");

                    jSONObject.put("contact_display_name", name);
                    jSONObject.put("number", getNonNullText(phoneNumber));
                    jSONObject.put("up_time", getNonNullText(updateTime));
                    jSONObject.put("last_time_contacted", getNonNullText(lastTimeContacted));
                    jSONObject.put("times_contacted", timeContacted);
                    jSONArray.put(jSONObject);

                    if (cursor1 != null && !cursor1.isClosed()) {
                        cursor1.close();
                    }
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            return jSONArray;
        }
        return jSONArray;
    }
}
