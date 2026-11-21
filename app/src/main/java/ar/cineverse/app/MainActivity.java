
package ar.cineverse.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.Gravity;
import android.widget.Toast;
import android.webkit.JavascriptInterface;
import androidx.mediarouter.app.MediaRouteButton;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.cast.MediaLoadRequestData;
import org.json.JSONObject;
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
    private MediaRouteButton castButton;
    private FrameLayout fullscreenContainer;
    private CastContext castContext;
    private String currentVideoUrl;
    private String currentVideoTitle = "CineVerse";
    private final SessionManagerListener<CastSession> castListener = new SessionManagerListener<CastSession>() {
        @Override public void onSessionStarted(CastSession session, String id) { sendToCast(session); }
        @Override public void onSessionResumed(CastSession session, boolean wasSuspended) { sendToCast(session); }
        @Override public void onSessionStarting(CastSession session) {}
        @Override public void onSessionStartFailed(CastSession session, int error) {}
        @Override public void onSessionEnding(CastSession session) {}
        @Override public void onSessionEnded(CastSession session, int error) {}
        @Override public void onSessionResuming(CastSession session, String id) {}
        @Override public void onSessionResumeFailed(CastSession session, int error) {}
        @Override public void onSessionSuspended(CastSession session, int reason) {}
    };
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
try {
    castContext = CastContext.getSharedInstance(this);
} catch (Exception e) {
    android.util.Log.e("CineVerse", "Error iniciando Google Cast", e);
    castContext = null;
}
        web.addJavascriptInterface(new Object() {
            @JavascriptInterface public void videoFound(String url, String title) {
                runOnUiThread(() -> {
                    currentVideoUrl = url;
                    currentVideoTitle = title;
                });
            }
        }, "CineVerseBridge");
        //castButton = new MediaRouteButton(this);
        //CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), castButton);
       // Google Cast desactivado temporalmente para diagnosticar el cierre.
        });
        loading = new ProgressBar(this);
        FrameLayout.LayoutParams progressParams =
            new FrameLayout.LayoutParams(100, 100);
        progressParams.gravity = android.view.Gravity.CENTER;

        root.addView(web);
        root.addView(loading, progressParams);
        addCastButton(root);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loading.setVisibility(View.GONE);
                scanVideo();
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
                fullscreenContainer = new FrameLayout(MainActivity.this);
                fullscreenContainer.setBackgroundColor(Color.BLACK);
                fullscreenContainer.addView(view);
                //addCastButton(fullscreenContainer);
                setContentView(fullscreenContainer);

                getWindow().getDecorView()
                    .setSystemUiVisibility(
                        5894 | 1024 | 512
                    );
            }

            @Override
            public void onHideCustomView() {
                fullscreen = null;
                if (fullscreenContainer != null) {
                    //fullscreenContainer.removeView(castButton);
                    fullscreenContainer = null;
                }
                //addCastButton(root);
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
        currentVideoUrl = null;
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

    private void addCastButton(FrameLayout parent) {
        if (castButton.getParent() instanceof android.view.ViewGroup) {
            ((android.view.ViewGroup)castButton.getParent()).removeView(castButton);
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(64, 64, Gravity.TOP | Gravity.RIGHT);
        lp.setMargins(0, 20, 20, 0);
        parent.addView(castButton, lp);
        castButton.bringToFront();
    }

    private void scanVideo() {
        web.evaluateJavascript("(function(){var v=document.querySelector('video'); if(!v)return ''; return JSON.stringify({url:v.currentSrc||v.src||((v.querySelector('source')||{}).src)||'',title:document.title||'CineVerse'});})()", value -> {
            try {
                if (value == null || value.equals("null") || value.equals("\"\"")) return;
                String decoded = new org.json.JSONTokener(value).nextValue().toString();
                JSONObject obj = new JSONObject(decoded);
                String url = obj.optString("url", "");
                if (!url.isEmpty()) {
                    currentVideoUrl = new java.net.URI(web.getUrl()).resolve(url).toString();
                    currentVideoTitle = obj.optString("title", "CineVerse");
                }
            } catch (Exception ignored) {}
        });
    }

    private void sendToCast(CastSession session) {
        scanVideo();
        // evaluateJavascript is asynchronous; wait for its callback before sending.
        web.evaluateJavascript("(function(){var v=document.querySelector('video');return v?(v.currentSrc||v.src||''):''})()", result -> {
            try {
                String url = new org.json.JSONTokener(result).nextValue().toString();
                if (url.isEmpty()) url = currentVideoUrl;
                if (url == null || url.isEmpty()) {
                    Toast.makeText(this, "Abrí una película antes de transmitir", Toast.LENGTH_LONG).show();
                    return;
                }
                url = new java.net.URI(web.getUrl()).resolve(url).toString();
                if (!url.startsWith("https://")) {
                    Toast.makeText(this, "La TV necesita una URL HTTPS accesible", Toast.LENGTH_LONG).show();
                    return;
                }
                MediaMetadata metadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                metadata.putString(MediaMetadata.KEY_TITLE, currentVideoTitle);
                MediaInfo info = new MediaInfo.Builder(url)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("video/mp4")
                    .setMetadata(metadata).build();
                RemoteMediaClient client = session.getRemoteMediaClient();
                if (client != null) client.load(new MediaLoadRequestData.Builder().setMediaInfo(info).setAutoplay(true).build());
            } catch (Exception ex) {
                Toast.makeText(this, "No se pudo transmitir: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();
        if (castContext != null) castContext.getSessionManager().addSessionManagerListener(castListener, CastSession.class);
    }
    @Override protected void onPause() {
        if (castContext != null) castContext.getSessionManager().removeSessionManagerListener(castListener, CastSession.class);
        super.onPause();
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

         
