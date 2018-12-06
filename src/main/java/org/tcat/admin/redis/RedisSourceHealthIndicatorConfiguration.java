package org.tcat.admin.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.tcat.admin.health.AbstractHealthIndicator;
import org.tcat.admin.health.Health;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Client;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author lin
 * @date 2018/12/5
 */
@Configuration
public class RedisSourceHealthIndicatorConfiguration extends AbstractHealthIndicator<RedisConnectionFactory> {

    static final String VERSION = "version";
    static final String REDIS_VERSION = "redis_version";

    @Autowired(required = false)
    private List<RedisConnectionFactory> redisConnectionFactorys;

    @Override
    protected String getName(RedisConnectionFactory redisConnectionFactory) {
        return "Redis";
    }

    @Override
    protected List<RedisConnectionFactory> getTs() {
        return redisConnectionFactorys;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder, RedisConnectionFactory redisConnectionFactory) throws Exception {
        RedisConnection connection = RedisConnectionUtils.getConnection(redisConnectionFactory);
        try {
            Properties info = connection.info();
            BinaryJedis jedis = ((BinaryJedis) connection.getNativeConnection());
            Client client = jedis.getClient();
            if (jedis.isConnected() && jedis.ping().equals("PONG")) {
                builder.up()
                        .withDetail(VERSION, info.getProperty(REDIS_VERSION))
                        .withDetail("host", client.getHost())
                        .withDetail("port", client.getPort())
                        .withDetail("localPort", client.getSocket().getLocalPort())
                        .withDetail("timeout", client.getTimeout());
            } else {
                builder.down().withException(new RedisConnectionFailureException("Redis 连接异常"));
            }
        } finally {
            RedisConnectionUtils.releaseConnection(connection, redisConnectionFactory);
        }
    }

    @Override
    public boolean open() {
        return Objects.nonNull(redisConnectionFactorys) && !redisConnectionFactorys.isEmpty();
    }
}
