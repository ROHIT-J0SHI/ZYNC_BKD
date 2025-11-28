# InternFlow API Documentation

## Base URL
```
http://localhost:1234/api
```

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## API Testing Sequence

Follow this sequence to test all APIs in the correct order:

### **Phase 1: Setup & Health Check**

#### 1.1 Health Check
**GET** `http://localhost:1234/api/health`

**Headers:** None

**Expected Response:**
```json
{
  "status": "UP",
  "service": "InternFlow HRMS",
  "timestamp": "2025-01-15T10:30:00"
}
```

#### 1.2 Hello Endpoint
**GET** `http://localhost:1234/api/hello`

**Headers:** None

**Expected Response:**
```json
{
  "message": "Hello from InternFlow HRMS API!",
  "status": "success",
  "timestamp": "2025-01-15T10:30:00",
  "application": "InternFlow - HRMS for interns",
  "version": "1.0.0"
}
```

---

### **Phase 2: Authentication**

#### 2.1 Login as HR
**POST** `http://localhost:1234/api/auth/login/hr`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "hr@internflow.com",
  "password": "hr123456"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "hr@internflow.com",
  "name": "HR Manager",
  "role": "HR",
  "userId": 1
}
```

**Save the token for subsequent requests as `HR_TOKEN`**

**Note:** Default HR credentials are created automatically on application startup:
- Email: `hr@internflow.com`
- Password: `hr123456`

---

### **Phase 3: Intern Onboarding (HR Only)**

#### 3.1 Onboard Intern 1
**POST** `http://localhost:1234/api/interns/onboard`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "password123",
  "name": "John Doe",
  "managerEmail": "manager@example.com",
  "joiningDate": "2024-11-01",
  "internshipDurationMonths": 6,
  "stipendAmount": 15000.0
}
```

**Expected Response:**
```
Intern onboarded successfully. Intern can now login and fill their details.
```

**Important Notes:**
- Only HR can onboard interns (requires HR JWT token)
- Required fields: email, password, name, joiningDate, internshipDurationMonths, stipendAmount
- Optional fields: managerEmail
- Intern will fill their own personal details (PAN, Aadhaar, bank details, address, etc.) after logging in
- Stipend type is automatically set to MONTHLY

#### 3.2 Onboard Intern 2
**POST** `http://localhost:1234/api/interns/onboard`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "jane.smith@example.com",
  "password": "password123",
  "name": "Jane Smith",
  "joiningDate": "2024-11-01",
  "internshipDurationMonths": 6,
  "stipendAmount": 15000.0
}
```

**Expected Response:**
```
Intern onboarded successfully. Intern can now login and fill their details.
```

---

### **Phase 4: Intern Authentication**

#### 4.1 Login as Intern 1
**POST** `http://localhost:1234/api/auth/login/intern`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john.doe@example.com",
  "name": "John Doe",
  "role": "INTERN",
  "userId": 2
}
```

**Save the token for subsequent requests as `INTERN1_TOKEN`**

#### 4.2 Login as Intern 2
**POST** `http://localhost:1234/api/auth/login/intern`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "jane.smith@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "jane.smith@example.com",
  "name": "Jane Smith",
  "role": "INTERN",
  "userId": 3
}
```

**Save the token for subsequent requests as `INTERN2_TOKEN`**

---

### **Phase 5: Intern Details Management**

#### 5.1 Get My Details (Intern 1)
**GET** `http://localhost:1234/api/interns/my-details`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:**
```json
{
  "id": 1,
  "user": {
    "id": 2,
    "email": "john.doe@example.com",
    "name": "John Doe",
    "role": "INTERN"
  },
  "joiningDate": "2024-11-01",
  "internshipDurationMonths": 6,
  "stipendType": "MONTHLY",
  "stipendAmount": 15000.0,
  "managerEmail": "manager@example.com",
  "panNumber": "ABCDE1234F",
  "aadhaarNumber": "123456789012",
  "bankAccountNumber": "1234567890",
  "bankIfscCode": "HDFC0001234",
  "bankName": "HDFC Bank",
  "bankBranch": "Mumbai Branch",
  "address": "123 Main Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "phoneNumber": "9876543210"
}
```

**Note:** Interns can view their complete details including joining date, duration, and stipend information set by HR.

#### 5.2 Update My Details (Intern 1)
**PUT** `http://localhost:1234/api/interns/my-details`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "panNumber": "ABCDE1234F",
  "aadhaarNumber": "123456789012",
  "bankAccountNumber": "1234567890",
  "bankIfscCode": "HDFC0001234",
  "bankName": "HDFC Bank",
  "bankBranch": "Mumbai Branch",
  "address": "123 Main Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "phoneNumber": "9876543210"
}
```

**Expected Response:**
```
Intern details updated successfully
```

**Note:** Interns can only update their personal details (PAN, Aadhaar, bank details, address, etc.). Joining date, internship duration, and stipend amount are set by HR during onboarding and cannot be changed by interns.

#### 5.3 HR Update Intern Details
**PUT** `http://localhost:1234/api/interns/{internId}`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Doe Updated",
  "managerEmail": "new.manager@example.com",
  "joiningDate": "2024-11-01",
  "internshipDurationMonths": 6,
  "stipendType": "MONTHLY",
  "stipendAmount": 18000.0,
  "panNumber": "ABCDE1234F",
  "aadhaarNumber": "123456789012",
  "bankAccountNumber": "1234567890",
  "bankIfscCode": "HDFC0001234",
  "bankName": "HDFC Bank",
  "bankBranch": "Mumbai Branch",
  "address": "123 Main Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "phoneNumber": "9876543210"
}
```

**Expected Response:**
```
Intern details updated successfully
```

**Note:** HR can update any intern's details. All fields are optional - only include fields you want to update.

---

### **Phase 6: Leave Management (Intern Actions)**

#### 6.1 Check Leave Balance (Intern 1)
**GET** `http://localhost:1234/api/leaves/balance`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:**
```json
{
  "paidLeavesUsed": 0,
  "paidLeavesRemaining": 2,
  "unpaidLeavesTotal": 0,
  "totalPaidLeavesEarned": 2
}
```

