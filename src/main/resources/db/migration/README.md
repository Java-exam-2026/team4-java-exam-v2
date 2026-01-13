# Database Migration

## Adding has_submitted Column to Existing Database

If you have an existing database with user progress data, use the migration script instead of running schema.sql:

```sql
-- Run this to add the has_submitted column without losing data:
sqlite3 java-exam.db < src/main/resources/db/migration/add_has_submitted_column.sql
```

## Fresh Installation

For a fresh installation with no existing data, you can use schema.sql as usual:

```bash
# The application will automatically create tables from schema.sql on first run
./mvnw spring-boot:run
```

## Note on schema.sql

The schema.sql file uses `DROP TABLE IF EXISTS` which is appropriate for:
- Development environments
- Testing
- Fresh installations

For production environments with existing data, always use migration scripts instead.
