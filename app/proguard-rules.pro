# Add project specific ProGuard rules here.
# By default_pic, the flags in this file are appended to flags specified
# in /home/android/android-sdk/tools/proguard/proguard-android.txt
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

-keep class com.facebook.** { *;}
-dontwarn com.facebook.**
-keep class com.tencent.**
-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
#-keep class com.tencent.open.PKDialog
#-keep class com.tencent.open.PKDialog {*;}
#-keep class com.tencent.open.PKDialog$*
#-keep class com.tencent.open.PKDialog$* {*;}
-keep class butterknife.** { *; }
-keep class **$$ViewInjector { *; }
-keep class com.google.gson.** {*;}
-dontwarn com.google.gson.**

-keep class retrofit.** {*;}
-dontwarn retrofit.**

-keep class org.henjue.library.** {*;}
-dontwarn org.henjue.library..**


#-keep class org.henjue.library.** {*;}
#-dontwarn org.henjue.library.**

-keep class org.codehaus.** {*;}
-dontwarn org.codehaus.**

-keep class com.squareup.** {*;}
-dontwarn com.squareup.**


-dontwarn butterknife.internal.**
-dontwarn java.lang.invoke.*
-dontwarn maimeng.yodian.app.client.android.model.**
-keep class maimeng.yodian.app.client.android.model.** { *; }

-dontwarn maimeng.yodian.app.client.android.common.model.**
-keep class maimeng.yodian.app.client.android.common.model.** { *; }
-keep class maimeng.yodian.app.client.android.network.response.** {*;}
-dontwarn maimeng.yodian.app.client.android.network.response.**

-keep class maimeng.yodian.app.client.android.javascripts.** {*;}
-dontwarn maimeng.yodian.app.client.android.javascripts.**

#-keep public class * implements maimeng.yodian.app.client.android.network.response.TypeData{*;}

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

#auto-scroll-viewpager
-keep class cn.trinea.android.** { *; }
-keepclassmembers class cn.trinea.android.** { *; }
-dontwarn cn.trinea.android.**

-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes *JavascriptInterface*

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {*;}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

#友盟 start
-keep class com.umeng.message.* {
        public <fields>;
        public <methods>;
}

-keep class com.umeng.message.protobuffer.* {
        public <fields>;
        public <methods>;
}

-keep class com.squareup.wire.* {
        public <fields>;
        public <methods>;
}

-keep class org.android.agoo.impl.*{
        public <fields>;
        public <methods>;
}

-keep class org.android.agoo.service.* {*;}

-keep class org.android.spdy.**{*;}
#友盟 end

-keep class **.R$* { *;}

-keep class * extends android.support.v4.app.Fragment{*;}
-keep class * extends android.app.Activity{*;}
-keep class * extends android.support.v7.app.AppCompatActivity{*;}
-keep class * extends android.app.Service{*;}
-keep class * implements org.henjue.library.hnet.RequestFilter {*;}

#
#Bugly接口
-keep public class com.tencent.bugly.crashreport.crash.jni.NativeCrashHandler{public *; native <methods>;}
-keep public interface com.tencent.bugly.crashreport.crash.jni.NativeExceptionHandler{*;}
-dontwarn java.lang.invoke.*


# 新浪微博
-keep class com.sina.**{*;}
-dontwarn com.sina.**

# baidu
-keep class com.baidu.**{*;}
-dontwarn com.baidu.**

-keep class org.android.agoo.net.async.**{*;}
-dontwarn org.android.agoo.net.async.**

-keep class org.apache.**{*;}
-dontwarn org.apache.**

-keep class com.ta.utdid2.aid.**{*;}
-dontwarn com.ta.utdid2.aid.**

-keep class com.ument.**{*;}
-dontwarn com.ument.**


-keep class u.aly.bw.**{*;}
-dontwarn u.aly.bw.**


# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }


# 保持自定义控件类不被混淆
-keep class * extends android.view.View{*;}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#2.0.9后的不需要加下面这个keep
#-keep class org.xbill.DNS.** {*;}
#另外，demo中发送表情的时候使用到反射，需要keep SmileUtils,注意前面的包名，
#不要SmileUtils复制到自己的项目下keep的时候还是写的demo里的包名
-keep class maimeng.yodian.app.client.android.utils.SmileUtils {*;}

#2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
-dontwarn ch.imvs.**
-dontwarn org.slf4j.**
-keep class org.ice4j.** {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}



-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

#-libraryjars libs/easemobchat_2.2.1.jar