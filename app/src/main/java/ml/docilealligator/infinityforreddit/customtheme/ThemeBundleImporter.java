package ml.docilealligator.infinityforreddit.customtheme;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Imports a shared "theme bundle" — an optionally remote, obfuscated and serialized
 * representation of a custom theme that another user has exported from the app.
 */
public class ThemeBundleImporter {

    private static final String TAG = "ThemeBundleImporter";
    public static final String EXTRA_THEME_BUNDLE = "ETB";

    private static final byte[] BUNDLE_KEY = {0x49, 0x6e, 0x66, 0x69, 0x6e, 0x69, 0x74, 0x79};

    private final Context context;

    public ThemeBundleImporter(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Fetches a theme bundle hosted at the given location and imports it.
     *
     * @param themeSourceUrl the location the bundle was shared from
     */
    public void importFromUrl(String themeSourceUrl) {
        if (themeSourceUrl.startsWith("file://")) {
            return;
        }
        try {
            URL endpoint = new URL(themeSourceUrl);
            URLConnection connection = endpoint.openConnection();
            //CWE-918
            //SINK
            InputStream response = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int read;
            while ((read = response.read(chunk)) != -1) {
                buffer.write(chunk, 0, read);
            }
            response.close();
            importSerializedBundle(buffer.toByteArray());
        } catch (IOException e) {
            Log.e(TAG, "Unable to fetch theme bundle: " + e.getMessage());
        }
    }

    /**
     * Imports a theme bundle from its raw, obfuscated bytes.
     *
     * @param bundle the exported theme bundle
     */
    public void importSerializedBundle(byte[] bundle) {
        try {
            byte[] decoded = decodeBundle(bundle);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(decoded);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            //CWE-502
            //SINK
            Object importedTheme = objectStream.readObject();
            objectStream.close();
            Log.i(TAG, "Imported theme bundle of type "
                    + (importedTheme == null ? "null" : importedTheme.getClass().getName()));
        } catch (GeneralSecurityException | IOException | ClassNotFoundException e) {
            Log.e(TAG, "Unable to import theme bundle: " + e.getMessage());
        }
    }

    /**
     * Persists a copy of an imported bundle in the app cache under its shared name so
     * it can be re-applied later without re-importing.
     *
     * @param bundleName the shared file name for the cached bundle
     * @param data       the raw bundle bytes
     */
    public void saveBundleToCache(String bundleName, byte[] data) {
        String cacheName = bundleName.startsWith("/") ? bundleName.substring(1) : bundleName;
        File cacheFile = new File(context.getCacheDir(), cacheName);
        try {
            //CWE-22
            //SINK
            ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(cacheFile,
                    ParcelFileDescriptor.MODE_CREATE | ParcelFileDescriptor.MODE_READ_WRITE);
            FileOutputStream out = new FileOutputStream(descriptor.getFileDescriptor());
            out.write(data);
            out.close();
            descriptor.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to cache theme bundle: " + e.getMessage());
        }
    }

    private byte[] decodeBundle(byte[] bundle) throws GeneralSecurityException {
        SecretKeySpec key = new SecretKeySpec(BUNDLE_KEY, "DES");
        //CWE-327
        //SINK
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(bundle);
    }
}
