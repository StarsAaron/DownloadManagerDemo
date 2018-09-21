package com.aaron.downloadmanagerdemo

import android.app.Application

/**
 * 作者：Aaron
 * 时间：2018/9/21:20:53
 * 邮箱：
 * 说明：
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        var downloadId = 0L // 下载任务的ID号
    }
}