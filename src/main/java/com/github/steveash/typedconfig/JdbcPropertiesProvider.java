package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.PropertiesProvider;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.Properties;

/**
 * @author: drudenko
 */
public class JdbcPropertiesProvider implements PropertiesProvider {
    private final JdbcTemplate jdbcTemplate;
    private final String sql;

    public JdbcPropertiesProvider(final JdbcTemplate jdbcTemplate, final String sql) {
        this.jdbcTemplate = jdbcTemplate;
        this.sql = sql;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        properties.putAll(result);
        return properties;
    }
}
