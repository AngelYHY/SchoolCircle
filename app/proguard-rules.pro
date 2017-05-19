# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in d:\Users\rj\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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
#忽略七牛云混淆
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings
#
##极光推送混淆
#-dontwarn cn.jpush.**
#-keepattributes  EnclosingMethod,Signature
#-keep class cn.jpush.** { *; }
#-keepclassmembers class ** {
#    public void onEvent*(**);
#}
#
##========================gson================================
#-dontwarn com.google.**
#-keep class com.google.gson.** {*;}
#
##========================protobuf================================
##-dontwarn com.google.**
#-keep class com.google.protobuf.** {*;}
#
##ButterKnife混淆
#-keep class butterknife.** { *; }
#-dontwarn butterknife.internal.**
#-keep class **$$ViewBinder { *; }
#
#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#}
#
#-keepclasseswithmembernames class * {
#    @butterknife.* <methods>;
#}
#
##xUtils混淆
#-keepattributes Signature,*Annotation*
#-keep public class org.xutils.** {
#    public protected *;
#}
#-keep public interface org.xutils.** {
#    public protected *;
#}
#-keepclassmembers class * extends org.xutils.** {
#    public protected *;
#}
#-keepclassmembers @org.xutils.db.annotation.* class * {*;}
#-keepclassmembers @org.xutils.http.annotation.* class * {*;}
#-keepclassmembers class * {
#    @org.xutils.view.annotation.Event <methods>;
#}
#
##Glide
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#    **[] $VALUES;
#    public *;
#}
#
##mob混淆
#-keep class cn.sharesdk.**{*;}
#-keep class com.sina.**{*;}
#-keep class **.R$* {*;}
#-keep class **.R{*;}
#-dontwarn cn.sharesdk.**
#-dontwarn **.R$*
#-keep class m.framework.**{*;}
#-keep class android.net.http.SslError
#-keep class android.webkit.**{*;}
#-keep class com.mob.tools.utils
#-keep class com.xxx.share.onekey.theme.classic.EditPage