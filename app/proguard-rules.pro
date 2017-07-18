# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/edmund/android-sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#--------------------------------------------------
# Firebase
# See  https://firebase.google.com/docs/database/android/start/
-keepattributes Signature

# This rule will properly ProGuard all the model classes.
-keepclassmembers class uk.jumpingmouse.moviecompanion.data.** {
  *;
}

#--------------------------------------------------
# Retrofit2
# Lines recommended in the Retrofit2 documentation:
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
#-keepattributes Signature  # this line is already included for Firebase
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn okio.**
-dontwarn javax.annotation.**

#--------------------------------------------------
# OKHttp
#-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
#-dontwarn javax.annotation.Nullable
#-dontwarn javax.annotation.ParametersAreNonnullByDefault