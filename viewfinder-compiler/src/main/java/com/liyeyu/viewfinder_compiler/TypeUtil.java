package com.liyeyu.viewfinder_compiler;

import com.squareup.javapoet.ClassName;

/**
 * Created by Liyeyu on 2017/2/23.
 */

public class TypeUtil {
    public static final ClassName ANDROID_VIEW = ClassName.get("android.view", "View");
    public static final ClassName ANDROID_ON_CLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    public static final ClassName FINDER = ClassName.get("com.liyeyu.viewfinder", "Finder");
    public static final ClassName PROVIDER = ClassName.get("com.liyeyu.viewfinder.provider", "Provider");
}
