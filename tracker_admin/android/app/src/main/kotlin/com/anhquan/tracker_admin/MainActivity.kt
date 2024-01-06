package com.anhquan.tracker_admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private lateinit var spf: SharedPreferences

    @SuppressLint("QueryPermissionsNeeded")
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        spf = getSharedPreferences("com.anhquan.tracker_admin", Context.MODE_PRIVATE)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, "com.anhquan.tracker_admin/spf"
        ).setMethodCallHandler { call, result ->
            if (call.method == "get_string") {
                result.success(
                    spf.getString(
                        (call.arguments as Map<*, *>)["key"].toString(),
                        null,
                    )
                )
            }
            if (call.method == "put_string") {
                spf.edit().putString(
                    (call.arguments as Map<*, *>)["key"].toString(),
                    (call.arguments as Map<*, *>)["value"]?.toString(),
                ).apply()
                result.success(null)
            }
            if (call.method == "get_bool") {
                val key = (call.arguments as Map<*, *>)["key"].toString()
                result.success(
                    if (spf.contains(key)) spf.getBoolean(
                        key,
                        true,
                    )
                    else null
                )
            }
            if (call.method == "put_bool") {
                val key = (call.arguments as Map<*, *>)["key"].toString()
                val value = (call.arguments as Map<*, *>)["value"] as Boolean?
                if (value == null) spf.edit().remove(key).apply()
                else spf.edit().putBoolean(key, value).apply()
                result.success(null)
            }
            if (call.method == "get_int") {
                val key = (call.arguments as Map<*, *>)["key"].toString()
                result.success(
                    if (spf.contains(key)) spf.getInt(
                        key,
                        -1,
                    )
                    else null
                )
            }
            if (call.method == "put_int") {
                val key = (call.arguments as Map<*, *>)["key"].toString()
                val value = (call.arguments as Map<*, *>)["value"] as Int?
                if (value == null) spf.edit().remove(key).apply()
                else spf.edit().putInt(key, value).apply()
                result.success(null)
            }
        }
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, "com.anhquan.tracker_admin/service"
        ).setMethodCallHandler { call, result ->
            if (call.method == "start_service") {
                startService(Intent(this, MainService::class.java))
                result.success(true)
            }
            if (call.method == "stop_service") {
                stopService(Intent(this, MainService::class.java))
                result.success(true)
            }
            if (call.method == "restart_service") {
                stopService(Intent(this, MainService::class.java))
                startService(Intent(this, MainService::class.java))
                result.success(true)
            }
        }
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, "com.anhquan.tracker_admin/intent"
        ).setMethodCallHandler { call, result ->
            if (call.method == "open_map") {
                val lat = (call.arguments as Map<*, *>)["lat"] as Double
                val lon = (call.arguments as Map<*, *>)["lon"] as Double
                val title = (call.arguments as Map<*, *>)["title"].toString()
                val intent = Intent(
                    Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lon?q=$lat,$lon($title)")
                )
                intent.resolveActivity(packageManager)
                startActivity(intent)
                result.success(true)
            }
        }
    }
}
