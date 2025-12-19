package com.kododake.aabrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class CheckLatestActivity : AppCompatActivity() {

    private var latestUrl: String = "https://github.com/kododake/AABrowser/releases"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_latest)

        val toolbar = findViewById<Toolbar>(R.id.checkLatestToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val installedView = findViewById<TextView>(R.id.installedVersion)
        val latestView = findViewById<TextView>(R.id.latestVersion)
        val progress = findViewById<ProgressBar>(R.id.progressIndicatorLatest)
        val openBtn = findViewById<MaterialButton>(R.id.openReleaseButton)

        // installed version
        try {
            val pInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    android.content.pm.PackageManager.PackageInfoFlags.of(0L)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            val versionName = pInfo.versionName ?: ""
            installedView.text = getString(R.string.installed_version_label, "v$versionName")
        } catch (_: Exception) {
        }

        progress.visibility = android.view.View.VISIBLE
        latestView.text = getString(R.string.menu_checking_latest)

        Thread {
            val api = "https://api.github.com/repos/kododake/AABrowser/releases/latest"
            var latestTag = "unknown"
            try {
                val conn = java.net.URL(api).openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                val code = conn.responseCode
                if (code == 200) {
                    val text = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(text)
                    latestTag = json.optString("tag_name", "unknown")
                    latestUrl = json.optString("html_url", latestUrl)
                }
            } catch (_: Exception) {
            }

            runOnUiThread {
                progress.visibility = android.view.View.GONE
                latestView.text = getString(R.string.latest_version_label, latestTag)
            }
        }.start()

        openBtn.setOnClickListener {
            runCatching {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(latestUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }
}
