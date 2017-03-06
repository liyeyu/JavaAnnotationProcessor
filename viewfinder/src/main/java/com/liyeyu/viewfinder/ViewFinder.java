package com.liyeyu.viewfinder;

import android.app.Activity;
import android.view.View;

import com.liyeyu.viewfinder.provider.ActivityProvider;
import com.liyeyu.viewfinder.provider.Provider;
import com.liyeyu.viewfinder.provider.ViewProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liyeyu on 2017/2/23.
 */

public class ViewFinder {

    private static final ActivityProvider PROVIDER_ACTIVITY = new ActivityProvider();
    private static final ViewProvider PROVIDER_VIEW = new ViewProvider();

    private static final Map<String, Finder> FINDER_MAP = new HashMap<>();

    public static void inject(Activity host){
        inject(host,host,PROVIDER_ACTIVITY);
    }
    public static void inject(View host){
        inject(host,host);
    }
    public static void inject(Object host,View source){
        inject(host,source,PROVIDER_VIEW);
    }


    public static void inject(Object host, Object source, Provider provider){
        String name = host.getClass().getName();
        try {
            Finder finder = FINDER_MAP.get(name);
            if(finder == null){
                Class<?> finderClass = Class.forName(name + "$$Finder");
                finder  = (Finder) finderClass.newInstance();
                FINDER_MAP.put(name,finder);
            }
            finder.inject(host,source,provider);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
