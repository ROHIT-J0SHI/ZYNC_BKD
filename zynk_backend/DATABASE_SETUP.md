# Database Setup Guide for Team

## Prerequisites

1. **Install MySQL** (if not already installed)
   - Windows: Download from https://dev.mysql.com/downloads/mysql/
   - Linux: `sudo apt-get install mysql-server` or `sudo yum install mysql-server`
   - Mac: `brew install mysql` or download from MySQL website

2. **Start MySQL Service**
   - Windows: Start MySQL service from Services
   - Linux: `sudo systemctl start mysql`
   - Mac: `brew services start mysql`

## Setup Steps

### Step 1: Create Database

Open MySQL command line or MySQL Workbench and run:

```sql
CREATE DATABASE IF NOT EXISTS internflow;
```

Or use the schema.sql file:

```bash
mysql -u root -p < schema.sql
```

### Step 2: Update Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/internflow?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**Important:** Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password.

### Step 3: Run Schema (Optional)

If you want to manually create tables, run:

```bash
mysql -u root -p internflow < schema.sql
```

Or use MySQL Workbench:
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Open `schema.sql` file
4. Execute the script

### Step 4: Verify Tables

Check if tables are created:

```sql
USE internflow;
SHOW TABLES;
```

You should see:
- users
- intern_details
- invoices
- leaves
- announcements

## Configuration for Each Team Member

Each team member should:

1. **Install MySQL** on their local machine
2. **Create database:**
   ```sql
   CREATE DATABASE internflow;
   ```
3. **Update `application.properties`:**
   - Set their MySQL username (usually `root`)
   - Set their MySQL password
   - Keep database name as `internflow`
4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

Hibernate will automatically create/update tables on first run.

## Default MySQL Credentials

If you're using default MySQL installation:
- **Username:** `root`
- **Password:** (the one you set during MySQL installation)

## Troubleshooting

### Connection Error
```
Communications link failure
```
**Solution:** Make sure MySQL service is running.

### Access Denied
```
Access denied for user 'root'@'localhost'
```
**Solution:** Check username and password in `application.properties`.

### Database Doesn't Exist
```
Unknown database 'internflow'
```
**Solution:** Run `CREATE DATABASE internflow;` or use `schema.sql`.

### Port Already in Use
```
Port 3306 is already in use
```
**Solution:** Check if MySQL is already running or change port in connection URL.

## Quick Setup Script

For Linux/Mac users, you can create a setup script:

```bash
#!/bin/bash
# setup-db.sh

echo "Creating database..."
mysql -u root -p <<EOF
CREATE DATABASE IF NOT EXISTS internflow;
USE internflow;
SOURCE schema.sql;
EOF

echo "Database setup complete!"
```

Make it executable:
```bash
chmod +x setup-db.sh
./setup-db.sh
```

## Team Checklist

- [ ] MySQL installed
- [ ] MySQL service running
- [ ] Database `internflow` created
- [ ] Updated `application.properties` with correct credentials
- [ ] Application starts successfully
- [ ] Can connect to database

---

**Your database is now persistent! Data will be saved even after application restart.** 🎉

