# demo

## Prerequisites
- Java 8 or later run time environment
- Docker, please make sure your docker has enough memory
  - If you use Colima: `colima start --cpu 4 --memory 8`

## Tasks
- start: `./batect start`
- unit test: `./batect unitTest` or `./gradlew test`
- component test: `./batect componentTest`
- prepush: `./batect prepush`
- bash with service dependencies: `./batect bash`

## Postgres commands
- Login: `psql -d postgres -U postgres`
- Show databases: `\l`
- Change database: `\c database`
- List tables: `\dt`