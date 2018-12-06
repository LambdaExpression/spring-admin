package org.tcat.admin.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.tcat.admin.health.AbstractHealthIndicator;
import org.tcat.admin.health.Health;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author lin
 * @date 2018/12/4
 */
@Configuration
public class DataSourceHealthIndicatorConfiguration extends AbstractHealthIndicator<JdbcTemplate> {

    private static final String QUERY = "SELECT 1";

    @Autowired(required = false)
    private List<JdbcTemplate> jdbcTemplates;

    @Override
    protected String getName(JdbcTemplate jdbcTemplate) {
        return product(jdbcTemplate);
    }

    @Override
    protected List<JdbcTemplate> getTs() {
        return jdbcTemplates;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder, JdbcTemplate jdbcTemplate) throws Exception {
        doDataSourceHealthCheck(builder, jdbcTemplate);
    }

    @Override
    public boolean open() {
        return Objects.nonNull(jdbcTemplates) && !jdbcTemplates.isEmpty();
    }

    private void doDataSourceHealthCheck(Health.Builder builder, JdbcTemplate jdbcTemplate) throws Exception {
        String product = product(jdbcTemplate);
        List<Object> results = jdbcTemplate.query(QUERY, new SingleColumnRowMapper());
        Object result = DataAccessUtils.requiredSingleResult(results);
        DatabaseMetaData databaseMetaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
        builder.up()
                .withDetail("database", product)
                .withDetail("hello", result)
                .withDetail("url", databaseMetaData.getURL())
                .withDetail("username", databaseMetaData.getUserName().split("@")[0])
                .withDetail("driverName", databaseMetaData.getDriverName())
                .withDetail("driverVersion", databaseMetaData.getDriverVersion())
                .withDetail("databaseProductName", databaseMetaData.getDatabaseProductName())
                .withDetail("databaseProductVersion", databaseMetaData.getDatabaseProductVersion());
    }

    private String product(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.execute((ConnectionCallback<String>) this::product);
    }

    private String product(Connection connection) throws SQLException {
        return connection.getMetaData().getDatabaseProductName();
    }

}
