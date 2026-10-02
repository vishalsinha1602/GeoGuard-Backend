package com.backend.geosentinel.devices.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

/**
 * Extends the existing PostgreSQL device type check constraint to accept ESP32.
 * Existing rows and all existing device types remain valid.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceTypeConstraintMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<String> definitions = jdbcTemplate.query(
                """
                select pg_get_constraintdef(c.oid)
                from pg_constraint c
                join pg_class t on t.oid = c.conrelid
                join pg_namespace n on n.oid = t.relnamespace
                where c.conname = 'devices_type_check'
                  and t.relname = 'devices'
                  and n.nspname = 'public'
                """,
                (resultSet, rowNumber) -> resultSet.getString(1)
        );

        if (definitions.isEmpty()
                || definitions.get(0).toUpperCase().contains("ESP32")) {
            return;
        }

        jdbcTemplate.execute(
                "alter table public.devices drop constraint devices_type_check"
        );
        jdbcTemplate.execute(
                """
                alter table public.devices
                add constraint devices_type_check
                check (type in (
                    'MOBILE', 'LAPTOP', 'VEHICLE', 'BIKE',
                    'SMART_WATCH', 'GPS_TRACKER', 'ESP32', 'OTHER'
                ))
                """
        );

        log.info("Updated devices_type_check to allow ESP32 devices");
    }
}
