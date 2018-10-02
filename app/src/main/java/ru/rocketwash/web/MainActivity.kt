package ru.rocketwash.web

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG: String = MainActivity::class.java.simpleName

    private val PERMISSION_CAMERA = Manifest.permission.CAMERA
    private val REQUEST_PERMISSION_CAMERA = 132
    private var permissionCameraRequest: PermissionRequest? = null
    private var permissionsArray: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWebView()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    private fun isPermissionCameraGranted() = ActivityCompat.checkSelfPermission(this, PERMISSION_CAMERA) != PackageManager.PERMISSION_DENIED


    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webViewClient = object : WebViewClient() {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                webView.loadUrl(request.url.toString())
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showProgressBar()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideProgressBar()
            }
        }
        webView.webChromeClient = object : WebChromeClient() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onPermissionRequest(request: PermissionRequest) {
                for (permission in request.resources) {
                    Log.d(TAG, "on permission request: $permission")

                    if (permission == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                        permissionCameraRequest = request
                        permissionsArray = arrayOf(permission)
                        if (isPermissionCameraGranted()) {
                            request.grant(permissionsArray)
                            break
                        } else {
                            ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(PERMISSION_CAMERA),
                                    REQUEST_PERMISSION_CAMERA)
                        }
                        break
                    }
                }
            }

        }
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        loadWebview()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> {
                if (permissions.isNotEmpty() && grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCameraRequest!!.grant(permissionsArray)
                }
            }
        }
    }

    private fun loadWebview() {
        webView.loadUrl(getString(R.string.url))
    }

    override fun onPause() {
        super.onPause()
        webView.onResume()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
