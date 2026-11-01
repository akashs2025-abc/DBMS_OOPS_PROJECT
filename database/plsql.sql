USE job_tracker;

DELIMITER $$

DROP TRIGGER IF EXISTS trg_before_app_status_revision$$

CREATE TRIGGER trg_before_app_status_revision
BEFORE UPDATE ON applications
FOR EACH ROW
BEGIN
    IF NOT (NEW.status <=> OLD.status) THEN
        SET NEW.status_revision = OLD.status_revision + 1;
    ELSE
        SET NEW.status_revision = OLD.status_revision;
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_before_offer_insert$$

CREATE TRIGGER trg_before_offer_insert
BEFORE INSERT ON offers
FOR EACH ROW
BEGIN
    DECLARE previous_status VARCHAR(32) DEFAULT NULL;
    DECLARE previous_revision BIGINT UNSIGNED DEFAULT NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET previous_status = NULL, previous_revision = NULL;

    SELECT status, status_revision INTO previous_status, previous_revision
    FROM applications
    WHERE application_id = NEW.application_id
    FOR UPDATE;

    SET NEW.previous_application_status = previous_status;
    IF previous_status IS NOT NULL THEN
        SET NEW.offered_status_revision = previous_revision
            + IF(previous_status = 'Offered', 0, 1);
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_after_offer_insert$$

CREATE TRIGGER trg_after_offer_insert
AFTER INSERT ON offers
FOR EACH ROW
BEGIN
    UPDATE applications
    SET status = 'Offered'
    WHERE application_id = NEW.application_id;
END$$

DROP TRIGGER IF EXISTS trg_after_offer_delete$$

CREATE TRIGGER trg_after_offer_delete
AFTER DELETE ON offers
FOR EACH ROW
BEGIN
    IF OLD.previous_application_status IS NOT NULL
       AND OLD.offered_status_revision IS NOT NULL THEN
        UPDATE applications
        SET status = OLD.previous_application_status
        WHERE application_id = OLD.application_id
          AND status = 'Offered'
          AND status_revision = OLD.offered_status_revision;
    END IF;
END$$

DROP PROCEDURE IF EXISTS get_upcoming_followups$$

CREATE PROCEDURE get_upcoming_followups(IN days_ahead INT)
BEGIN
    SELECT 
        a.application_id,
        c.name AS company_name,
        a.role,
        a.follow_up_date,
        DATEDIFF(a.follow_up_date, CURDATE()) AS days_remaining
    FROM applications a
    INNER JOIN companies c ON a.company_id = c.company_id
    WHERE a.follow_up_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL days_ahead DAY)
    ORDER BY a.follow_up_date ASC;
END$$

DROP FUNCTION IF EXISTS calculate_offer_rate$$

CREATE FUNCTION calculate_offer_rate()
RETURNS DECIMAL(5,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE total_apps INT DEFAULT 0;
    DECLARE total_offers INT DEFAULT 0;
    DECLARE rate DECIMAL(5,2) DEFAULT 0.00;

    SELECT COUNT(*) INTO total_apps FROM applications;
    SELECT COUNT(*) INTO total_offers FROM offers;

    IF total_apps > 0 THEN
        SET rate = (total_offers * 100.0) / total_apps;
    ELSE
        SET rate = 0.00;
    END IF;

    RETURN rate;
END$$

DELIMITER ;
