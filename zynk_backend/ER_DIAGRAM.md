# Entity Relationship (ER) Diagram - InternFlow HRMS

## Database Schema Overview

This document provides a visual representation of all entity relationships in the InternFlow HRMS database.

---

## ER Diagram (Text Format)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           INTERNFLOW DATABASE SCHEMA                        │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│       USERS          │
├──────────────────────┤
│ PK  id (BIGINT)      │
│      email (VARCHAR) │ ◄─── UNIQUE
│      password        │
│      name            │
│      role (ENUM)     │ ◄─── INTERN | HR
│      createdAt       │
│      updatedAt       │
└──────────┬───────────┘
           │
           │ 1:1 (One-to-One)
           │
           │ ┌──────────────────────────────────────────────┐
           │ │ Each User can have ONE InternDetails         │
           │ │ (only if role = INTERN)                      │
           │ └──────────────────────────────────────────────┘
           │
           ▼
┌──────────────────────┐
│   INTERN_DETAILS     │
├──────────────────────┤
│ PK  id (BIGINT)      │
│ FK  user_id          │ ◄─── REFERENCES users(id) - UNIQUE
│      joiningDate     │
│      durationMonths  │
│      stipendType     │ ◄─── MONTHLY | DAILY
│      stipendAmount   │
│      panNumber       │
│      aadhaarNumber   │
│      bankAccountNo   │
│      bankIFSC        │
│      bankName        │
│      bankBranch      │
│      address         │
│      city            │
│      state           │
│      pincode         │
│      phoneNumber     │
│      signaturePath   │
│      createdAt       │
│      updatedAt       │
└──────────┬───────────┘
           │
           │ 1:N (One-to-Many)
           │
           ├──────────────────────┐
           │                      │
           │                      │
           ▼                      ▼
┌──────────────────────┐  ┌──────────────────────┐
│      INVOICES        │  │       LEAVES          │
├──────────────────────┤  ├──────────────────────┤
│ PK  id (BIGINT)      │  │ PK  id (BIGINT)      │
│ FK  intern_id        │◄─┤ FK  intern_id        │◄─┐
│      invoiceNumber   │◄─┤      leaveDate       │  │
│      invoiceDate     │  │      reason          │  │
│      billingFrom     │  │      status (ENUM)   │◄─┤
│      billingTill     │  │      leaveType (ENUM)│◄─┤
│      workingDays     │  │      approvedBy      │  │
│      paidLeaves      │  │      approvedAt      │  │
│      unpaidLeaves    │  │      createdAt       │  │
│      stipendAmount   │  │      updatedAt       │  │
│      status (ENUM)   │◄─┤                      │  │
│      remarks         │  │ ENUM: status         │  │
│      createdAt       │  │   - PENDING          │  │
│      updatedAt       │  │   - APPROVED         │  │
│                      │  │   - REJECTED         │  │
│ ENUM: status         │  │                      │  │
│   - PENDING          │  │ ENUM: leaveType      │  │
│   - APPROVED         │  │   - PAID            │  │
│   - PAID             │  │   - UNPAID          │  │
└──────────────────────┘  └──────────────────────┘  │
                                                      │
                                                      │
