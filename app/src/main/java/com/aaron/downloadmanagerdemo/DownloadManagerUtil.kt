package com.aaron.downloadmanagerdemo

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast

/**
 * 作者：Aaron
 * 时间：2018/9/21:8:41
 * 邮箱：
 * 说明：下载管理器
 */
class DownloadManagerUtil(private val mContext: Context) {

    /**
     * 可能会出错Cannot update URI: content://downloads/my_downloads/-1
     * 检查下载管理器是否被禁用
     */
    fun checkDownloadManagerEnable():Boolean {
        try {
            val state = mContext.packageManager.getApplicationEnabledSetting("com.android.providers.downloads")
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                val packageName = "com.android.providers.downloads"
                try {
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    mContext.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    mContext.startActivity(intent)
                }
                return false
            }
        } catch (e:Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun download(url: String, title: String, desc: String): Long {
        val uri = Uri.parse(url)
        val req = DownloadManager.Request(uri)
        //设置允许使用的网络类型，这里是移动网络和wifi都可以
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        //下载中和下载完后都显示通知栏
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        //设置文件的保存的位置[三种方式]
        // 第一种 file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
        req.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "$title.apk")
        //第二种 file:///storage/emulated/0/Download/update.apk
//        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk")
        //第三种 自定义文件路径
//        req.setDestinationUri()

        //禁止发出通知，既后台下载
//        req.setShowRunningNotification(false);
        //通知栏标题
        req.setTitle(title)
        //通知栏描述信息
        req.setDescription(desc)
        //设置类型为.apk
        req.setMimeType("application/vnd.android.package-archive")
        // 设置为可被媒体扫描器找到
        req.allowScanningByMediaScanner()
        // 设置为可见和可管理
        req.setVisibleInDownloadsUi(true)
        //获取下载任务ID
        val dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        return try {
            dm.enqueue(req)
        } catch (e: Exception) {
            Toast.makeText(mContext, "找不到下载文件", Toast.LENGTH_SHORT).show()
            -1
        }
    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    fun clearCurrentTask(downloadId: Long) {
        val dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        try {
            dm.remove(downloadId)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }
}