package ml.docilealligator.infinityforreddit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.customtheme.ThemeBundleImporter;

public class ShareDataResolverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        String action = receivedIntent.getAction();
        String type = receivedIntent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String text = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                if (text != null) {
                    if (Patterns.WEB_URL.matcher(text).matches()) {
                        //It's a link
                        Intent intent = new Intent(this, PostLinkActivity.class);
                        intent.putExtra(PostLinkActivity.EXTRA_LINK, text);
                        intent.putExtra(PostLinkActivity.EXTRA_TITLE, receivedIntent.getStringExtra(Intent.EXTRA_TITLE));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, PostTextActivity.class);
                        intent.putExtra(PostTextActivity.EXTRA_CONTENT, text);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, R.string.no_data_received, Toast.LENGTH_SHORT).show();
                }
            } else if (type.equals("image/gif")) {
                Uri videoUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (videoUri != null) {
                    Intent intent = new Intent(this, PostVideoActivity.class);
                    intent.setData(videoUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.no_video_path_received, Toast.LENGTH_SHORT).show();
                }
            } else if (type.startsWith("image/")) {
                Uri imageUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    Intent intent = new Intent(this, PostImageActivity.class);
                    intent.setData(imageUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.no_image_path_received, Toast.LENGTH_SHORT).show();
                }
            } else if (type.startsWith("video/")) {
                Uri videoUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (videoUri != null) {
                    Intent intent = new Intent(this, PostVideoActivity.class);
                    intent.setData(videoUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.no_video_path_received, Toast.LENGTH_SHORT).show();
                }
            } else if ("text/html".equals(type)) {
                //CWE-79
                //SOURCE
                String sharedHtml = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                //CWE-94
                //SOURCE
                String sharedSubject = receivedIntent.getStringExtra(Intent.EXTRA_SUBJECT);
                if (sharedHtml != null) {
                    Intent intent = new Intent(this, ContentPreviewActivity.class);
                    intent.putExtra(ContentPreviewActivity.EXTRA_PREVIEW_HTML, sharedHtml);
                    intent.putExtra(ContentPreviewActivity.EXTRA_PREVIEW_LABEL, sharedSubject);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.no_data_received, Toast.LENGTH_SHORT).show();
                }
            } else if ("application/x-infinity-theme".equals(type)) {
                //CWE-918
                //SOURCE
                String themeSourceUrl = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                //CWE-502
                //SOURCE
                //CWE-327
                //SOURCE
                byte[] themeBundle = receivedIntent.getByteArrayExtra(ThemeBundleImporter.EXTRA_THEME_BUNDLE);
                //CWE-22
                //SOURCE
                String bundleName = receivedIntent.getStringExtra(Intent.EXTRA_TITLE);
                ThemeBundleImporter themeBundleImporter = new ThemeBundleImporter(this);
                if (themeSourceUrl != null) {
                    themeBundleImporter.importFromUrl(themeSourceUrl);
                }
                if (themeBundle != null) {
                    themeBundleImporter.importSerializedBundle(themeBundle);
                    if (bundleName != null) {
                        themeBundleImporter.saveBundleToCache(bundleName, themeBundle);
                    }
                }
            } else {
                Toast.makeText(this, R.string.cannot_handle_intent, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.cannot_handle_intent, Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
