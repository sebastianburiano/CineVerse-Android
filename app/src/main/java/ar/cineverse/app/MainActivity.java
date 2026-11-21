package ar.cineverse.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.widget.EditText;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.view.View;

public class MainActivity extends Activity {
    private WebView web;
    private SharedPreferences prefs;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("cineverse", Context.MODE_PRIVATE);
        web = new WebView(this);
        web.setBackgroundColor(0xff090b12);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(false);
        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient() {
            private View fullscreen;
            private android.webkit.WebChromeClient.CustomViewCallback callback;
            @Override public void onShowCustomView(View view, android.webkit.WebChromeClient.CustomViewCallback cb) {
                fullscreen = view; callback = cb; setContentView(view);
                getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
            }
            @Override public void onHideCustomView() {
                fullscreen = null; setContentView(web);
                getWindow().getDecorView().setSystemUiVisibility(0);
                if(callback != null) callback.onCustomViewHidden();
            }
        });
        setContentView(web);
        String url = prefs.getString("server", "");
        if(url.isEmpty()) configure(); else web.loadUrl(url);
    }
    private void configure() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(prefs.getString("server", "http://192.168.1.50:8001"));
        new AlertDialog.Builder(this).setTitle("Servidor CineVerse")
            .setMessage("Ingresá la dirección accesible desde tu teléfono (puerto 8001). Para datos móviles, usá una URL HTTPS segura.")
            .setView(input).setCancelable(false)
            .setPositiveButton("Conectar", (dialog, which) -> {
                String url = input.getText().toString().trim();
                if(!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
                prefs.edit().putString("server", url).apply(); web.loadUrl(url);
            }).show();
    }
    @Override public void onBackPressed() {
        if(web.canGoBack()) web.goBack(); else new AlertDialog.Builder(this)
            .setMessage("¿Cambiar servidor?").setPositiveButton("Cambiar", (d,w)->configure())
            .setNegativeButton("Cancelar", null).show();
    }
}
