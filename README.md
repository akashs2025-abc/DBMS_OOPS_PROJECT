# CareerTrack

CareerTrack is a Java and MySQL application for tracking job and internship
applications, interviews, follow-ups, and offers. The browser interface is
served by the Java HTTP server.

## Requirements

- Java 17 or newer.
- MySQL 8.0.16 or newer (the schema uses enforced `CHECK` constraints).
- MySQL Connector/J available on the Java runtime classpath.

## Fresh setup

Run these scripts in order against a new or empty MySQL instance:

1. `database/schema.sql` creates the database and tables.
2. `database/plsql.sql` creates triggers, a procedure, and a function.

Users register through the web application. Normal setup does not create demo
accounts. `database/sample_data.sql` is an optional test fixture for companies;
it does not clear existing data.

For an existing database, make and verify a restorable backup first. Then run
`database/migrate_application_owners.sql`; it stops if any application has a
NULL or invalid owner, and does not guess ownership. Resolve those records from
trusted information before rerunning. Next run
`database/migrate_offer_status.sql`, then `database/plsql.sql`. Existing offers
have unknown prior application status; deleting one will not reset that status.
The web server checks that required ownership and offer-status schema objects
exist, but does not perform migrations automatically.

`database/reset.sql` is a separate destructive script that drops the entire
`job_tracker` database, including accounts. Do not run it where data must be
preserved.

## Compile and launch

From the repository root, compile the Java sources:

```sh
mkdir -p /tmp/careertrack-classes
javac -Xlint:all -d /tmp/careertrack-classes $(find backend/src -name '*.java' -print)
```

Set `DB_USER` and `DB_PASSWORD` in the process environment. Optionally set
`DB_URL`; otherwise the application connects to the local `job_tracker`
database. Do not put database credentials in source files or command-line
arguments. Add the installed Connector/J JAR to the runtime classpath and run
the web server from the repository root so it can find `frontend/`:

```sh
java -cp "/tmp/careertrack-classes:/path/to/mysql-connector-j.jar" util.WebServer
```

Open `http://localhost:8080`. `main.Main` intentionally refuses to start the
legacy console program because its old DAO operations are not user-scoped; use
the authenticated web application for shared multi-user data.

## Verification

Java compile check:

```sh
javac -Xlint:all -d /tmp/careertrack-classes $(find backend/src -name '*.java' -print)
```

No automated integration tests are included. Run the following only on an
isolated, disposable test database created from `database/schema.sql`—never
against production or data that must be retained:

1. Register two users. Create distinct applications for each. Check that list,
   search, status/type filters, counts, and reports show only the signed-in
   user's records.
2. As user A, try to update/delete user B's application and to create
   interviews/offers for B's application or update/delete B's interview/offer.
   Verify records remain unchanged and a safe error is shown.
3. Submit invalid IDs, dates, status/type values, and package values. Verify
   rejection and a visible error; try unsupported methods and verify HTTP 405.
4. Create an offer for a `Shortlisted` application; verify status becomes
   `Offered`. Delete it and verify status restores to `Shortlisted`. Change the
   status after offer creation (including changing away from and back to
   `Offered`) before deletion; verify the later status is preserved.
5. On disposable copies, give an application a NULL owner and an invalid owner
   in separate tests. Verify the owner migration stops without adding the
   constraint. Correct ownership only in the test copy, rerun, and verify
   `user_id` is `NOT NULL` with a foreign key to `user_details`.

No destructive reset, database connection, or integration test is required to
perform the Java compile check.
