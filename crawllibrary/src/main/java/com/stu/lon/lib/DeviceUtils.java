package com.stu.lon.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.BATTERY_SERVICE;
import static android.telephony.TelephonyManager.PHONE_TYPE_NONE;

class DeviceUtils {
    // 没有网络连接
    private static final String NETWORN_NONE = "none";
    // wifi连接
    private static final String NETWORN_WIFI = "WIFI";
    // 手机网络数据连接类型
    private static final String NETWORN_2G = "2G";
    private static final String NETWORN_3G = "3G";
    private static final String NETWORN_4G = "4G";
    private static final String NETWORN_MOBILE = "other";

    private static Context context = CrawlMainHandler.getApplication();

    static JSONObject getHardWareInfo() throws JSONException {
        JSONObject hardWareData = new JSONObject();
        hardWareData.put("device_name", CommonUtil.getNonNullText(getDriverBrand()));
        hardWareData.put("brand", CommonUtil.getNonNullText(getDriverBrand()));
        hardWareData.put("sdk_version", CommonUtil.getNonNullText(getDriverSDKVersion()));
        hardWareData.put("model", CommonUtil.getNonNullText(getDriverModel()));
        hardWareData.put("release", CommonUtil.getNonNullText(getDriverOsVersion()));
        hardWareData.put("serial_number", CommonUtil.getNonNullText(getSerialNumber()));
        hardWareData.put("physical_size", CommonUtil.getNonNullText(getScreenPhysicalSize(context)));
        return hardWareData;
    }

    static JSONObject getGeneralData() throws JSONException {
        JSONObject generalData = new JSONObject();
        generalData.put("gaid", CommonUtil.getNonNullText(getGAID()));
        generalData.put("and_id", CommonUtil.getNonNullText(getAndroidId(context)));
        generalData.put("phone_type", CommonUtil.getNonNullText(String.valueOf(getPhoneType())));
        generalData.put("mac", CommonUtil.getNonNullText(getMacAddress()));
        generalData.put("locale_iso_3_language", CommonUtil.getNonNullText(getISO3Language(context)));
        generalData.put("locale_display_language", CommonUtil.getNonNullText(getLocaleDisplayLanguage()));
        generalData.put("locale_iso_3_country", CommonUtil.getNonNullText(getISO3Country(context)));
        generalData.put("imei", CommonUtil.getNonNullText(getDeviceImeIValue(context)));
        generalData.put("phone_number", CommonUtil.getNonNullText(getCurrentPhoneNum()));
        generalData.put("network_operator_name", CommonUtil.getNonNullText(getNetWorkOperatorName()));
        generalData.put("network_type", CommonUtil.getNonNullText(getNetworkState()));
        generalData.put("time_zone_id", CommonUtil.getNonNullText(getCurrentTimeZone()));
        generalData.put("language", CommonUtil.getNonNullText(getLanguage()));
        return generalData;
    }

    static JSONObject getOtherData() throws JSONException {
        JSONObject otherData = new JSONObject();
        otherData.put("root_jailbreak", isRoot() ? "1" : "0");
        otherData.put("last_boot_time", bootTime() + "");//最后一次开机时间戳
        otherData.put("keyboard", "");//有没有外接蓝牙键盘   todo 无法获取
        otherData.put("simulator", isEmulator() ? "1" : "0");
        otherData.put("dbm", CommonUtil.getNonNullText(getMobileDbm()));//手机的信号强度
        return otherData;
    }

