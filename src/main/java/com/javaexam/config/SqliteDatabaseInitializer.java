package com.javaexam.config;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Component
public class SqliteDatabaseInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SqliteDatabaseInitializer.class);
    private static final String SQLITE_URL_PREFIX = "jdbc:sqlite:";

    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.sql.init.schema-locations:classpath:schema.sql}")
    private String schemaLocation;

    @Value("${spring.sql.init.data-locations:classpath:data.sql}")
    private String dataLocation;

    public SqliteDatabaseInitializer(DataSource dataSource, ResourceLoader resourceLoader) {
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(ApplicationArguments args) {
        Path dbPath = resolveSqliteDbPath(datasourceUrl);
        if (dbPath == null) {
            return;
        }

        // With SQLite, the file can be created as soon as the first connection is opened.
        // So we detect whether initialization is needed by checking for an expected table.
        if (hasTable("users")) {
            log.info("SQLite DB is already initialized ({}); skipping schema/data initialization.", dbPath);
            return;
        }

        log.info("Initializing SQLite DB ({}): running schema.sql and data.sql.", dbPath);

        Resource schema = resourceLoader.getResource(schemaLocation);
        Resource data = resourceLoader.getResource(dataLocation);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(schema, data);
        populator.setContinueOnError(false);
        DatabasePopulatorUtils.execute(populator, dataSource);
    }

    private boolean hasTable(String tableName) {
        String sql = "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?";
        try (var conn = dataSource.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (var rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            log.warn("Failed to detect tables; assuming DB is not initialized.", e);
            return false;
        }
    }

    private static Path resolveSqliteDbPath(String url) {
        if (url == null || !url.startsWith(SQLITE_URL_PREFIX)) {
            return null;
        }

        String location = url.substring(SQLITE_URL_PREFIX.length());
        if (location.isBlank() || ":memory:".equals(location)) {
            return null;
        }

        if (location.startsWith("file:")) {
            return Paths.get(URI.create(location)).toAbsolutePath().normalize();
        }

        return Paths.get(location).toAbsolutePath().normalize();
    }
}