┌──────────────────────┐                              │
│   ANNOUNCEMENTS      │                              │
├──────────────────────┤                              │
│ PK  id (BIGINT)      │                              │
│ FK  created_by       │◄─────────────────────────────┘
│      title           │
│      body (TEXT)     │
│      expiryDate      │
│      isActive        │
│      createdAt       │
│      updatedAt       │
│                      │
│ Note: created_by     │
│ references users(id) │
│ where role = HR      │
└──────────────────────┘
```

---

## Relationship Details

### 1. **User ↔ InternDetails** (One-to-One)
- **Cardinality**: 1:1
- **Relationship**: Each User can have at most ONE InternDetails record
- **Foreign Key**: `intern_details.user_id` → `users.id` (UNIQUE constraint)
- **Business Rule**: Only Users with `role = INTERN` will have an InternDetails record
- **JPA Mapping**: `@OneToOne` in InternDetails

```
User (1) ──────── (0..1) InternDetails
```

### 2. **InternDetails ↔ Invoice** (One-to-Many)
- **Cardinality**: 1:N
- **Relationship**: One InternDetails can have MANY Invoices
- **Foreign Key**: `invoices.intern_id` → `intern_details.id`
- **Business Rule**: Each invoice represents a monthly billing cycle for an intern
- **JPA Mapping**: `@ManyToOne` in Invoice

```
InternDetails (1) ──────── (N) Invoice
```

### 3. **InternDetails ↔ Leave** (One-to-Many)
- **Cardinality**: 1:N
- **Relationship**: One InternDetails can have MANY Leave records
- **Foreign Key**: `leaves.intern_id` → `intern_details.id`
- **Business Rule**: Each leave represents a single day off for an intern
- **JPA Mapping**: `@ManyToOne` in Leave

```
InternDetails (1) ──────── (N) Leave
``` 

### 4. **User ↔ Announcement** (One-to-Many)
- **Cardinality**: 1:N
- **Relationship**: One User (HR) can create MANY Announcements
- **Foreign Key**: `announcements.created_by` → `users.id`
- **Business Rule**: Only Users with `role = HR` can create announcements
- **JPA Mapping**: `@ManyToOne` in Announcement

```
User (1) ──────── (N) Announcement
```

---

## Entity Details

### 📋 **USERS** Table
**Purpose**: Stores all system users (Interns and HR personnel)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Primary key |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | User email (login) |
| `password` | VARCHAR(255) | NOT NULL | Encrypted password |
| `name` | VARCHAR(255) | NOT NULL | Full name |
| `role` | ENUM | NOT NULL | INTERN or HR |
| `createdAt` | DATETIME(6) | | Timestamp |
| `updatedAt` | DATETIME(6) | | Timestamp |

---

### 👤 **INTERN_DETAILS** Table
**Purpose**: Stores detailed onboarding information for interns

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Primary key |
| `user_id` | BIGINT | FK, UNIQUE, NOT NULL | References users.id |
| `joiningDate` | DATE | NOT NULL | Internship start date |
| `internshipDurationMonths` | INT | NOT NULL | 3 or 6 months |
| `stipendType` | ENUM | NOT NULL | MONTHLY or DAILY |
| `stipendAmount` | DOUBLE | NOT NULL | Stipend per period |
| `panNumber` | VARCHAR(255) | NOT NULL | PAN card number |
| `aadhaarNumber` | VARCHAR(255) | NOT NULL | Aadhaar number |
| `bankAccountNumber` | VARCHAR(255) | | Bank account |
| `bankIfscCode` | VARCHAR(255) | | IFSC code |
| `bankName` | VARCHAR(255) | | Bank name |
| `bankBranch` | VARCHAR(255) | | Branch name |
| `address` | VARCHAR(255) | | Street address |
| `city` | VARCHAR(255) | | City |
| `state` | VARCHAR(255) | | State |
| `pincode` | VARCHAR(255) | | PIN code |
| `phoneNumber` | VARCHAR(255) | | Phone number |
| `signatureFilePath` | VARCHAR(255) | | Path to signature file |
| `createdAt` | DATETIME(6) | | Timestamp |
| `updatedAt` | DATETIME(6) | | Timestamp |

---

### 💰 **INVOICES** Table
**Purpose**: Stores monthly invoices generated for interns

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Primary key |
| `intern_id` | BIGINT | FK, NOT NULL | References intern_details.id |
| `invoiceNumber` | VARCHAR(255) | UNIQUE, NOT NULL | Format: 001, 002, etc. |
| `invoiceDate` | DATE | NOT NULL | Invoice generation date |
| `billingPeriodFrom` | DATE | NOT NULL | Billing start date |
| `billingPeriodTill` | DATE | NOT NULL | Billing end date |
| `totalWorkingDays` | INT | NOT NULL | Working days in period |
| `paidLeaves` | INT | NOT NULL | Number of paid leaves |
| `unpaidLeaves` | INT | NOT NULL | Number of unpaid leaves |
| `stipendAmount` | DOUBLE | NOT NULL | Final calculated stipend |
| `status` | ENUM | NOT NULL | PENDING, APPROVED, PAID |
| `remarks` | VARCHAR(255) | | HR remarks |
| `createdAt` | DATETIME(6) | | Timestamp |
| `updatedAt` | DATETIME(6) | | Timestamp |

---

### 🏖️ **LEAVES** Table
**Purpose**: Stores leave requests and approvals

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Primary key |
| `intern_id` | BIGINT | FK, NOT NULL | References intern_details.id |
| `leaveDate` | DATE | NOT NULL | Date of leave |
| `reason` | VARCHAR(255) | NOT NULL | Leave reason |
| `status` | ENUM | NOT NULL | PENDING, APPROVED, REJECTED |
| `leaveType` | ENUM | NOT NULL | PAID or UNPAID (auto-determined) |
| `approvedBy` | VARCHAR(255) | | HR/Manager name |
| `approvedAt` | DATETIME(6) | | Approval timestamp |
| `createdAt` | DATETIME(6) | | Timestamp |
| `updatedAt` | DATETIME(6) | | Timestamp |

---

### 📢 **ANNOUNCEMENTS** Table
**Purpose**: Stores HR announcements visible to interns

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Primary key |
| `created_by` | BIGINT | FK, NOT NULL | References users.id (HR) |
| `title` | VARCHAR(255) | NOT NULL | Announcement title |
| `body` | TEXT | NOT NULL | Announcement content |
| `expiryDate` | DATE | NOT NULL | Expiry date |
| `isActive` | BOOLEAN | NOT NULL | Active status |
| `createdAt` | DATETIME(6) | | Timestamp |
| `updatedAt` | DATETIME(6) | | Timestamp |

---

## Relationship Summary Table

| From Entity | To Entity | Type | Foreign Key | Description |
|-------------|-----------|------|-------------|-------------|
| InternDetails | User | One-to-One | `user_id` | Each intern has one user account |
| Invoice | InternDetails | Many-to-One | `intern_id` | Each invoice belongs to one intern |
| Leave | InternDetails | Many-to-One | `intern_id` | Each leave belongs to one intern |
| Announcement | User | Many-to-One | `created_by` | Each announcement created by one HR user |

---

## Data Flow Example

### Example: Intern Onboarding Flow

```
1. User Registration
   └─> users table: Insert user with role = INTERN
   
