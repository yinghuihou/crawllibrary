package com.abroad.crawllibrary.main;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CrawlMainHandler {

    private static Context mContext;
    private static String ImeIValue = "";
    private static String locationValue = "";
    private static String gaidValue = "";

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 获取全局引用值
     */
    static Context getApplication() {
        return mContext;
    }

    public static void setImeIValue(String value) {
        CrawlMainHandler.ImeIValue = value;
    }

    static String getImeIValue() {
        return ImeIValue;
    }

    public static void setLocationInfo(String location) {
        CrawlMainHandler.locationValue = location;
    }

    public static void setGAID(String value) {
        CrawlMainHandler.gaidValue = value;
    }

    static String getGaidValue() {
        return gaidValue;
    }

    //获取联系人信息
    public static String getContactList() throws Exception {
        if (mContext == null) {
            throw new Exception("crawlHandler not be inited!");
        }
        return CommonUtil.getContactList().toString();
    }

    //获取APPList信息
    public static String getAppList() throws Exception {
        if (mContext == null) {
            throw new Exception("crawlHandler not be inited!");
        }
        JSONArray appsJsonArray = DeviceUtils.getAppList(mContext);
        if (appsJsonArray == null || appsJsonArray.length() <= 0) {
            appsJsonArray = DeviceUtils.getAppList2(mContext);
        }
        return appsJsonArray.toString();
    }

    //获取设备信息参数
    public static String getDeviceInfo() throws Exception {
        if (mContext == null) {
            throw new Exception("crawlHandler not be inited!");
        }

        JSONObject deviceInfo = new JSONObject();
        try {
            deviceInfo.put("hardware", DeviceUtils.getHardWareInfo());
            deviceInfo.put("location", locationValue);
            deviceInfo.put("storage", FileSizeUtil.getStorageInfo());
            deviceInfo.put("general_data", DeviceUtils.getGeneralData());
            deviceInfo.put("other_data", DeviceUtils.getOtherData());
            deviceInfo.put("network", DeviceUtils.getNetworkData());
            deviceInfo.put("battery_status", DeviceUtils.getBatteryData());
            deviceInfo.put("audio_external", DeviceUtils.getAudioExternalNumber());
            deviceInfo.put("audio_internal", DeviceUtils.getAudioInternalNumber());
            deviceInfo.put("images_external", DeviceUtils.getImagesExternalNumber());
            deviceInfo.put("images_internal", DeviceUtils.getImagesInternalNumber());
            deviceInfo.put("video_external", DeviceUtils.getVideoExternalNumber());
            deviceInfo.put("video_internal", DeviceUtils.getVideoInternalNumber());
            deviceInfo.put("download_files", DeviceUtils.getDownloadFileNumber());
            deviceInfo.put("contact_group", DeviceUtils.getContactsGroupNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deviceInfo.toString();
    }
}
