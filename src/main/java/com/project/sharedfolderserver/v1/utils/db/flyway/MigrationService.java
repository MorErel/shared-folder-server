package com.project.sharedfolderserver.v1.utils.db.flyway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
/**
 * Migration service
 * on bean init, Will run the migration script from the MigrationProperties.SchemaPath
 * (default: db.migrations under the resource folder)
 */
public class MigrationService {
    private final DataSource dataSource;
    private final MigrationProperties migrationProperties;

    @PostConstruct
    void onStart() {
        if (migrationProperties.isEnabled()) {
            migrate();
        }
    }

    private void migrate() {
        List<String> tenants = getAllSchemas();
        tenants.forEach(this::migrate);

    }

    /**
     * running flyway migration for the given schema
     * @param schema - the requested schema
     */
    private void migrate(String schema) {
        try {
            log.info("Starting migration for schema: [{}]", schema);
            Instant startTime = Instant.now();
            Flyway flyway = Flyway.configure()
                    .locations(migrationProperties.getSchemasPath())
                    .baselineOnMigrate(migrationProperties.isBaselineOnMigration())
                    .dataSource(dataSource)
                    .schemas(schema)
                    .load();
            flyway.migrate();
            Instant endTime = Instant.now();
            long timeElapsed = Duration.between(startTime, endTime).toMillis();
            log.info("Finish migration for schema [{}] in {} milliseconds", schema, timeElapsed);
        } catch (Exception e) {
            log.error("Could not migrate schema [{}]: {}", schema, e.getMessage());
        }
    }

    //todo: if there's time - multi-schemas
    private List<String> getAllSchemas() {
        return List.of("public");
    }
}
