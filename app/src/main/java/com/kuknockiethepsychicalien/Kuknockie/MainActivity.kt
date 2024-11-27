package com.kuknockiethepsychicalien.Kuknockie

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kuknockiethepsychicalien.Kuknockie.Utils.LOGIN_URL

class MainActivity : AppCompatActivity() {
    private lateinit var connectivityObserver: ConnectivityObserver
    private var lastConnectivityStatus: Boolean? = null
    private lateinit var reloadButton: FloatingActionButton
    private lateinit var webView: WebView
    private lateinit var noInternetText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var mainView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reloadButton = findViewById(R.id.reloadButton)
        webView = findViewById(R.id.webView)
        noInternetText = findViewById(R.id.noInternetText)
        progressBar = findViewById(R.id.progressBar)
        mainView = findViewById(R.id.mainView)


        connectivityObserver = ConnectivityObserver(applicationContext)
        connectivityObserver.startObserving()

        // Initial Connectivity Check
        connectivityObserver.isConnected.observe(this) { isConnected ->
            if (lastConnectivityStatus == null || lastConnectivityStatus != isConnected) {
                lastConnectivityStatus = isConnected
                handleConnectivityChange(isConnected)
            }
        }

        reloadButton.setOnClickListener {
            reloadCurrentPage()
        }
    }

    private fun reloadCurrentPage() {
        if (lastConnectivityStatus == true) {
            webView.visibility = View.VISIBLE
            noInternetText.visibility = View.GONE
            reloadButton.visibility = View.GONE
            setupWebView()
            webView.loadUrl(LOGIN_URL)
        } else {
            showSnackBar()
            noInternetText.visibility = View.VISIBLE
            reloadButton.visibility = View.VISIBLE
            webView.visibility = View.GONE // Hide the WebView
        }
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun handleConnectivityChange(isConnected: Boolean) {
        if (isConnected) {
            webView.visibility = View.VISIBLE
            noInternetText.visibility = View.GONE
            reloadButton.visibility = View.GONE
            setupWebView()
            webView.loadUrl(LOGIN_URL)
        } else {
            showSnackBar()
            noInternetText.visibility = View.VISIBLE
            reloadButton.visibility = View.VISIBLE
            webView.visibility = View.GONE // Hide the WebView
        }
    }

    private fun showSnackBar() {
        val snackbar = Snackbar.make(mainView, "No internet connection!", Snackbar.LENGTH_LONG)
        snackbar.setAction("Settings") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
            } else {
                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
            }
        }
        snackbar.show()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityObserver.stopObserving()
    }
}