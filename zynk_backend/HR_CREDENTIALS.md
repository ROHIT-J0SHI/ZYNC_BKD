# HR Login Credentials

This document contains the hardcoded HR user credentials for accessing the InternFlow HRMS dashboard.

---

## Default HR Users

### HR User 1
- **Email**: `hr1@internflow.com`
- **Password**: `hr123456`
- **Name**: HR Manager 1
- **Role**: HR

### HR User 2
- **Email**: `hr2@internflow.com`
- **Password**: `hr123456`
- **Name**: HR Manager 2
- **Role**: HR

---

## How to Login

### Using cURL
```bash
curl -X POST http://localhost:1234/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "hr1@internflow.com",
    "password": "hr123456"
  }'
```

### Using Postman
1. **Method**: POST
2. **URL**: `http://localhost:1234/api/auth/login`
3. **Headers**: 
   - `Content-Type: application/json`
4. **Body** (raw JSON):
```json
{
  "email": "hr1@internflow.com",
  "password": "hr123456"
}
```

### Response Example
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "hr1@internflow.com",
  "name": "HR Manager 1",
  "role": "HR",
  "userId": 1
}
```

---

## Using the Token

After login, use the token in subsequent API calls:

```bash
# Example: Get all invoices (HR only)
curl -X GET http://localhost:1234/api/invoices/all \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## HR Dashboard Features

Once logged in as HR, you can:

1. **View All Invoices**
   - `GET /api/invoices/all`
   - View all intern invoices

2. **Update Invoice Status**
   - `PUT /api/invoices/{invoiceId}/status?status=APPROVED&remarks=Approved`
   - Change invoice status (PENDING → APPROVED → PAID)

3. **View Pending Leaves**
   - `GET /api/leaves/pending`
   - See all pending leave requests

4. **Approve/Reject Leaves**
   - `PUT /api/leaves/{leaveId}/approve?approvedBy=HR Manager 1`
   - `PUT /api/leaves/{leaveId}/reject`

5. **Create Announcements**
   - `POST /api/announcements`
   - Post announcements visible to interns

6. **View All Interns**
   - `GET /api/interns/all`
   - View all intern profiles (HR only)

7. **Generate HR Monthly Summary**
   - `POST /api/ai/hr-summary`
   - Get AI-generated monthly summary

---

## Notes

- These users are **automatically created** when the application starts
- If users already exist, they will **not be recreated** (no duplicates)
- Passwords are **BCrypt encoded** for security
- You can change these credentials by modifying `DataInitializer.java`

---

## Security Reminder

⚠️ **Important**: These are default credentials for development/testing. 
- Change passwords in production
- Use environment variables for sensitive credentials
- Never commit real credentials to version control

