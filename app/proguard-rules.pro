# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/billy/Library/Android/sdk/tools/proguard/proguard-android.txt
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

#proguard用法: http://blog.csdn.net/u014134488/article/details/51038727
#混淆与完整类名一致的字符串。没指定过滤器时，所有符合现有类的完整类名的字符串常量均会混淆。只有开启混淆时可用。
#-adaptclassstrings [class_filter]
