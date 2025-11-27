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
  "managerEmail": "manager@example.com"
}
```

**Expected Response:**
```
Intern onboarded successfully. Intern can now login and fill their details.
```

**Important Notes:**
- Only HR can onboard interns (requires HR JWT token)
- Only email, password, name, and optional manager email are required
- Intern will fill their own details (PAN, Aadhaar, bank details, etc.) after logging in
- Manager email is optional

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
  "name": "Jane Smith"
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

#### 5.1 Update My Details (Intern 1)
**PUT** `http://localhost:1234/api/interns/my-details`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "joiningDate": "2024-11-01",
  "internshipDurationMonths": 6,
  "stipendType": "MONTHLY",
  "stipendAmount": 15000.0,
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

**Note:** Interns can update their own details. All fields are optional - only include fields you want to update.

#### 5.2 HR Update Intern Details
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

#### 5.1 Check Leave Balance (Intern 1)
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

#### 5.2 Request Leave (Intern 1)
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

#### 5.3 Request Another Leave (Intern 1)
**POST** `http://localhost:1234/api/leaves/request`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "leaveDate": "2025-01-25",
  "reason": "Family function"
}
```

**Expected Response:** Leave object with status PENDING

#### 5.4 Get My Leaves (Intern 1)
**GET** `http://localhost:1234/api/leaves/my-leaves`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:** Array of leave objects

#### 5.5 Request Leave (Intern 2)
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

#### 6.1 Get All Pending Leaves
**GET** `http://localhost:1234/api/leaves/pending`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Array of pending leave requests

#### 6.2 Approve Leave
**PUT** `http://localhost:1234/api/leaves/1/approve?approvedBy=HR Manager`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Updated leave object with status APPROVED

#### 6.3 Reject Leave
**PUT** `http://localhost:1234/api/leaves/2/reject`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Updated leave object with status REJECTED

---

### **Phase 8: Invoice Management (Intern Actions)**

#### 7.1 Generate Invoice (Intern 1 - November 2024)
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

#### 7.2 Generate Invoice (Intern 1 - December 2024)
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

#### 7.3 Get My Invoices (Intern 1)
**GET** `http://localhost:1234/api/invoices/my-invoices`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:** Array of invoice objects

#### 7.4 Get Invoice HTML (Intern 1)
**GET** `http://localhost:1234/api/invoices/1/html`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:** HTML formatted invoice

#### 7.5 Get Invoice by ID
**GET** `http://localhost:1234/api/invoices/1`

**Headers:**
```
Authorization: Bearer <INTERN1_TOKEN>
```

**Expected Response:** Invoice object

---

### **Phase 9: Invoice Management (HR Actions)**

#### 8.1 Get All Invoices
**GET** `http://localhost:1234/api/invoices/all`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Array of all invoice objects

#### 8.2 Update Invoice Status to APPROVED
**PUT** `http://localhost:1234/api/invoices/1/status?status=APPROVED&remarks=Approved by HR`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Updated invoice object

#### 8.3 Update Invoice Status to PAID
**PUT** `http://localhost:1234/api/invoices/1/status?status=PAID&remarks=Payment processed`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Updated invoice object

---

### **Phase 10: Announcement Management (HR Actions)**

#### 9.1 Create Announcement
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

#### 9.2 Create Another Announcement
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

#### 9.3 Get All Announcements (HR)
**GET** `http://localhost:1234/api/announcements/all`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Array of all announcement objects

#### 9.4 Get Active Announcements (Public)
**GET** `http://localhost:1234/api/announcements/active`

**Headers:** None

**Expected Response:** Array of active announcement objects

#### 9.5 Deactivate Announcement
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

#### 10.1 Policy Buddy Question (Intern 1)
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

#### 10.2 Policy Buddy Question (Intern 1)
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

#### 10.3 HR Monthly Summary
**GET** `http://localhost:1234/api/ai/hr-summary?month=1&year=2025`

**Headers:**
```
Authorization: Bearer <HR_TOKEN>
```

**Expected Response:** Plain text summary

---

### **Phase 12: Additional Endpoints**

#### 11.1 Get All Interns
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
  "managerEmail": "manager@example.com"
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
- Manager email is optional

**Note:** Interns will fill their own details (PAN, Aadhaar, bank details, joining date, stipend, etc.) after logging in using the `/api/interns/my-details` endpoint.

#### PUT `/api/interns/my-details`
Update intern's own details. **Intern Only** - Requires Intern JWT token.

**Headers:** 
```
Authorization: Bearer <intern-jwt-token>
Content-Type: application/json
```

**Request Body (all fields optional):**
```json
{
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
- `400` - Bad Request (e.g., "PAN number already exists", "Aadhaar number already exists", "Bank account number already exists")
- `401` - Unauthorized (Invalid or missing JWT token)

**Validation Rules:**
- PAN number must be unique across all interns
- Aadhaar number must be unique across all interns
- Bank account number must be unique across all interns (if provided)
- All fields are optional - only include fields you want to update

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
   - Monthly: Deducts only unpaid leaves proportionally
   - Daily: Deducts unpaid leave days from total payable days

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
   - HR creates intern with minimal info: email, password, name, optional manager email
   - Intern logs in and fills their own details (PAN, Aadhaar, bank details, joining date, stipend, etc.)
   - HR can edit any intern's details at any time
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
    "managerEmail": "manager@example.com"
  }'
```

### Update Intern Details (Intern)
```bash
curl -X PUT http://localhost:1234/api/interns/my-details \
  -H "Authorization: Bearer YOUR_INTERN_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "joiningDate": "2025-01-01",
    "internshipDurationMonths": 6,
    "stipendType": "MONTHLY",
    "stipendAmount": 15000.0,
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

**Happy Coding! 🚀**
