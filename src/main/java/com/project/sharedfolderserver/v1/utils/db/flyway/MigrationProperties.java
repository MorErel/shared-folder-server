package com.project.sharedfolderserver.v1.utils.db.flyway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "migration")
@Data
/**
 * Class for migration application properties
 */
public class MigrationProperties {
    private String schemasPath;
    private boolean enabled;
    private boolean baselineOnMigration;
}