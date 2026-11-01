
USE job_tracker;

CREATE TABLE IF NOT EXISTS user_details (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    normalized_name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

DROP PROCEDURE IF EXISTS migrate_user_details_table;

DELIMITER $$
CREATE PROCEDURE migrate_user_details_table()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS 
        WHERE TABLE_SCHEMA = DATABASE() 
          AND TABLE_NAME = 'user_details' 
          AND COLUMN_NAME = 'normalized_name'
    ) THEN
        ALTER TABLE user_details 
            ADD COLUMN normalized_name VARCHAR(100) NULL AFTER full_name;

        UPDATE user_details 
            SET normalized_name = LOWER(TRIM(full_name)) 
            WHERE normalized_name IS NULL;

        ALTER TABLE user_details 
            MODIFY COLUMN normalized_name VARCHAR(100) NOT NULL,
            ADD CONSTRAINT uq_user_normalized_name UNIQUE (normalized_name);
    END IF;
END$$
DELIMITER ;

CALL migrate_user_details_table();
DROP PROCEDURE IF EXISTS migrate_user_details_table;

DESCRIBE user_details;