#### 6.2 Request Leave (Intern 1)
**POST** `http://localhost:1234/api/leaves/request`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "leaveDate": "2025-01-20",
  "reason": "Personal work"
}
```

**Expected Response:** Leave object with status PENDING

Example: 
```json
{
    "id": 1,
    "intern": {
        "id": 2,
        "user": {
            "id": 3,
            "email": "rohit.joshi@jynk.com",
            "password": "$2a$10$8dXEz4Kiw4CbBCSSJvZT4uob.AcstXdcE9XV7iiEjMGcizJvb8ete",
            "role": "INTERN",
            "name": "ROhit Joshi",
            "createdAt": "2025-11-27T21:53:52",
            "updatedAt": "2025-11-27T21:53:52"
        },
        "joiningDate": "2024-11-01",
        "internshipDurationMonths": 6,
        "stipendType": "MONTHLY",
        "stipendAmount": 15000.0,
        "managerEmail": "manager@example.com",
        "panNumber": "87287821234F",
        "aadhaarNumber": "12343936789012",
        "bankAccountNumber": "123438890",
        "bankIfscCode": "HDFC0001234",
        "bankName": "HDFC Bank",
        "bankBranch": "Mumbai Branch",
        "address": "123 Main Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "phoneNumber": "9876543210",
        "signatureFilePath": null,
        "createdAt": "2025-11-27T21:53:52",
        "updatedAt": "2025-11-27T21:56:45",
        "internshipEndDate": "2025-05-01"
    },
    "leaveDate": "2025-01-20",
    "reason": "Personal work",
    "status": "PENDING",
    "leaveType": "PAID",
    "approvedBy": null,
    "approvedAt": null,
    "createdAt": "2025-11-27T21:58:39.691304228",
    "updatedAt": "2025-11-27T21:58:39.691315362"
}
```

**Expected Response:** Leave object with status PENDING

#### 6.4 Get My Leaves (Intern 1)
**GET** `http://localhost:1234/api/leaves/my-leaves`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:** Array of leave objects

Ex Response


**Request Body:**
```json
[
    {
        "id": 1,
        "leaveDate": "2025-01-20",
        "reason": "Personal work",
        "status": "PENDING",
        "leaveType": "PAID",
        "approvedBy": null,
        "approvedAt": null,
        "createdAt": "2025-11-27T21:58:40"
    },
    {
        "id": 2,
        "leaveDate": "2025-01-22",
        "reason": "Personal work",
        "status": "PENDING",
        "leaveType": "PAID",
        "approvedBy": null,
        "approvedAt": null,
        "createdAt": "2025-11-27T22:01:23"
    }
]
```

#### 6.5 Request Leave (Intern 2)
**POST** `http://localhost:1234/api/leaves/request`

**Headers:**
```
Authorization: Bearer <INTERN2_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "leaveDate": "2025-01-22",
  "reason": "Medical appointment"
}
```

**Expected Response:** Leave object with status PENDING

---

### **Phase 7: Leave Management (HR Actions)**

#### 7.1 Get All Pending Leaves
**GET** `http://localhost:1234/api/leaves/pending`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Array of pending leave requests

```json
[
    {
        "id": 1,
        "leaveDate": "2025-01-20",
        "reason": "Personal work",
        "status": "PENDING",
        "leaveType": "PAID",
        "approvedBy": null,
        "approvedAt": null,
        "createdAt": "2025-11-27T21:58:40"
    },
    {
        "id": 2,
        "leaveDate": "2025-01-22",
        "reason": "Personal work",
        "status": "PENDING",
        "leaveType": "PAID",
        "approvedBy": null,
        "approvedAt": null,
        "createdAt": "2025-11-27T22:01:23"
    }
]
```

#### 7.2 Approve Leave
**PUT** `http://localhost:1234/api/leaves/1/approve?approvedBy=HR Manager`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Updated leave object with status APPROVED

```json
{
    "id": 1,
    "intern": {
        "id": 2,
        "user": {
            "id": 3,
            "email": "rohit.joshi@jynk.com",
            "password": "$2a$10$8dXEz4Kiw4CbBCSSJvZT4uob.AcstXdcE9XV7iiEjMGcizJvb8ete",
            "role": "INTERN",
            "name": "ROhit Joshi",
            "createdAt": "2025-11-27T21:53:52",
            "updatedAt": "2025-11-27T21:53:52"
        },
        "joiningDate": "2024-11-01",
        "internshipDurationMonths": 6,
        "stipendType": "MONTHLY",
        "stipendAmount": 15000.0,
        "managerEmail": "manager@example.com",
        "panNumber": "87287821234F",
        "aadhaarNumber": "12343936789012",
        "bankAccountNumber": "123438890",
        "bankIfscCode": "HDFC0001234",
        "bankName": "HDFC Bank",
        "bankBranch": "Mumbai Branch",
        "address": "123 Main Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "phoneNumber": "9876543210",
        "signatureFilePath": null,
        "createdAt": "2025-11-27T21:53:52",
        "updatedAt": "2025-11-27T21:56:45",
        "internshipEndDate": "2025-05-01"
    },
    "leaveDate": "2025-01-20",
    "reason": "Personal work",
    "status": "APPROVED",
    "leaveType": "PAID",
    "approvedBy": "HR Manager",
    "approvedAt": "2025-11-27T22:06:04.621069611",
    "createdAt": "2025-11-27T21:58:40",
    "updatedAt": "2025-11-27T22:06:04.621395952"
}
```

