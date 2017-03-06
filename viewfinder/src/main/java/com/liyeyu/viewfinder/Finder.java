package com.liyeyu.viewfinder;

import com.liyeyu.viewfinder.provider.Provider;

/**
 * Created by Liyeyu on 2017/2/23.
 */

public interface Finder<T> {
    /**
     *
     * @param host
     * @param source
     * @param provider
     */
    void inject(T host, Object source, Provider provider);
}
