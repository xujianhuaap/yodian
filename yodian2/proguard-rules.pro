# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\dev_tools\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *

-dontwarn io.realm.internal.Keep
-dontwarn javax.**
-dontwarn io.realm.**

-keep class com.squareup.**{*;}
-dontwarn com.squareup.**

-keep class com.google.vending.licensing.ILicensingService
-dontwarn com.google.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService

-keep class org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontnote org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keep class com.android.vending.licensing.ILicensingService
-dontwarn com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

-keep class org.parceler.**
-dontwarn org.parceler

-keep class org.robolectric.**
-dontwarn org.robolectric

# 重复的类
-dontnote android.net.http.SslError
-dontnote android.net.http.SslCertificate
-dontnote android.net.http.SslCertificate$DName
-dontnote org.apache.http.conn.scheme.HostNameResolver
-dontnote org.apache.http.conn.scheme.SocketFactory
-dontnote org.apache.http.conn.ConnectTimeoutException
-dontnote org.apache.http.params.HttpParams

#反射
-keepattributes *Annotation*.
#由于预编译
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption