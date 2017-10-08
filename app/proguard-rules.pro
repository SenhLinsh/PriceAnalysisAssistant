# Add project specific ProGuard rules here.
  # By default, the flags in this file are appended to flags specified
  # in /Users/yy/Library/Android/sdk/tools/proguard/proguard-android.txt
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


#---------------------------------1.实体类---------------------------------
-keep class com.linsh.paa.model.** { *; }
-keep class com.linsh.paa.view.** {*;}
-keep class com.linsh.lshutils.** {*;}
-keep class com.linsh.lshapp.common.** {*;}

#---------------------------------2.第三方包-------------------------------
#gson
-keep class com.google.gson.** {*;}
-keep class com.google.gson.examples.android.model.** { *; }

#Glide
-keep class com.bumptech.glide.** {*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#okHttp
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *; }
-keep class okhttp3.** {*;}
-keep class okio.** {*;}
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
-dontwarn com.squareup.**
-dontwarn okio.**

# Realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Retrolambda
-dontwarn java.lang.invoke.*

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class com.github.tamir7.contacts.** { *;}
-keep class com.github.chrisbanes.photoview.** { *;}
-keep class cn.carbswang.android.numberpickerview.library.** { *;}

-keep class com.google.** { *;}
-keep class org.apache.** { *;}
-keep class com.squareup.** {*;}
-keep class com.tencent.** {*;}
-keep class com.github.** {*;}

-keep class java.** {*;}
-dontwarn java.**
#-dontwarn java.lang.management.**
-keep class org.** {*;}
-dontwarn org.**
-keep class sun.** {*;}
-dontwarn sun.**
-keep class rx.** {*;}
-dontwarn rx.**
-keep class io.** {*;}
-dontwarn io.**


-keepattributes *Annotation*
-keepattributes EnclosingMethod

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------

-optimizationpasses 5 # 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-dontskipnonpubliclibraryclassmembers # 混合时不使用大小写混合，混合后的类名为小写
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/* # 指定混淆是采用的算法，这个过滤器是谷歌推荐的算法，一般不做更改
-keepattributes *Annotation*,InnerClasses # 保留Annotation不混淆
-keepattributes Signature # 避免混淆泛型
-keepattributes SourceFile,LineNumberTable # 抛出异常时保留代码行号


#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

-keep class android.support.** {*;}
-keep public class * extends android.os.IInterface
-keep public class * extends android.support.annotation.**

# 不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
 native <methods>;
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留在Activity中的方法参数是view的方法，这样在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# webView处理，项目中没有使用到webView忽略即可
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}