package org.tcat.admin.health;

import com.google.common.collect.Multimap;

/**
 * @author lin
 * @date 2018/12/4
 */
public interface HealthIndicator {

    boolean open();

    /**
     * @return
     */
    Multimap<String, Health> healths();

}