#### 7.3 Reject Leave
**PUT** `http://localhost:1234/api/leaves/2/reject`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Updated leave object with status REJECTED

```json
{
    "id": 1,
    "intern": {
        "id": 2,
        "user": {
            "id": 3,
            "email": "rohit.joshi@jynk.com",
            "password": "$2a$10$8dXEz4Kiw4CbBCSSJvZT4uob.AcstXdcE9XV7iiEjMGcizJvb8ete",
            "role": "INTERN",
            "name": "ROhit Joshi",
            "createdAt": "2025-11-27T21:53:52",
            "updatedAt": "2025-11-27T21:53:52"
        },
        "joiningDate": "2024-11-01",
        "internshipDurationMonths": 6,
        "stipendType": "MONTHLY",
        "stipendAmount": 15000.0,
        "managerEmail": "manager@example.com",
        "panNumber": "87287821234F",
        "aadhaarNumber": "12343936789012",
        "bankAccountNumber": "123438890",
        "bankIfscCode": "HDFC0001234",
        "bankName": "HDFC Bank",
        "bankBranch": "Mumbai Branch",
        "address": "123 Main Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "phoneNumber": "9876543210",
        "signatureFilePath": null,
        "createdAt": "2025-11-27T21:53:52",
        "updatedAt": "2025-11-27T21:56:45",
        "internshipEndDate": "2025-05-01"
    },
    "leaveDate": "2025-01-20",
    "reason": "Personal work",
    "status": "REJECTED",
    "leaveType": "PAID",
    "approvedBy": "HR Manager",
    "approvedAt": "2025-11-27T22:06:04.621069611",
    "createdAt": "2025-11-27T21:58:40",
    "updatedAt": "2025-11-27T22:06:04.621395952"
}
```

---

### **Phase 8: Invoice Management (Intern Actions)**

#### 8.1 Generate Invoice (Intern 1 - November 2024)
**POST** `http://localhost:1234/api/invoices/generate`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "month": 11,
  "year": 2024
}
```

**Expected Response:** Invoice object with calculated values

#### 8.2 Generate Invoice (Intern 1 - December 2024)
**POST** `http://localhost:1234/api/invoices/generate`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "month": 12,
  "year": 2024
}
```

**Expected Response:** Invoice object with calculated values

#### 8.3 Get My Invoices (Intern 1)
**GET** `http://localhost:1234/api/invoices/my-invoices`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:** Array of invoice objects
```json
{
    "id": 1,
    "intern": {
        "id": 2,
        "user": {
            "id": 3,
            "email": "rohit.joshi@jynk.com",
            "password": "$2a$10$8dXEz4Kiw4CbBCSSJvZT4uob.AcstXdcE9XV7iiEjMGcizJvb8ete",
            "role": "INTERN",
            "name": "ROhit Joshi",
            "createdAt": "2025-11-27T21:53:52",
            "updatedAt": "2025-11-27T21:53:52"
        },
        "joiningDate": "2024-11-01",
        "internshipDurationMonths": 6,
        "stipendType": "MONTHLY",
        "stipendAmount": 15000.0,
        "managerEmail": "manager@example.com",
        "panNumber": "87287821234F",
        "aadhaarNumber": "12343936789012",
        "bankAccountNumber": "123438890",
        "bankIfscCode": "HDFC0001234",
        "bankName": "HDFC Bank",
        "bankBranch": "Mumbai Branch",
        "address": "123 Main Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "phoneNumber": "9876543210",
        "signatureFilePath": null,
        "createdAt": "2025-11-27T21:53:52",
        "updatedAt": "2025-11-27T21:56:45",
        "internshipEndDate": "2025-05-01"
    },
    "invoiceNumber": "001",
    "invoiceDate": "2025-11-27",
    "billingPeriodFrom": "2024-11-01",
    "billingPeriodTill": "2024-11-30",
    "totalWorkingDays": 21,
    "paidLeaves": 0,
    "unpaidLeaves": 0,
    "stipendAmount": 15000.0,
    "status": "PENDING",
    "remarks": null,
    "createdAt": "2025-11-27T22:07:50.580704317",
    "updatedAt": "2025-11-27T22:07:50.580713624"
}
```

#### 8.4 Get Invoice HTML (Intern 1)
**GET** `http://localhost:1234/api/invoices/1/html`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:** HTML formatted invoice

#### 8.5 Get Invoice by ID
**GET** `http://localhost:1234/api/invoices/1`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:** Invoice object

---

### **Phase 9: Invoice Management (HR Actions)**

#### 9.1 Get All Invoices
**GET** `http://localhost:1234/api/invoices/all`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Array of all invoice objects
```json
[
    {
        "id": 1,
        "invoiceNumber": "001",
        "invoiceDate": "2025-11-27",
        "billingPeriodFrom": "2024-11-01",
        "billingPeriodTill": "2024-11-30",
        "totalWorkingDays": 21,
        "paidLeaves": 0,
        "unpaidLeaves": 0,
        "stipendAmount": 15000.0,
        "status": "PENDING",
        "remarks": null,
        "internName": "ROhit Joshi",
        "internEmail": "rohit.joshi@jynk.com"
    }
]
```

