-- Non-destructive ownership migration for an existing job_tracker database.
-- Back up the database first. This script never assigns an owner to existing data.
USE job_tracker;

DROP PROCEDURE IF EXISTS migrate_application_owners;
DELIMITER $$

CREATE PROCEDURE migrate_application_owners()
BEGIN
    DECLARE owner_column_count INT DEFAULT 0;
    DECLARE invalid_owner_count BIGINT DEFAULT 0;
    DECLARE user_index_count INT DEFAULT 0;
    DECLARE matching_fk_count INT DEFAULT 0;
    DECLARE conflicting_fk_name_count INT DEFAULT 0;

    SELECT COUNT(*) INTO owner_column_count
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'applications'
      AND column_name = 'user_id';

    IF owner_column_count = 0 THEN
        ALTER TABLE applications ADD COLUMN user_id INT NULL AFTER application_id;
    END IF;

    SELECT COUNT(*) INTO invalid_owner_count
    FROM applications a
    LEFT JOIN user_details u ON u.user_id = a.user_id
    WHERE a.user_id IS NULL OR u.user_id IS NULL;

    IF invalid_owner_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'NULL/invalid application owners found. Back up DB; assign each owner from trusted records, then rerun. No owners guessed.';
    END IF;

    SELECT COUNT(*) INTO user_index_count
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'applications'
      AND index_name = 'idx_applications_user_id';

    SELECT COUNT(*) INTO matching_fk_count
    FROM information_schema.key_column_usage
    WHERE constraint_schema = DATABASE()
      AND table_name = 'applications'
      AND column_name = 'user_id'
      AND referenced_table_name = 'user_details'
      AND referenced_column_name = 'user_id';

    SELECT COUNT(*) INTO conflicting_fk_name_count
    FROM information_schema.table_constraints
    WHERE constraint_schema = DATABASE()
      AND table_name = 'applications'
      AND constraint_name = 'fk_app_user'
      AND constraint_type = 'FOREIGN KEY';

    IF conflicting_fk_name_count > 0 AND matching_fk_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'fk_app_user conflicts with another foreign key. Resolve the name conflict, then rerun migration.';
    END IF;

    ALTER TABLE applications MODIFY COLUMN user_id INT NOT NULL;

    IF user_index_count = 0 THEN
        CREATE INDEX idx_applications_user_id ON applications(user_id);
    END IF;

    IF matching_fk_count = 0 THEN
        ALTER TABLE applications
            ADD CONSTRAINT fk_app_user
            FOREIGN KEY (user_id) REFERENCES user_details(user_id);
    END IF;
END$$

DELIMITER ;

CALL migrate_application_owners();
DROP PROCEDURE IF EXISTS migrate_application_owners;
