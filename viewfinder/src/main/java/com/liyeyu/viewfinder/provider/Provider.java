package com.liyeyu.viewfinder.provider;

import android.content.Context;
import android.view.View;

/**
 * Created by Liyeyu on 2017/2/23.
 */

public interface Provider {

    Context getContext(Object source);

    View findView(Object source,int id);
}
