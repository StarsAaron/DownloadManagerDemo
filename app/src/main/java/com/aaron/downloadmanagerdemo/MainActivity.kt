package com.aaron.downloadmanagerdemo

import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var downloadBroadcastReceiver: DownloadReceiver? = null
    private val url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk"
    private val title = "下载"
    private val desc = "下载"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadBroadcastReceiver = DownloadReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.DOWNLOAD_COMPLETE")
        intentFilter.addAction("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED")
        registerReceiver(downloadBroadcastReceiver, intentFilter)

        btn_download.setOnClickListener {
            val dm = DownloadManagerUtil(this)
            if (dm.checkDownloadManagerEnable()) {
                if (MyApplication.downloadId != 0L) {
                    dm.clearCurrentTask(MyApplication.downloadId) // 先清空之前的下载
                }
                MyApplication.downloadId = dm.download(url, title, desc)
            }else{
                Toast.makeText(this@MainActivity,"请开启下载管理器",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadBroadcastReceiver)
    }
}
