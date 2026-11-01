-- Safe, idempotent prerequisite for the offer-status triggers in plsql.sql.
-- Existing offers retain NULL prior status because their history is unknown.
USE job_tracker;

DROP PROCEDURE IF EXISTS migrate_offer_status;
DELIMITER $$

CREATE PROCEDURE migrate_offer_status()
BEGIN
    DECLARE previous_status_column_count INT DEFAULT 0;
    DECLARE app_status_revision_count INT DEFAULT 0;
    DECLARE offer_status_revision_count INT DEFAULT 0;

    SELECT COUNT(*) INTO app_status_revision_count
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'applications'
      AND column_name = 'status_revision';

    IF app_status_revision_count = 0 THEN
        ALTER TABLE applications
            ADD COLUMN status_revision BIGINT UNSIGNED NOT NULL DEFAULT 0 AFTER status;
    END IF;

    SELECT COUNT(*) INTO previous_status_column_count
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'offers'
      AND column_name = 'previous_application_status';

    IF previous_status_column_count = 0 THEN
        ALTER TABLE offers
            ADD COLUMN previous_application_status
                ENUM('Applied', 'Shortlisted', 'Interview Scheduled', 'Offered', 'Rejected') NULL
                AFTER offer_status;
    END IF;

    SELECT COUNT(*) INTO offer_status_revision_count
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'offers'
      AND column_name = 'offered_status_revision';

    IF offer_status_revision_count = 0 THEN
        ALTER TABLE offers
            ADD COLUMN offered_status_revision BIGINT UNSIGNED NULL
            AFTER previous_application_status;
    END IF;
END$$

DELIMITER ;

CALL migrate_offer_status();
DROP PROCEDURE IF EXISTS migrate_offer_status;
