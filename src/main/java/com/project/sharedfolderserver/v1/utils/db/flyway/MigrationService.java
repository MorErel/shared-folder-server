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
        List<String> tenants = getAllTenants();
        tenants.forEach(this::migrate);

    }

    private void migrate(String tenant) {
        try {
            log.info("Starting migration for tenant: [{}]", tenant);
            Instant startTime = Instant.now();
            Flyway flyway = Flyway.configure()
                    .locations(migrationProperties.getSchemasPath())
                    .baselineOnMigrate(migrationProperties.isBaselineOnMigration())
                    .dataSource(dataSource)
                    .schemas(tenant)
                    .load();
            flyway.migrate();
            Instant endTime = Instant.now();
            long timeElapsed = Duration.between(startTime, endTime).toMillis();
            log.info("Finish migration for tenant [{}] in {} milliseconds", tenant, timeElapsed);
        } catch (Exception e) {
            log.error("Could not migrate tenant [{}]: {}", tenant, e.getMessage());
        }
    }

    //todo: multi-tenancy
    private List<String> getAllTenants() {
        return List.of("public");
    }
}
