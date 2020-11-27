package com.assukar.android.attest.statics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class AndroidAttestStatics {


    private static Context context;

    public static String attest(Context ctx) {

        context = ctx;

        JSONObject result = new JSONObject();
        try {
            result.put("debug", getDebug());
            result.put("root", getRoot());
            result.put("emulator", getEmulator());
            result.put("installer", getInstaller());
            result.put("apkSha", getApkSha());

            JSONArray signaturesResult = new JSONArray();
            List<String> signaturesList = getAppSignatures();
            for (String signature : signaturesList) {
                signaturesResult.put(signature);
            }
            result.put("signatures", signaturesResult);

        } catch (JSONException ignore) {
        }

        return result.toString();
    }

    private static boolean getDebug() {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    private static boolean getRoot() {
        for (String pathDir : System.getenv("PATH").split(":")) {
            if (new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }

    private static boolean getEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    private static String getInstaller() {
        String result = context.getPackageManager()
                .getInstallerPackageName(context.getPackageName());
        return result != null ? result : "";
    }


    private static List<String> getAppSignatures() {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(
                            context.getPackageName(),
                            PackageManager.GET_SIGNATURES
                    );
            List<String> results = new ArrayList<>();
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                results.add(
                        Base64.encodeToString(md.digest(), Base64.DEFAULT)
                                .replaceAll("[^A-Za-z0-9=]", "")
                );
            }

            return results;
        } catch (Exception ignored) {
        }

        return null;
    }


    private static String getApkSha() {

        String apkPath = context.getPackageCodePath();
        MessageDigest msgDigest = null;

        try {

            msgDigest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[1024];
            int byteCount;
            FileInputStream fis = new FileInputStream(new File(apkPath));

            while ((byteCount = fis.read(bytes)) > 0) {
                msgDigest.update(bytes, 0, byteCount);
            }

            BigInteger bi = new BigInteger(1, msgDigest.digest());
            String sha = bi.toString(16);

            fis.close();

            return sha;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
