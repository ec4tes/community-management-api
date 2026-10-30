package com.devkit.hsd.service.impl;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantMigrationDocumentationTest {

    @Test
    void hardeningMigration_containsExpectedTenantConstraints() throws IOException {
        String migration = Files.readString(Path.of("src/main/resources/db/migration/V2__tenant_constraint_hardening.sql"));

        assertTrue(migration.contains("ALTER TABLE projects"));
        assertTrue(migration.contains("ALTER COLUMN community_id SET NOT NULL"));
        assertTrue(migration.contains("FOREIGN KEY (community_id) REFERENCES communities (id)"));
        assertTrue(migration.contains("uk_event_identity_per_community"));
        assertTrue(migration.contains("uk_meeting_identity_per_community"));
        assertTrue(migration.contains("uk_project_name_per_committee_tenant"));
        assertTrue(migration.contains("uk_project_task_title_per_project_tenant"));
        assertTrue(migration.contains("CREATE INDEX idx_task_point_record_community_created_at"));
    }
}
