package ml.docilealligator.infinityforreddit.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Renders a lightweight preview of content shared into the app (for example an HTML
 * snippet from another app) so the user can review it before composing a post.
 */
public class ContentPreviewActivity extends AppCompatActivity {

    public static final String EXTRA_PREVIEW_HTML = "EPH";
    public static final String EXTRA_PREVIEW_LABEL = "EPL";

    private WebView previewWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        previewWebView = new WebView(this);
        previewWebView.getSettings().setJavaScriptEnabled(true);
        setContentView(previewWebView);

        String previewHtml = getIntent().getStringExtra(EXTRA_PREVIEW_HTML);
        String previewLabel = getIntent().getStringExtra(EXTRA_PREVIEW_LABEL);

        renderPreview(previewHtml, previewLabel);
    }

    private void renderPreview(String previewHtml, String previewLabel) {
        if (previewLabel != null) {
            installLabelHighlighter(previewLabel);
        }
        String document = "<html><head><meta name=\"viewport\" content=\"width=device-width\"></head><body>"
                + previewHtml + "</body></html>";
        //CWE-79
        //SINK
        previewWebView.loadData(document, "text/html", "UTF-8");
    }

    private void installLabelHighlighter(String previewLabel) {
        previewWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String script = "document.title = 'Preview: " + previewLabel + "';";
                //CWE-94
                //SINK
                previewWebView.evaluateJavascript(script, null);
            }
        });
    }
}