#### 9.2 Update Invoice Status to APPROVED
**PUT** `http://localhost:1234/api/invoices/1/status?status=APPROVED&remarks=Approved by HR`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Updated invoice object
```json
{
    "id": 1,
    "intern": {
        "id": 2,
        "user": {
            "id": 3,
            "email": "rohit.joshi@jynk.com",
            "password": "$2a$10$8dXEz4Kiw4CbBCSSJvZT4uob.AcstXdcE9XV7iiEjMGcizJvb8ete",
            "role": "INTERN",
            "name": "ROhit Joshi",
            "createdAt": "2025-11-27T21:53:52",
            "updatedAt": "2025-11-27T21:53:52"
        },
        "joiningDate": "2024-11-01",
        "internshipDurationMonths": 6,
        "stipendType": "MONTHLY",
        "stipendAmount": 15000.0,
        "managerEmail": "manager@example.com",
        "panNumber": "87287821234F",
        "aadhaarNumber": "12343936789012",
        "bankAccountNumber": "123438890",
        "bankIfscCode": "HDFC0001234",
        "bankName": "HDFC Bank",
        "bankBranch": "Mumbai Branch",
        "address": "123 Main Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "phoneNumber": "9876543210",
        "signatureFilePath": null,
        "createdAt": "2025-11-27T21:53:52",
        "updatedAt": "2025-11-27T21:56:45",
        "internshipEndDate": "2025-05-01"
    },
    "invoiceNumber": "001",
    "invoiceDate": "2025-11-27",
    "billingPeriodFrom": "2024-11-01",
    "billingPeriodTill": "2024-11-30",
    "totalWorkingDays": 21,
    "paidLeaves": 0,
    "unpaidLeaves": 0,
    "stipendAmount": 15000.0,
    "status": "APPROVED",
    "remarks": "Approved by HR",
    "createdAt": "2025-11-27T22:07:51",
    "updatedAt": "2025-11-27T22:11:48.762173459"
}
```

#### 9.3 Update Invoice Status to PAID
**PUT** `http://localhost:1234/api/invoices/1/status?status=PAID&remarks=Payment processed`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Updated invoice object
```json
{
    "id": 1,
    "intern": {
        "id": 2,
        "user": {
            "id": 3,
            "email": "rohit.joshi@jynk.com",
            "password": "$2a$10$8dXEz4Kiw4CbBCSSJvZT4uob.AcstXdcE9XV7iiEjMGcizJvb8ete",
            "role": "INTERN",
            "name": "ROhit Joshi",
            "createdAt": "2025-11-27T21:53:52",
            "updatedAt": "2025-11-27T21:53:52"
        },
        "joiningDate": "2024-11-01",
        "internshipDurationMonths": 6,
        "stipendType": "MONTHLY",
        "stipendAmount": 15000.0,
        "managerEmail": "manager@example.com",
        "panNumber": "87287821234F",
        "aadhaarNumber": "12343936789012",
        "bankAccountNumber": "123438890",
        "bankIfscCode": "HDFC0001234",
        "bankName": "HDFC Bank",
        "bankBranch": "Mumbai Branch",
        "address": "123 Main Street",
        "city": "Mumbai",
        "state": "Maharashtra",
        "pincode": "400001",
        "phoneNumber": "9876543210",
        "signatureFilePath": null,
        "createdAt": "2025-11-27T21:53:52",
        "updatedAt": "2025-11-27T21:56:45",
        "internshipEndDate": "2025-05-01"
    },
    "invoiceNumber": "001",
    "invoiceDate": "2025-11-27",
    "billingPeriodFrom": "2024-11-01",
    "billingPeriodTill": "2024-11-30",
    "totalWorkingDays": 21,
    "paidLeaves": 0,
    "unpaidLeaves": 0,
    "stipendAmount": 15000.0,
    "status": "Paid",
    "remarks": "Payment Processed",
    "createdAt": "2025-11-27T22:07:51",
    "updatedAt": "2025-11-27T22:11:48.762173459"
}
```

---

### **Phase 10: Announcement Management (HR Actions)**

#### 10.1 Create Announcement
**POST** `http://localhost:1234/api/announcements`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Office Holiday - Republic Day",
  "body": "The office will be closed on January 26, 2025 in observance of Republic Day. All employees are requested to plan accordingly.",
  "expiryDate": "2025-01-31"
}
```

**Expected Response:** Announcement object

#### 10.2 Create Another Announcement
**POST** `http://localhost:1234/api/announcements`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Monthly Team Meeting",
  "body": "Monthly team meeting scheduled for January 30, 2025 at 3:00 PM. All interns are required to attend.",
  "expiryDate": "2025-02-05"
}
```

**Expected Response:** Announcement object

#### 10.3 Get All Announcements (HR)
**GET** `http://localhost:1234/api/announcements/all`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Array of all announcement objects

#### 10.4 Get Active Announcements (Public)
**GET** `http://localhost:1234/api/announcements/active`

**Headers:** None

**Expected Response:** Array of active announcement objects

#### 10.5 Deactivate Announcement
**PUT** `http://localhost:1234/api/announcements/1/deactivate`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:**
```
Announcement deactivated
```

---

### **Phase 11: AI Features**

#### 11.1 Policy Buddy Question (Intern 1)
**POST** `http://localhost:1234/api/ai/policy-buddy`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "question": "How many paid leaves do I have left?"
}
```

**Expected Response:**
```json
{
  "answer": "Based on your internship details..."
}
```

#### 11.2 Policy Buddy Question (Intern 1)
**POST** `http://localhost:1234/api/ai/policy-buddy`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "question": "What is my stipend amount?"
}
```

**Expected Response:**
```json
{
  "answer": "Based on your internship details..."
}
```

#### 11.3 HR Monthly Summary
**GET** `http://localhost:1234/api/ai/hr-summary?month=1&year=2025`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Plain text summary

