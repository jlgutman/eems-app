# EEMS — Employment Management System

Pure Java N-Tier backend using raw JDBC + MySQL. No frameworks, no ORMs.

## Setup

1. Run the schema: `mysql -u root -p < db/schema.sql`
2. Update credentials in `src/com/eems/util/DBConnection.java` if needed.

## Build & Run

```bash
javac -cp libs/mysql-connector-j-9.5.0.jar -d out $(find src -name "*.java")
java  -cp out:libs/mysql-connector-j-9.5.0.jar Main
```

> Windows: replace `:` with `;` in the classpath.

## Layers

| Package | Role |
|---|---|
| `model` | Domain entities |
| `repository` | Raw JDBC queries |
| `service` | Business logic & transactions |
| `util` | `DBConnection` static factory |

## Entities

`Department` · `Employee` · `Project` · `Client`
