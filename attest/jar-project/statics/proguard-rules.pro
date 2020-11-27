# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class com.assukar.android.attest.statics.AndroidAttestStatics {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontwarn com.assukar.android.attest.statics.R*
#-injars bin/project.jar
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class com.assukar.android.attest.statics.AndroidAttestStatics
-keep public class android.content.Context
-keep public class android.content.pm.ApplicationInfo
-keep public class android.content.pm.PackageInfo
-keep public class android.content.pm.PackageManager
-keep public class android.content.pm.Signature
-keep public class android.os.Build
-keep public class android.util.Base64
-keep public class org.json.JSONArray
-keep public class org.json.JSONException
-keep public class org.json.JSONObject
-keep public class java.io.File
-keep public class java.io.FileInputStream
-keep public class java.math.BigInteger
-keep public class java.security.MessageDigest
-keep public class java.util.ArrayList
-keep public class java.util.List

-keepclasseswithmembers class * {
   public static *** parse(...);
}

-keepclassmembers public class com.assukar.android.attest.statics.AndroidAttestStatics {
    public static <fields>;
}
