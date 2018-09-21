package com.aaron.downloadmanagerdemo

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 * 作者：Aaron
 * 时间：2018/9/21:8:44
 * 邮箱：
 * 说明：下载监听
 */
class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (MyApplication.downloadId == id) {
                installApk(context, id)
                MyApplication.downloadId = 0L // 重置
            }
        } else if (intent.action == DownloadManager.ACTION_NOTIFICATION_CLICKED) {
            // DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            //获取所有下载任务Ids组
            //long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            ////点击通知栏取消所有下载
            //manager.remove(ids);
            //Toast.makeText(context, "下载任务已取消", Toast.LENGTH_SHORT).show();
            //处理 如果还未完成下载，用户点击Notification ，跳转到下载中心
            val viewDownloadIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
            viewDownloadIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(viewDownloadIntent)
        }
    }

    private fun installApk(context: Context, downloadApkId: Long) {
        val dManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val install = Intent(Intent.ACTION_VIEW)
        val downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId)
        if (downloadFileUri != null) {
            Log.d("DownloadManager", downloadFileUri.toString())
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive")
            if ((Build.VERSION.SDK_INT >= 24)) {//判读版本是否在7.0以上
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
            }
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (install.resolveActivity(context.packageManager) != null) {
                context.startActivity(install)
            } else {
                Log.e("installApk","自动安装失败，请手动安装")
            }
        } else {
            Log.e("DownloadManager", "download error")
        }
    }
}