2. Intern Onboarding
   └─> intern_details table: Insert details with user_id = users.id
   
3. Leave Request
   └─> leaves table: Insert leave with intern_id = intern_details.id
   
4. Invoice Generation
   └─> invoices table: Insert invoice with intern_id = intern_details.id
       └─> Calculates paid/unpaid leaves from leaves table
```

### Example: HR Announcement Flow

```
1. HR User Login
   └─> users table: User with role = HR
   
2. Create Announcement
   └─> announcements table: Insert with created_by = users.id (HR)
   
3. Display to Interns
   └─> Query announcements where isActive = true AND expiryDate >= today
```

---

## Key Constraints

1. **Unique Constraints**:
   - `users.email` - Unique email per user
   - `intern_details.user_id` - One intern details per user
   - `invoices.invoiceNumber` - Unique invoice number

2. **Foreign Key Constraints**:
   - `intern_details.user_id` → `users.id`
   - `invoices.intern_id` → `intern_details.id`
   - `leaves.intern_id` → `intern_details.id`
   - `announcements.created_by` → `users.id`

3. **Business Rules**:
   - Only INTERN role users have InternDetails
   - Only HR role users can create Announcements
   - Invoice numbers are sequential per intern (001, 002, ...)
   - Leave type (PAID/UNPAID) is auto-determined based on balance

---

## Visual Relationship Map

```
                    ┌─────────┐
                    │  USER   │
                    │ (Base)  │
                    └────┬────┘
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          │ 1:1          │ 1:N          │ 1:N
          │              │              │
          ▼              ▼              ▼
    ┌──────────┐  ┌──────────┐  ┌──────────────┐
    │ INTERN   │  │ INTERN   │  │ ANNOUNCEMENT │
    │ DETAILS  │  │ DETAILS  │  │              │
    └────┬─────┘  └────┬─────┘  └──────────────┘
         │            │
         │ 1:N        │ 1:N
         │            │
         ▼            ▼
    ┌─────────┐  ┌─────────┐
    │ INVOICE │  │  LEAVE  │
    └─────────┘  └─────────┘
```

---

## Notes

- All timestamps (`createdAt`, `updatedAt`) are automatically managed by JPA `@PrePersist` and `@PreUpdate`
- Enum types ensure data integrity at the database level
- Foreign key relationships ensure referential integrity
- Unique constraints prevent duplicate data (email, user_id, invoiceNumber)

