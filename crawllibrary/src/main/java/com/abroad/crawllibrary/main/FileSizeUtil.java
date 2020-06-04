package com.abroad.crawllibrary.main;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

class FileSizeUtil {

    private static final int ERROR = -1;

    /**
     * SDCARD是否存
     */
    private static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
    }

    static JSONObject getStorageInfo() throws JSONException {
        JSONObject storageInfo = new JSONObject();
        storageInfo.put("ram_total_size", CommonUtil.getNonNullText(getRamTotalSize(CrawlMainHandler.getApplication())));
        storageInfo.put("ram_usable_size", CommonUtil.getNonNullText(getRamAvailSize(CrawlMainHandler.getApplication())));
        storageInfo.put("internal_storage_usable", getAvailableInternalMemorySize() + "");
        storageInfo.put("internal_storage_total", getTotalInternalMemorySize() + "");
        storageInfo.put("memory_card_size", getTotalExternalMemorySize() + "");
        storageInfo.put("memory_card_size_use", (getTotalExternalMemorySize() - getAvailableExternalMemorySize()) + "");
        return storageInfo;
    }

    private static String getRamTotalSize(Context paramContext) {
        ActivityManager activityManager = (ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(memoryInfo.totalMem);
        stringBuilder.append("");
        return stringBuilder.toString();
    }

    private static String getRamAvailSize(Context paramContext) {
        ActivityManager activityManager = (ActivityManager) paramContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(memoryInfo.availMem);
        stringBuilder.append("");
        return stringBuilder.toString();
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    private static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    private static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    private static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    private static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }
}
