package org.tcat.admin.health;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author lin
 * @date 2018/12/4
 */
public abstract class AbstractHealthIndicator<T> implements HealthIndicator {

    private static final String NO_MESSAGE = null;

    private static final String DEFAULT_MESSAGE = "Health check failed";

    private final Log logger = LogFactory.getLog(getClass());

    private final Function<Exception, String> healthCheckFailedMessage;

    /**
     * Create a new {@link AbstractHealthIndicator} instance with a default
     * {@code healthCheckFailedMessage}.
     */
    protected AbstractHealthIndicator() {
        this(NO_MESSAGE);
    }

    /**
     * Create a new {@link AbstractHealthIndicator} instance with a specific message to
     * log when the health check fails.
     *
     * @param healthCheckFailedMessage the message to log on health check failure
     * @since 2.0.0
     */
    protected AbstractHealthIndicator(String healthCheckFailedMessage) {
        this.healthCheckFailedMessage = (ex) -> healthCheckFailedMessage;
    }

    /**
     * Create a new {@link AbstractHealthIndicator} instance with a specific message to
     * log when the health check fails.
     *
     * @param healthCheckFailedMessage the message to log on health check failure
     * @since 2.0.0
     */
    protected AbstractHealthIndicator(
            Function<Exception, String> healthCheckFailedMessage) {
        Assert.notNull(healthCheckFailedMessage,
                "HealthCheckFailedMessage must not be null");
        this.healthCheckFailedMessage = healthCheckFailedMessage;
    }

    protected abstract String getName(T t);

    protected abstract List<T> getTs();

    @Override
    public final Multimap<String,Health> healths(){
        Multimap<String,Health> data= ArrayListMultimap.create();
        if(Objects.isNull(getTs())||getTs().isEmpty()){
            return data;
        }
        for (T t : getTs()) {
            Health health=health(t);
            data.put(health.getName(),health);
        }
        return data;
    }

    public final Health health(T t) {
        Health.Builder builder = new Health.Builder().withName(getName(t));
        try {
            doHealthCheck(builder, t);
        } catch (Exception ex) {
            if (this.logger.isWarnEnabled()) {
                String message = this.healthCheckFailedMessage.apply(ex);
                this.logger.warn(StringUtils.hasText(message) ? message : DEFAULT_MESSAGE,
                        ex);
            }
            builder.down(ex);
        }
        return builder.build();
    }

    /**
     * Actual health check logic.
     *
     * @param builder the {@link Health.Builder} to report health status and details
     * @throws Exception any {@link Exception} that should create a {@link Status#DOWN}
     *                   system status.
     */
    protected abstract void doHealthCheck(Health.Builder builder,T t) throws Exception;

}
