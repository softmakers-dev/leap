package com.softmakers.leap

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.softmakers.leap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create a new WebView and set its properties
        webView = WebView(this)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        // Set the custom WebViewClient as the WebView's client
        val webViewClient = LeapWebViewClient(this)
        webView.webViewClient = webViewClient

        // Set the WebView as the activity's content view
        setContentView(webView)

        // Load a URL into the WebView
        webView.loadUrl("https://www.google.com/")

        // Handle the back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    } // end of OnCreate

    // Custom WebViewClient that takes a context as a constructor parameter
    private class LeapWebViewClient(private val context: Context) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            // Handle the URL loading logic here
            // Check if the URL is a PDF file
            if (url?.contains(".pdf") == true) {

                // Check if a PDF reader app is installed
                val pdfReaderIntent = Intent(Intent.ACTION_VIEW)
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
                pdfReaderIntent.type = mimeType

                val isPdfReaderInstalled = context.packageManager.resolveActivity(pdfReaderIntent, PackageManager.MATCH_DEFAULT_ONLY) != null

                // If a PDF reader is installed, launch the intent to view the PDF file
                if (isPdfReaderInstalled) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } else {
                    // If no PDF reader app is installed, show a message to the user
                    // or redirect to a webview to view the PDF file
                    // If no PDF reader app is installed, show a message to the user and provide a button to open the Play Store
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("PDF Reader not found")
                        .setMessage("To view PDF files, you need to install a PDF reader app.")
                        .setPositiveButton("Install") { _, _ ->
                            // Open the Play Store with the search results for "PDF reader"
                            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pdf+reader"))
                            context.startActivity(marketIntent)
                        }
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show()
                }

                // Return true to indicate that we have handled the URL loading
                return true
            }

            // If the URL is not a PDF file, load the URL in the WebView
            view?.loadUrl(url)
            return true
        }
    }


}