---

### **Phase 12: Additional Endpoints**

#### 12.1 Get All Interns
**GET** `http://localhost:1234/api/interns/all`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Array of intern detail objects

---

## Complete API Reference

### 1. Authentication Endpoints

#### POST `/api/auth/login/hr`
Login as HR and get JWT token.

**Request Body:**
```json
{
  "email": "hr@internflow.com",
  "password": "hr123456"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "hr@internflow.com",
  "name": "HR Manager",
  "role": "HR",
  "userId": 1
}
```

**Error Response (401):**
```
Invalid HR credentials
```

#### POST `/api/auth/login/intern`
Login as Intern and get JWT token.

**Request Body:**
```json
{
  "email": "intern@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "intern@example.com",
  "name": "John Doe",
  "role": "INTERN",
  "userId": 2
}
```

**Error Response (401):**
```
Invalid intern credentials
```

**Important Notes:**
- HR can only login via `/api/auth/login/hr`
- Interns can only login via `/api/auth/login/intern`
- Using wrong endpoint will return 401 error

---

### 2. Intern Onboarding

#### POST `/api/interns/onboard`
Register a new intern. **HR Only** - Requires HR JWT token.

**Headers:** 
```
Authorization: Bearer <hr-jwt-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "intern@example.com",
  "password": "password123",
  "name": "John Doe",
  "managerEmail": "manager@example.com",
  "joiningDate": "2024-11-01",
  "internshipDurationMonths": 6,
  "stipendAmount": 15000.0
}
```

**Success Response (200):**
```
Intern onboarded successfully. Intern can now login and fill their details.
```

**Error Responses:**
- `400` - Bad Request (e.g., "User with email already exists")
- `403` - Forbidden ("Only HR can onboard interns")
- `401` - Unauthorized (Invalid or missing JWT token)

**Validation Rules:**
- Email is required and must be unique
- Password is required (minimum 6 characters)
- Name is required
- Joining date is required
- Internship duration is required (must be 3 or 6 months)
- Stipend amount is required (must be positive)
- Manager email is optional

**Note:** HR sets joining date, internship duration, and stipend amount during onboarding. Stipend type is automatically set to MONTHLY. Interns will fill their own personal details (PAN, Aadhaar, bank details, address, etc.) after logging in using the `/api/interns/my-details` endpoint.

#### GET `/api/interns/my-details`
Get intern's own details. **Intern Only** - Requires Intern JWT token.

**Headers:** 
```
Authorization: Bearer <intern-jwt-token>
```

**Success Response (200):**
```json
{
  "id": 1,
  "user": {
    "id": 2,
    "email": "intern@example.com",
    "name": "John Doe",
    "role": "INTERN"
  },
  "joiningDate": "2024-11-01",
  "internshipDurationMonths": 6,
  "stipendType": "MONTHLY",
  "stipendAmount": 15000.0,
  "managerEmail": "manager@example.com",
  "panNumber": "ABCDE1234F",
  "aadhaarNumber": "123456789012",
  "bankAccountNumber": "1234567890",
  "bankIfscCode": "BANK0001234",
  "bankName": "Bank Name",
  "bankBranch": "Branch Name",
  "address": "123 Street",
  "city": "City",
  "state": "State",
  "pincode": "123456",
  "phoneNumber": "9876543210"
}
```

**Error Responses:**
- `400` - Bad Request ("Intern details not found")
- `401` - Unauthorized (Invalid or missing JWT token)

#### PUT `/api/interns/my-details`
Update intern's own personal details. **Intern Only** - Requires Intern JWT token.

**Headers:** 
```
Authorization: Bearer <intern-jwt-token>
Content-Type: application/json
```

**Request Body (all fields optional):**
```json
{
  "panNumber": "ABCDE1234F",
  "aadhaarNumber": "123456789012",
  "bankAccountNumber": "1234567890",
  "bankIfscCode": "BANK0001234",
  "bankName": "Bank Name",
  "bankBranch": "Branch Name",
  "address": "123 Street",
  "city": "City",
  "state": "State",
  "pincode": "123456",
  "phoneNumber": "9876543210"
}
```

**Success Response (200):**
```
Intern details updated successfully
```

**Error Responses:**
- `400` - Bad Request (e.g., "PAN number already exists", "Aadhaar number already exists", "Bank account number already exists")
- `401` - Unauthorized (Invalid or missing JWT token)

**Validation Rules:**
- PAN number must be unique across all interns
- Aadhaar number must be unique across all interns
- Bank account number must be unique across all interns (if provided)
- All fields are optional - only include fields you want to update
- **Note:** Interns cannot update joining date, internship duration, stipend amount, or stipend type. These are set by HR during onboarding.

#### PUT `/api/interns/{internId}`
Update intern details. **HR Only** - Requires HR JWT token.

**Headers:** 
```
Authorization: Bearer <hr-jwt-token>
Content-Type: application/json
```

**Request Body (all fields optional):**
```json
{
  "name": "John Doe Updated",
  "managerEmail": "manager@example.com",
  "joiningDate": "2025-05-01",
  "internshipDurationMonths": 6,
  "stipendType": "MONTHLY",
  "stipendAmount": 15000.0,
  "panNumber": "ABCDE1234F",
  "aadhaarNumber": "123456789012",
  "bankAccountNumber": "1234567890",
  "bankIfscCode": "BANK0001234",
  "bankName": "Bank Name",
  "bankBranch": "Branch Name",
  "address": "123 Street",
  "city": "City",
  "state": "State",
  "pincode": "123456",
  "phoneNumber": "9876543210"
}
```

**Success Response (200):**
```
Intern details updated successfully
```

