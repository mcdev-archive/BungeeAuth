package me.loper.bungeeauth.storage.implementation.sql.connection.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.loper.bungeeauth.storage.implementation.sql.converters.HashMethodConverter;
import me.loper.bungeeauth.authentication.hash.HashMethodType;
import me.loper.bungeeauth.storage.implementation.sql.connection.SqlConnectionFactory;
import me.loper.bungeeauth.storage.misc.DatabaseStorageCredentials;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import org.sql2o.converters.Converter;
import org.sql2o.quirks.PostgresQuirks;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class HikariConnectionFactory implements SqlConnectionFactory {

    protected final DatabaseStorageCredentials configuration;
    private HikariDataSource hikari;
    private Sql2o sql2o;

    public HikariConnectionFactory(DatabaseStorageCredentials configuration) {
        this.configuration = configuration;
    }

    protected String getDriverClass() {
        return null;
    }

    protected void appendProperties(HikariConfig config, Map<String, String> properties) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    protected void appendConfigurationInfo(HikariConfig config) {
        String address = this.configuration.getAddress();
        String[] addressSplit = address.split(":");
        address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1] : "3306";

        config.setDataSourceClassName(getDriverClass());
        config.addDataSourceProperty("serverName", address);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", this.configuration.getDatabase());
        config.setUsername(this.configuration.getUsername());
        config.setPassword(this.configuration.getPassword());
    }

    @Override
    public void init() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("bungeeauth-hikari");

        appendConfigurationInfo(config);
        appendProperties(config, new HashMap<>(this.configuration.getProperties()));

        config.setMaximumPoolSize(this.configuration.getMaxPoolSize());
        config.setMinimumIdle(this.configuration.getMinIdleConnections());
        config.setMaxLifetime(this.configuration.getMaxLifetime());
        config.setConnectionTimeout(this.configuration.getConnectionTimeout());

        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);
        this.sql2o = new Sql2o(this.hikari, new PostgresQuirks(createMappers()));
        this.sql2o.setDefaultColumnMappings(getColumnMappings());
    }

    @SuppressWarnings("rawtypes")
    public Map<Class, Converter> createMappers() {
        Map<Class, Converter> mappers = new HashMap<>();

        mappers.put(HashMethodType.class, new HashMethodConverter());

        return mappers;
    }

    private Map<String, String> getColumnMappings() {
        Map<String, String> colMaps = new HashMap<>();

        colMaps.put("unique_id", "uniqueId");
        colMaps.put("created_at", "createdAt");
        colMaps.put("updated_at", "updatedAt");
        colMaps.put("registered_ip", "registeredIp");
        colMaps.put("registered_at", "registeredAt");
        colMaps.put("hash_method_type", "hashMethodType");

        return colMaps;
    }

    @Override
    public void shutdown() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }
        try {
            return this.sql2o.open();
        } catch (Sql2oException ex) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }
    }
}