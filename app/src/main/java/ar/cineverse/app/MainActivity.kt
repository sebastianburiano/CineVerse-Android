package ar.cineverse.app

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import android.webkit.*

/** CineVerse Android client for the user's own CineVerse server. */
class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var root: FrameLayout
    private var fullscreen: View? = null
    private var fullscreenCallback: WebChromeClient.CustomViewCallback? = null
    private var errorDialog: AlertDialog? = null
    private val prefs by lazy { getSharedPreferences("cineverse", MODE_PRIVATE) }
    private val defaultServer = "http://192.168.0.113:8000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK
        root = FrameLayout(this)
        web = WebView(this)
        root.addView(web, FrameLayout.LayoutParams(-1, -1))
        setContentView(root)
        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        web.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) showConnectionError()
            }
            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                if (request.isForMainFrame && errorResponse.statusCode >= 500) showConnectionError()
            }
        }
        web.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                if (fullscreen != null) { callback.onCustomViewHidden(); return }
                fullscreen = view
                fullscreenCallback = callback
                web.visibility = View.GONE
                root.addView(view, FrameLayout.LayoutParams(-1, -1))
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
            override fun onHideCustomView() { leaveFullscreen() }
        }
        if (savedInstanceState == null) loadServer() else web.restoreState(savedInstanceState)
    }

    private fun server(): String = prefs.getString("server", defaultServer) ?: defaultServer
    private fun loadServer() { errorDialog?.dismiss(); errorDialog = null; web.loadUrl(server()) }
    private fun showConnectionError() {
        if (isFinishing || errorDialog?.isShowing == true) return
        errorDialog = AlertDialog.Builder(this).setTitle("Sin conexión a CineVerse")
            .setMessage("Comprobá que el servidor esté iniciado, que la PC y el teléfono estén en la misma red y que el puerto 8000 esté permitido en Windows.\n\nServidor: ${server()}")
            .setPositiveButton("Reintentar") { _, _ -> loadServer() }
            .setNeutralButton("Cambiar servidor") { _, _ -> changeServer() }
            .setNegativeButton("Cerrar", null).create()
        errorDialog?.show()
    }
    private fun changeServer() {
        val input = EditText(this).apply { setSingleLine(true); setText(server()); selectAll() }
        AlertDialog.Builder(this).setTitle("Dirección del servidor")
            .setMessage("Ejemplo: http://192.168.1.50:8000")
            .setView(input).setPositiveButton("Guardar") { _, _ ->
                val value = input.text.toString().trim().trimEnd('/')
                val uri = android.net.Uri.parse(value)
                val valid = (uri.scheme == "http" || uri.scheme == "https") && !uri.host.isNullOrBlank() && uri.userInfo == null && uri.port in -1..65535
                if (valid) { prefs.edit().putString("server", value).apply(); loadServer() }
                else Toast.makeText(this, "Ingresá una URL HTTP o HTTPS válida", Toast.LENGTH_LONG).show()
            }.setNegativeButton("Cancelar", null).show()
    }
    private fun leaveFullscreen() {
        fullscreen?.let { root.removeView(it) }
        fullscreen = null
        fullscreenCallback?.onCustomViewHidden()
        fullscreenCallback = null
        web.visibility = View.VISIBLE
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        window.decorView.systemUiVisibility = 0
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when {
            fullscreen != null -> leaveFullscreen()
            web.canGoBack() -> web.goBack()
            else -> AlertDialog.Builder(this).setTitle("CineVerse")
                .setItems(arrayOf("Recargar", "Cambiar servidor", "Salir")) { _, i ->
                    when (i) { 0 -> loadServer(); 1 -> changeServer(); 2 -> finish() }
                }.show()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) { web.saveState(outState); super.onSaveInstanceState(outState) }
    override fun onDestroy() { errorDialog?.dismiss(); if (fullscreen != null) leaveFullscreen(); web.destroy(); super.onDestroy() }
}