**Error Responses:**
- `400` - Bad Request (e.g., "PAN number already exists", "Aadhaar number already exists", "Bank account number already exists", "Intern not found")
- `403` - Forbidden ("Only HR can update intern details")
- `401` - Unauthorized (Invalid or missing JWT token)

**Validation Rules:**
- PAN number must be unique across all interns
- Aadhaar number must be unique across all interns
- Bank account number must be unique across all interns (if provided)
- All fields are optional - only include fields you want to update

#### GET `/api/interns/all`
Get all interns (HR only).

**Headers:** `Authorization: Bearer <hr-jwt-token>`

---

### 3. Invoice Endpoints

#### POST `/api/invoices/generate`
Generate an invoice for the logged-in intern.

**Headers:** `Authorization: Bearer <intern-jwt-token>`

**Request Body:**
```json
{
  "month": 5,
  "year": 2025
}
```

**Response:** Invoice object with calculated values.

#### GET `/api/invoices/my-invoices`
Get all invoices for the logged-in intern.

**Headers:** `Authorization: Bearer <intern-jwt-token>`

#### GET `/api/invoices/all`
Get all invoices (HR only).

**Headers:** `Authorization: Bearer <hr-jwt-token>`

#### GET `/api/invoices/{invoiceId}`
Get invoice by ID.

**Headers:** `Authorization: Bearer <jwt-token>`

#### GET `/api/invoices/{invoiceId}/html`
Get invoice as HTML.

**Headers:** `Authorization: Bearer <jwt-token>`

#### PUT `/api/invoices/{invoiceId}/status`
Update invoice status (HR only).

**Query Parameters:**
- `status`: PENDING, APPROVED, or PAID
- `remarks`: (optional) Remarks

**Headers:** `Authorization: Bearer <hr-jwt-token>`

---

### 4. Leave Endpoints

#### POST `/api/leaves/request`
Request a leave (logged-in intern).

**Headers:** `Authorization: Bearer <intern-jwt-token>`

**Request Body:**
```json
{
  "leaveDate": "2025-06-15",
  "reason": "Personal work"
}
```

#### GET `/api/leaves/my-leaves`
Get all leaves for the logged-in intern.

**Headers:** `Authorization: Bearer <intern-jwt-token>`

#### GET `/api/leaves/balance`
Get leave balance for the logged-in intern.

**Headers:** `Authorization: Bearer <intern-jwt-token>`

**Response:**
```json
{
  "paidLeavesUsed": 2,
  "paidLeavesRemaining": 4,
  "unpaidLeavesTotal": 1,
  "totalPaidLeavesEarned": 6
}
```

#### GET `/api/leaves/pending`
Get all pending leave requests (HR only).

**Headers:** `Authorization: Bearer <hr-jwt-token>`

#### PUT `/api/leaves/{leaveId}/approve`
Approve a leave request (HR only).

**Query Parameters:**
- `approvedBy`: Name of approver

**Headers:** `Authorization: Bearer <hr-jwt-token>`

#### PUT `/api/leaves/{leaveId}/reject`
Reject a leave request (HR only).

**Headers:** `Authorization: Bearer <hr-jwt-token>`

---

### 5. Announcement Endpoints

#### POST `/api/announcements`
Create an announcement (HR only).

**Headers:** `Authorization: Bearer <hr-jwt-token>`

**Request Body:**
```json
{
  "title": "Office Holiday",
  "body": "Office will be closed on...",
  "expiryDate": "2025-12-31"
}
```

#### GET `/api/announcements/active`
Get all active announcements (public).

#### GET `/api/announcements/all`
Get all announcements (HR only).

**Headers:** `Authorization: Bearer <hr-jwt-token>`

#### PUT `/api/announcements/{announcementId}/deactivate`
Deactivate an announcement (HR only).

**Headers:** `Authorization: Bearer <hr-jwt-token>`

---

### 6. AI Endpoints

#### POST `/api/ai/policy-buddy`
Ask policy questions (logged-in intern).

**Headers:** `Authorization: Bearer <intern-jwt-token>`

**Request Body:**
```json
{
  "question": "How many paid leaves do I have left?"
}
```

**Response:**
```json
{
  "answer": "Based on your internship details..."
}
```

#### GET `/api/ai/hr-summary`
Get AI-generated monthly summary (HR only).

**Query Parameters:**
- `month`: 1-12
- `year`: e.g., 2025

**Headers:** `Authorization: Bearer <hr-jwt-token>`

**Response:** Plain text summary.

---

### 7. Test Endpoints

#### GET `/api/hello`
Test endpoint to verify API is running.

#### GET `/api/health`
Health check endpoint.

---

## Error Responses

All endpoints return standard error format:

```json
{
  "error": "Error message here"
}
```