    public static JSONObject getNetworkData() {
        JSONObject network = new JSONObject();
        JSONObject currentNetwork = new JSONObject();
        JSONArray configNetwork = new JSONArray();

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                currentNetwork.put("bssid", wifiInfo.getBSSID());
                currentNetwork.put("ssid", wifiInfo.getSSID());
                currentNetwork.put("mac", wifiInfo.getMacAddress());
                currentNetwork.put("name", getWifiName());

                network.put("current_wifi", currentNetwork);
                network.put("IP", getWifiIP());

                List<ScanResult> configs = wifiManager.getScanResults();
                for (ScanResult scanResult : configs) {
                    JSONObject config = new JSONObject();
                    config.put("bssid", scanResult.BSSID);
                    config.put("ssid", scanResult.SSID);
                    config.put("mac", scanResult.BSSID);
                    config.put("name", scanResult.SSID);
                    configNetwork.put(config);
                }

                network.put("configured_wifi", configNetwork);
            }
        } catch (Exception e) {

        }
        return network;
    }

    static JSONObject getBatteryData() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        BatteryManager manager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        if (manager != null) {
            int dianliang = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            jSONObject.put("battery_pct", dianliang);
        }

        Intent intent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int k = intent.getIntExtra("plugged", -1);
        switch (k) {
            default:
                jSONObject.put("is_usb_charge", Integer.valueOf(0));
                jSONObject.put("is_ac_charge", Integer.valueOf(0));
                jSONObject.put("is_charging", Integer.valueOf(0));
                return jSONObject;
            case 2:
                jSONObject.put("is_usb_charge", Integer.valueOf(1));
                jSONObject.put("is_ac_charge", Integer.valueOf(0));
                jSONObject.put("is_charging", Integer.valueOf(1));
                return jSONObject;
            case 1:
                break;
        }
        jSONObject.put("is_usb_charge", Integer.valueOf(0));
        jSONObject.put("is_ac_charge", Integer.valueOf(1));
        jSONObject.put("is_charging", Integer.valueOf(1));
        return jSONObject;
    }

    private static String getGAID() {
        if (!TextUtils.isEmpty(CrawlMainHandler.getGaidValue())) {
            return CrawlMainHandler.getGaidValue();
        }

        try {
            AdvertisingIdProvider.AdInfo adInfo = AdvertisingIdProvider.getAdvertisingIdInfo(context);
            if (adInfo != null) {
                return adInfo.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getISO3Language(Context paramContext) {
        return (paramContext.getResources().getConfiguration()).locale.getISO3Language();
    }

    private static String getISO3Country(Context paramContext) {
        return (paramContext.getResources().getConfiguration()).locale.getISO3Country();
    }

    private static String getLocaleDisplayLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceImeIValue(Context paramContext) {
        if (!TextUtils.isEmpty(CrawlMainHandler.getImeIValue())) {
            return CrawlMainHandler.getImeIValue();
        }

        if (CommonUtil.haveSelfPermission(paramContext, "android.permission.READ_PHONE_STATE")) {
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    return ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).getImei();
                } else {
                    return ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                }
            } catch (Exception e) {

            }
        }
        return "";
    }

    //获取手机号码
    @SuppressLint("MissingPermission")
    private static String getCurrentPhoneNum() {
        try {
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String tel = tm.getLine1Number(); // 手机号码
                return tel;
            }
        } catch (Exception e) {

        }
        return "";
    }

    //获取屏幕大小
    private static String getScreenPhysicalSize(Context paramContext) {
        Display display = ((WindowManager) paramContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return Double.toString(Math.sqrt(Math.pow((displayMetrics.heightPixels / displayMetrics.ydpi), 2.0D) + Math.pow((displayMetrics.widthPixels / displayMetrics.xdpi), 2.0D)));
    }

    //获取外部音频数量
    static String getAudioExternalNumber() {
        int result = 0;
        if (CommonUtil.haveSelfPermission(CrawlMainHandler.getApplication(), "android.permission.READ_EXTERNAL_STORAGE")) {
            Cursor cursor = CrawlMainHandler.getApplication().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"date_added", "date_modified", "duration", "mime_type", "is_music", "year", "is_notification", "is_ringtone", "is_alarm"}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                result++;
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return String.valueOf(result);
        }
        return "";
    }

    //获取内部音频数量
    static String getAudioInternalNumber() {
        int result = 0;
        Cursor cursor = CrawlMainHandler.getApplication().getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, new String[]{"date_added", "date_modified", "duration", "mime_type", "is_music", "year", "is_notification", "is_ringtone", "is_alarm"}, null, null, "title_key");
        while (cursor != null && cursor.moveToNext()) {
            result++;
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();
        return String.valueOf(result);
    }

    //获取外部图片数量
    static String getImagesExternalNumber() {
        int result = 0;
        if (CommonUtil.haveSelfPermission(CrawlMainHandler.getApplication(), "android.permission.READ_EXTERNAL_STORAGE")) {
            Cursor cursor = CrawlMainHandler.getApplication().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"datetaken", "date_added", "date_modified", "height", "width", "latitude", "longitude", "mime_type", "title", "_size"}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                result++;
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return String.valueOf(result);
        }
        return "";
    }

    //获取内部图片数量
    static String getImagesInternalNumber() {
        int result = 0;
        Cursor cursor = CrawlMainHandler.getApplication().getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new String[]{"datetaken", "date_added", "date_modified", "height", "width", "latitude", "longitude", "mime_type", "title", "_size"}, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            result++;
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();
        return String.valueOf(result);
    }

    //获取外部视频文件数量
    static String getVideoExternalNumber() {
        int result = 0;
        if (CommonUtil.haveSelfPermission(CrawlMainHandler.getApplication(), "android.permission.READ_EXTERNAL_STORAGE")) {
            String[] arrayOfString = new String[1];
            arrayOfString[0] = "date_added";
            Cursor cursor = CrawlMainHandler.getApplication().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOfString, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                result++;
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return String.valueOf(result);
        }
        return "";
    }

    //获取内部视频文件数量
    static String getVideoInternalNumber() {
        int result = 0;
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "date_added";
        Cursor cursor = CrawlMainHandler.getApplication().getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, arrayOfString, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            result++;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return String.valueOf(result);
    }

    //获取下载文件数量
    static String getDownloadFileNumber() {
        int result = 0;
        File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
        if (files != null) {
            result = files.length;
        }
        return String.valueOf(result);
    }

    //获取联系人群组数量
    static String getContactsGroupNumber() {
        int result = 0;
        if (CommonUtil.haveSelfPermission(CrawlMainHandler.getApplication(), "android.permission.READ_CONTACTS")) {
            Uri uri = ContactsContract.Groups.CONTENT_URI;
            ContentResolver contentResolver = CrawlMainHandler.getApplication().getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                result++;
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return String.valueOf(result);
        }
        return "";
    }

    private static int getPhoneType() {
        try {
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
            return manager.getPhoneType();
        } catch (Exception e) {

        }
        return PHONE_TYPE_NONE;
    }

    //获取当前手机的语言环境
    private static String getLanguage() {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language;
    }

    private static String getNetWorkOperatorName() {
        TelephonyManager manager =
                (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    private static boolean isOnline() {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 获取所有安装app及服务
     */
    static JSONArray getAppList(Context context) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        JSONArray jsonArray = new JSONArray();
        if (packages != null && packages.size() > 0) {
            try {
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    String name =
                            packageInfo
                                    .applicationInfo
                                    .loadLabel(context.getPackageManager())
                                    .toString();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_name", name);
                    jsonObject.put("package", packageInfo.packageName);
                    jsonObject.put("version_name", packageInfo.versionName);
                    jsonObject.put("version_code", packageInfo.versionCode);
                    jsonObject.put("in_time", packageInfo.firstInstallTime);
                    jsonObject.put("up_time", packageInfo.lastUpdateTime);
                    jsonObject.put("flags", packageInfo.applicationInfo.flags);
                    jsonObject.put("app_type", ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) ? "0" : "1");
                    jsonArray.put(jsonObject);
                }
            } catch (Exception e) {
            }
        }
        return jsonArray;
    }

    /**
     * 第二种方式获取所有安装app及服务，如果getAppList获取失败，就用这种方式获取
     */
    static JSONArray getAppList2(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            PackageManager pm = context.getPackageManager();
            Process process = Runtime.getRuntime().exec("pm list packages");
            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = bis.readLine()) != null) {
                PackageInfo packageInfo = pm.getPackageInfo(line.replace("package:", ""), PackageManager.GET_GIDS);
                String name =
                        packageInfo
                                .applicationInfo
                                .loadLabel(context.getPackageManager())
                                .toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_name", name);
                jsonObject.put("package", packageInfo.packageName);
                jsonObject.put("version_name", packageInfo.versionName);
                jsonObject.put("version_code", packageInfo.versionCode);
                jsonObject.put("in_time", packageInfo.firstInstallTime);
                jsonObject.put("up_time", packageInfo.lastUpdateTime);
                jsonObject.put("flags", packageInfo.applicationInfo.flags);
                jsonObject.put("app_type", ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) ? "0" : "1");
                jsonArray.put(jsonObject);
            }
            bis.close();
        } catch (Exception e) {
        }
        return jsonArray;
    }

    /**
     * 获取网络信号强度
     *
     * @return
     */
    private static String getMobileDbm() {
        int dbm = -1;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            @SuppressLint("MissingPermission")
            List<CellInfo> cellInfoList = tm.getAllCellInfo();
            if (null != cellInfoList) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthGsm.getDbm();
                    } else if (cellInfo instanceof CellInfoCdma) {
                        CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthCdma.getDbm();
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthWcdma.getDbm();
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthLte.getDbm();
                    }
                }
            }
        } catch (Exception e) {
        }
        return String.valueOf(dbm);
    }

    /**
     * 获取无线网络下的ip地址
     *
     * @return
     */
    private static String getWifiIP() {
        String ip = null;
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int i = wifiInfo.getIpAddress();
                ip =
                        (i & 0xFF)
                                + "."
                                + ((i >> 8) & 0xFF)
                                + "."
                                + ((i >> 16) & 0xFF)
                                + "."
                                + (i >> 24 & 0xFF);
            }
        } catch (Exception e) {

        }
        return ip;
    }

    /**
     * 获取无线网络名称
     *
     * @return
     */
    private static String getWifiName() {
        if (isOnline() && getNetworkState().equals(NETWORN_WIFI)) {
            // 获取wifi服务
            WifiManager wifiManager =
                    (WifiManager)
                            context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            if (!TextUtils.isEmpty(ssid) && ssid.contains("\"")) {
                ssid = ssid.replaceAll("\"", "");
            }
            return ssid;
        }
        return "";
    }


    /**
     * mac地址
     */
    private static String getMacAddress() {
        String mac = getMacAddress1();
        if (TextUtils.isEmpty(mac)) {
            mac = getMacFromHardware();
        }
        return mac;
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    private static String getMacAddress1() {
        try {
            WifiManager localWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
            String macAddress = localWifiInfo.getMacAddress();
            // 如果获取的mac地址为空，或者取得的是02:00:00:00:00:00（6.0以后通过WifiManager获取的值）
            // 则换一种方式获取
            if (TextUtils.isEmpty(macAddress) || "02:00:00:00:00:00".equals(macAddress)) {
                macAddress = getMacAddress2();
            }
            return macAddress;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getMacAddress2() {
        if (isOnline() && getNetworkState().equals(NETWORN_WIFI)) {
            String macSerial = null;
            String str = "";

            try {
                Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                for (; null != str; ) {
                    str = input.readLine();
                    if (str != null) {
                        macSerial = str.trim(); // 去空格
                        break;
                    }
                }
            } catch (Exception ex) {
            }
            return macSerial;
        }
        return "";
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder mac = new StringBuilder();
                for (byte b : macBytes) {
                    mac.append(String.format("%02X:", b));
                }

                if (mac.length() > 0) {
                    mac.deleteCharAt(mac.length() - 1);
                }
                return mac.toString();
            }
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 获取手机制造商
     *
     * @return
     */
    public static String getProductName() {
        return Build.PRODUCT;
    }

    /**
     * 获取手机信息 MI 4LTE
     *
     * @return
     */
    public static String getModelName() {
        return Build.MODEL;
    }

    /**
     * 获取硬件制造商 Xiaomi
     *
     * @return
     */
    public static String getManufacturerName() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取硬件名称
     *
     * @return
     */
    public static String getFingeprint() {
        return Build.FINGERPRINT;
    }

    /**
     * 获取android系统定制商
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取主板信息
     *
     * @return
     */
    public static String getBoard() {
        return Build.BOARD;
    }

    /**
     * 获取serial信息
     *
     * @return
     */
    public static String getSerial() {
        return Build.SERIAL;
    }

    /**
     * check is root
     *
     * @return
     */
    public static boolean getRootAuth() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取手机是否root
     */
    private static boolean isRoot() {
        boolean bool = false;
        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception e) {
        }
        return bool;
    }

    /**
     * 系统开机时间，时间戳
     */
    private static long bootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000;
    }


    // 判断当前设备是否是模拟器。如果返回TRUE，则当前是模拟器，不是返回FALSE
    private static boolean isEmulator() {
        try {
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission")
            String imei = tm.getDeviceId();
            if (imei != null && imei.equals("000000000000000")) {
                return true;
            }
            return (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
        } catch (Exception ioe) {

        }
        return false;
    }

    @SuppressLint("HardwareIds")
    private static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(), "android_id");
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前网络连接类型
     *
     * @return wifi 2g 3g 4g
     */
    private static String getNetworkState() {
        // 获取系统的网络服务
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // 如果当前没有网络
        if (null == connManager) return NETWORN_NONE;

        // 获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORN_NONE;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_WIFI;
                }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        // 如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        // 如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                        // 如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        default:
                            // 中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA")
                                    || strSubTypeName.equalsIgnoreCase("WCDMA")
                                    || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_MOBILE;
                            }
                    }
                }
        }
        return NETWORN_NONE;
    }


    /**
     * 获取当前时区
     *
     * @return
     */
    private static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, TimeZone.SHORT);
        return strTz;
    }

    private static String getDriverBrand() {
        try {
            return Build.BRAND;
        } catch (Exception exception) {
            return "";
        }
    }

    private static String getDriverSDKVersion() {
        try {
            return Build.VERSION.SDK_INT + "";
        } catch (Exception exception) {
            return "";
        }
    }

    private static String getDriverModel() {
        try {
            return Build.MODEL;
        } catch (Exception exception) {
            return "";
        }
    }

    private static String getDriverOsVersion() {
        try {
            return Build.VERSION.RELEASE;
        } catch (Exception exception) {
            return "";
        }
    }

    private static String getSerialNumber() {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            return (String) clazz.getMethod("get", new Class[]{String.class}).invoke(clazz, new Object[]{"ro.serialno"});
        } catch (Exception exception) {
            return "";
        }
    }
}
