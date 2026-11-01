-- DESTRUCTIVE, OPT-IN RESET ONLY.
-- This permanently drops all tables and data in job_tracker, including user accounts.
-- Never run this against a database containing data you need to preserve.
DROP DATABASE IF EXISTS job_tracker;