**Status Codes:**
- `200` - Success
- `400` - Bad Request (validation errors, duplicate values, etc.)
- `401` - Unauthorized (invalid credentials, missing/invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `500` - Internal Server Error

---

## Notes

1. **JWT Token Expiration:** Tokens expire after 24 hours (configurable in `application.properties`).

2. **Invoice Number Format:** Generated as 001, 002, 003... based on months since joining.

3. **Leave Policy:** 
   - 1 paid leave per month
   - Can carry forward
   - System automatically determines PAID vs UNPAID based on balance

4. **Stipend Calculation:**
   - Monthly stipend: Calculates per-day rate (monthly stipend / total working days in month)
   - Deducts unpaid leaves: Amount deducted = (unpaid leaves × per-day rate)
   - Final amount = Monthly stipend - (unpaid leaves × per-day rate)
   - Only approved unpaid leaves are considered for deduction
   - Paid leaves do not affect the stipend amount

5. **Working Days:** Calculated excluding weekends (Saturday & Sunday).

6. **Default HR Credentials:**
   - Email: `hr@internflow.com`
   - Password: `hr123456`
   - Created automatically on application startup

7. **Unique Validations:**
   - PAN number must be unique
   - Aadhaar number must be unique
   - Bank account number must be unique (if provided)
   - Email must be unique

8. **Role-Based Access:**
   - Intern onboarding: HR only
   - Separate login endpoints for HR and Intern
   - HR cannot login via intern endpoint and vice versa

9. **Intern Onboarding Workflow:**
   - HR creates intern with: email, password, name, joining date, internship duration, stipend amount, and optional manager email
   - Stipend type is automatically set to MONTHLY
   - Intern logs in and fills their own personal details (PAN, Aadhaar, bank details, address, etc.)
   - Interns can view their complete details including HR-set fields (joining date, duration, stipend) via GET `/api/interns/my-details`
   - Interns can only update personal details (PAN, Aadhaar, bank, address) via PUT `/api/interns/my-details`
   - HR can edit any intern's details at any time via PUT `/api/interns/{internId}`
   - All personal details (PAN, Aadhaar, bank account) must be unique across all interns

---

## Example Usage with cURL

### Login as HR
```bash
curl -X POST http://localhost:1234/api/auth/login/hr \
  -H "Content-Type: application/json" \
  -d '{"email":"hr@internflow.com","password":"hr123456"}'
```

### Login as Intern
```bash
curl -X POST http://localhost:1234/api/auth/login/intern \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","password":"password123"}'
```

### Onboard Intern (HR only)
```bash
curl -X POST http://localhost:1234/api/interns/onboard \
  -H "Authorization: Bearer YOUR_HR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "new.intern@example.com",
    "password": "password123",
    "name": "New Intern",
    "managerEmail": "manager@example.com",
    "joiningDate": "2024-11-01",
    "internshipDurationMonths": 6,
    "stipendAmount": 15000.0
  }'
```

### Get My Details (Intern)
```bash
curl -X GET http://localhost:1234/api/interns/my-details \
  -H "Authorization: Bearer YOUR_INTERN_TOKEN_HERE"
```

### Update Intern Details (Intern)
```bash
curl -X PUT http://localhost:1234/api/interns/my-details \
  -H "Authorization: Bearer YOUR_INTERN_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "panNumber": "NEWPA1234N",
    "aadhaarNumber": "111122223333",
    "bankAccountNumber": "1111222233",
    "bankIfscCode": "BANK0001111",
    "bankName": "Bank Name",
    "bankBranch": "Branch Name",
    "address": "123 Street",
    "city": "City",
    "state": "State",
    "pincode": "123456",
    "phoneNumber": "9876543210"
  }'
```

### Update Intern Details (HR)
```bash
curl -X PUT http://localhost:1234/api/interns/1 \
  -H "Authorization: Bearer YOUR_HR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "managerEmail": "new.manager@example.com",
    "stipendAmount": 18000.0
  }'
```

### Generate Invoice (with token)
```bash
curl -X POST http://localhost:1234/api/invoices/generate \
  -H "Authorization: Bearer YOUR_INTERN_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"month":11,"year":2024}'
```

### Get Leave Balance
```bash
curl -X GET http://localhost:1234/api/leaves/balance \
  -H "Authorization: Bearer YOUR_INTERN_TOKEN_HERE"
```

### Request Leave
```bash
curl -X POST http://localhost:1234/api/leaves/request \
  -H "Authorization: Bearer YOUR_INTERN_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"leaveDate":"2025-01-20","reason":"Personal work"}'
```

---

### 8. Separation / Internship Exit Endpoints

#### POST `/api/separations/request`
Create a separation request (request to leave internship early). **Intern Only** - Requires Intern JWT token.

**Headers:**
```
Authorization: Bearer <intern-jwt-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "requestedSeparationDate": "2025-03-15",
  "reason": "Found a better opportunity that aligns with my career goals"
}
```

**Success Response (200):**
```json
{
  "id": 1,
  "internId": 2,
  "internName": "John Doe",
  "internEmail": "john.doe@example.com",
  "requestedSeparationDate": "2025-03-15",
  "reason": "Found a better opportunity that aligns with my career goals",
  "status": "PENDING",
  "approvedBy": null,
  "approvedAt": null,
  "hrRemarks": null,
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

**Error Responses:**
- `400` - Bad Request (e.g., "A pending separation request already exists", "Requested separation date cannot be in the past", "Requested separation date cannot be before joining date", "Requested separation date cannot be after internship end date")
- `403` - Forbidden ("Only interns can create separation requests")
- `401` - Unauthorized (Invalid or missing JWT token)

**Validation Rules:**
- `requestedSeparationDate` is required and must be:
  - On or after today's date
  - On or after the intern's joining date
  - On or before the internship end date (if applicable)
- `reason` is required and must be between 10 and 1000 characters
- An intern cannot have multiple PENDING separation requests at the same time

**Note:** When a separation request is created, it starts with status PENDING and awaits HR approval.

---

#### GET `/api/separations/my-requests`
Get all separation requests for the logged-in intern. **Intern Only** - Requires Intern JWT token.

**Headers:**
```
Authorization: Bearer <intern-jwt-token>
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "internId": 2,
    "internName": "John Doe",
    "internEmail": "john.doe@example.com",
    "requestedSeparationDate": "2025-03-15",
    "reason": "Found a better opportunity that aligns with my career goals",
    "status": "PENDING",
    "approvedBy": null,
    "approvedAt": null,
    "hrRemarks": null,
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  },
  {
    "id": 2,
    "internId": 2,
    "internName": "John Doe",
    "internEmail": "john.doe@example.com",
    "requestedSeparationDate": "2025-02-10",
    "reason": "Personal reasons",
    "status": "APPROVED",
    "approvedBy": "HR Manager",
    "approvedAt": "2025-01-16T14:20:00",
    "hrRemarks": "Approved as per policy",
    "createdAt": "2025-01-10T09:15:00",
    "updatedAt": "2025-01-16T14:20:00"
  }
]
```

**Error Responses:**
- `403` - Forbidden ("Only interns can view their separation requests")
- `401` - Unauthorized (Invalid or missing JWT token)

---

#### GET `/api/separations/all`
Get all separation requests. **HR Only** - Requires HR JWT token.

**Headers:**
```
Authorization: Bearer <hr-jwt-token>
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "internId": 2,
    "internName": "John Doe",
    "internEmail": "john.doe@example.com",
    "requestedSeparationDate": "2025-03-15",
    "reason": "Found a better opportunity that aligns with my career goals",
    "status": "PENDING",
    "approvedBy": null,
    "approvedAt": null,
    "hrRemarks": null,
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  },
  {
    "id": 2,
    "internId": 3,
    "internName": "Jane Smith",
    "internEmail": "jane.smith@example.com",
    "requestedSeparationDate": "2025-02-20",
    "reason": "Personal family commitments",
    "status": "APPROVED",
    "approvedBy": "HR Manager",
    "approvedAt": "2025-01-16T14:20:00",
    "hrRemarks": "Approved",
    "createdAt": "2025-01-12T11:00:00",
    "updatedAt": "2025-01-16T14:20:00"
  }
]
```

**Error Responses:**
- `403` - Forbidden ("Only HR can view all separation requests")
- `401` - Unauthorized (Invalid or missing JWT token)

---

#### GET `/api/separations/pending`
Get all pending separation requests. **HR Only** - Requires HR JWT token.

**Headers:**
```
Authorization: Bearer <hr-jwt-token>
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "internId": 2,
    "internName": "John Doe",
    "internEmail": "john.doe@example.com",
    "requestedSeparationDate": "2025-03-15",
    "reason": "Found a better opportunity that aligns with my career goals",
    "status": "PENDING",
    "approvedBy": null,
    "approvedAt": null,
    "hrRemarks": null,
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  }
]
```

**Error Responses:**
- `403` - Forbidden ("Only HR can view pending separation requests")
- `401` - Unauthorized (Invalid or missing JWT token)

---

#### PUT `/api/separations/{requestId}/approve`
Approve a separation request. **HR Only** - Requires HR JWT token.

**Headers:**
```
Authorization: Bearer <hr-jwt-token>
```

**Query Parameters:**
- `approvedBy`: (required) Name of the HR who is approving the request

**Example:**
```
PUT /api/separations/1/approve?approvedBy=HR Manager
```

**Success Response (200):**
```json
{
  "id": 1,
  "internId": 2,
  "internName": "John Doe",
  "internEmail": "john.doe@example.com",
  "requestedSeparationDate": "2025-03-15",
  "reason": "Found a better opportunity that aligns with my career goals",
  "status": "APPROVED",
  "approvedBy": "HR Manager",
  "approvedAt": "2025-01-16T14:20:00",
  "hrRemarks": null,
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-16T14:20:00"
}
```

**Error Responses:**
- `400` - Bad Request (e.g., "Separation request not found", "Separation request has already been processed")
- `403` - Forbidden ("Only HR can approve separation requests")
- `401` - Unauthorized (Invalid or missing JWT token)

**Important Notes:**
- When a separation request is approved:
  - The intern's `active` status is set to `false`
  - The intern's internship duration is updated to reflect the requested separation date
  - The request status is changed to APPROVED
  - Once approved, the request cannot be changed again (idempotent operation)

---

#### PUT `/api/separations/{requestId}/reject`
Reject a separation request. **HR Only** - Requires HR JWT token.

**Headers:**
```
Authorization: Bearer <hr-jwt-token>
```

**Query Parameters:**
- `approvedBy`: (optional) Name of the HR who is rejecting the request. If not provided, it will use the HR's name from the JWT token.
- `hrRemarks`: (optional) Comments or remarks from HR about the rejection

**Example:**
```
PUT /api/separations/1/reject?approvedBy=HR Manager&hrRemarks=Request does not meet company policy
```

**Success Response (200):**
```json
{
  "id": 1,
  "internId": 2,
  "internName": "John Doe",
  "internEmail": "john.doe@example.com",
  "requestedSeparationDate": "2025-03-15",
  "reason": "Found a better opportunity that aligns with my career goals",
  "status": "REJECTED",
  "approvedBy": "HR Manager",
  "approvedAt": "2025-01-16T14:20:00",
  "hrRemarks": "Request does not meet company policy",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-16T14:20:00"
}
```

**Error Responses:**
- `400` - Bad Request (e.g., "Separation request not found", "Separation request has already been processed")
- `403` - Forbidden ("Only HR can reject separation requests")
- `401` - Unauthorized (Invalid or missing JWT token)

**Important Notes:**
- When a separation request is rejected:
  - The request status is changed to REJECTED
  - The intern remains active (no changes to intern status)
  - Once rejected, the request cannot be changed again (idempotent operation)
  - HR can optionally provide remarks explaining the rejection

---

### Separation Request Status Values

- **PENDING**: Request has been submitted and is awaiting HR review
- **APPROVED**: Request has been approved by HR. Intern is marked as inactive and internship end date is updated.
- **REJECTED**: Request has been rejected by HR. Intern remains active.

---

**Happy Coding! 🚀**
