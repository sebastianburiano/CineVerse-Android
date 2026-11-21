
package ar.cineverse.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView web;
    private FrameLayout root;
    private ProgressBar loading;
    private SharedPreferences prefs;
    private View fullscreen;
    private WebChromeClient.CustomViewCallback callback;

    private static final String DEFAULT_URL =
        "https://waters-attempting-bali-cedar.trycloudflare.com";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        prefs = getSharedPreferences(
            "cineverse", Context.MODE_PRIVATE
        );

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(9, 11, 18));

        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(9, 11, 18));

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(
            WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        );

        loading = new ProgressBar(this);
        FrameLayout.LayoutParams progressParams =
            new FrameLayout.LayoutParams(100, 100);
        progressParams.gravity = android.view.Gravity.CENTER;

        root.addView(web);
        root.addView(loading, progressParams);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(
                WebView view,
                WebResourceRequest request,
                WebResourceError error
            ) {
                if (request.isForMainFrame()) {
                    loading.setVisibility(View.GONE);
                    showConnectionError();
                }
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(
                View view,
                CustomViewCallback cb
            ) {
                fullscreen = view;
                callback = cb;
                setContentView(view);

                getWindow().getDecorView()
                    .setSystemUiVisibility(
                        5894 | 1024 | 512
                    );
            }

            @Override
            public void onHideCustomView() {
                fullscreen = null;
                setContentView(root);

                getWindow().getDecorView()
                    .setSystemUiVisibility(0);

                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
            }
        });

        setContentView(root);

        String url = prefs.getString("server", DEFAULT_URL);
        loadServer(url);
    }

    private void loadServer(String url) {
        loading.setVisibility(View.VISIBLE);
        web.loadUrl(url);
    }

    private void showConnectionError() {
        new AlertDialog.Builder(this)
            .setTitle("CineVerse - Sin conexión")
            .setMessage(
                "No se pudo conectar al servidor. " +
                "Comprobá que CineVerse y Cloudflare " +
                "estén funcionando."
            )
            .setPositiveButton("Reintentar", (d, w) -> {
                web.reload();
            })
            .setNegativeButton("Cambiar servidor", (d, w) -> {
                configure();
            })
            .show();
    }

    private void configure() {
        EditText input = new EditText(this);
        input.setSingleLine(true);

        input.setText(
            prefs.getString("server", DEFAULT_URL)
        );

        new AlertDialog.Builder(this)
            .setTitle("Servidor CineVerse")
            .setMessage("Ingresá la dirección HTTPS del servidor.")
            .setView(input)
            .setPositiveButton("Conectar", (d, w) -> {
                String url = input.getText()
                    .toString().trim();

                if (!url.startsWith("http://") &&
                    !url.startsWith("https://")) {
                    url = "https://" + url;
                }

                prefs.edit()
                    .putString("server", url)
                    .apply();

                loadServer(url);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    public void onBackPressed() {
        if (fullscreen != null) {
            web.getWebChromeClient().onHideCustomView();
        } else if (web.canGoBack()) {
            web.goBack();
        } else {
            configure();
        }
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.destroy();
        }
        super.onDestroy();
    }
}

            .setMessage("¿Cambiar servidor?").setPositiveButton("Cambiar", (d,w)->configure())
            .setNegativeButton("Cancelar", null).show();
    }
}
