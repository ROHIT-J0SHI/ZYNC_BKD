# InternFlow API Testing Guide

## 🚀 Starting the Application

### Step 1: Start the Application

```bash
cd "/home/tanisha.sharma@ad.franconnect.com/Downloads/ZYNK PROJECT"
./mvnw spring-boot:run
```

Or build and run:
```bash
./mvnw clean install
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

**Expected Output:**
```
Started Application in X.XXX seconds
```

The application will start on: **http://localhost:1234**

---

## 🧪 Testing All APIs

### Prerequisites
- Application running on `http://localhost:1234`
- `curl` command available (or use Postman/Thunder Client)

---

## 📋 Test Scenarios

### 1. Health Check (No Auth Required)

```bash
curl http://localhost:1234/api/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "service": "InternFlow HRMS",
  "timestamp": "2025-11-22T13:50:00"
}
```

---

### 2. Intern Onboarding (No Auth Required)

```bash
curl -X POST http://localhost:1234/api/interns/onboard \
  -H "Content-Type: application/json" \
  -d '{
    "email": "intern1@example.com",
    "password": "password123",
    "name": "John Doe",
    "joiningDate": "2025-05-01",
    "internshipDurationMonths": 6,
    "stipendType": "MONTHLY",
    "stipendAmount": 35000.0,
    "panNumber": "ABCDE1234F",
    "aadhaarNumber": "694305372619",
    "bankAccountNumber": "110134412744",
    "bankIfscCode": "CNRB0002602",
    "bankName": "Canara Bank",
    "bankBranch": "Circular Road",
    "address": "H.no.1150,Purusharti colony",
    "city": "Muzaffarnagar",
    "state": "UP",
    "pincode": "251001",
    "phoneNumber": "+91-9759523109"
  }'
```

**Expected Response:**
```
"Intern onboarded successfully"
```

**Save the email and password for next steps!**

---

### 3. Login (Get JWT Token)

```bash
curl -X POST http://localhost:1234/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "intern1@example.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "intern1@example.com",
  "name": "John Doe",
  "role": "INTERN",
  "userId": 1
}
```

**⚠️ IMPORTANT: Copy the `token` value for all subsequent requests!**

---

### 4. Generate Invoice (Intern)

Replace `<YOUR_TOKEN>` with the token from login:

```bash
curl -X POST http://localhost:1234/api/invoices/generate \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "month": 5,
    "year": 2025
  }'
```

**Expected Response:** Invoice object with calculated values

---

### 5. Get My Invoices (Intern)

```bash
curl -X GET http://localhost:1234/api/invoices/my-invoices \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

### 6. Get Invoice HTML (Intern)

First, note the invoice ID from step 5, then:

```bash
curl -X GET http://localhost:1234/api/invoices/1/html \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

**Expected Response:** HTML string (can be saved to file and opened in browser)

---

### 7. Request Leave (Intern)

```bash
curl -X POST http://localhost:1234/api/leaves/request \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "2025-06-15",
    "reason": "Personal work"
  }'
```

---

### 8. Get My Leaves (Intern)

```bash
curl -X GET http://localhost:1234/api/leaves/my-leaves \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

### 9. Get Leave Balance (Intern)

```bash
curl -X GET http://localhost:1234/api/leaves/balance \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

**Expected Response:**
```json
{
  "paidLeavesUsed": 0,
  "paidLeavesRemaining": 1,
  "unpaidLeavesTotal": 0,
  "totalPaidLeavesEarned": 1
}
```

---

### 10. Get Active Announcements (Public)

```bash
curl -X GET http://localhost:1234/api/announcements/active
```

---

### 11. AI Policy Buddy (Intern)

```bash
curl -X POST http://localhost:1234/api/ai/policy-buddy \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "How many paid leaves do I have left?"
  }'
```

**Note:** Requires OpenAI API key configured in `application.properties`

---

## 🔐 HR Endpoints Testing

### Create HR User (Manual - via Database or Code)

For testing HR endpoints, you need an HR user. You can:

1. **Option 1: Create via SQL** (if using H2 console):
   - Go to: http://localhost:1234/h2-console
   - JDBC URL: `jdbc:h2:mem:internflow`
   - Username: `sa`
   - Password: (empty)
   - Run:
   ```sql
   INSERT INTO users (email, password, role, name, created_at, updated_at)
   VALUES ('hr@franconnect.com', '$2a$10$...', 'HR', 'HR Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
   ```
   (Password should be BCrypt encoded - use online BCrypt generator)

2. **Option 2: Create via Code** (temporary endpoint):
   Add this to `AuthController` temporarily:
   ```java
   @PostMapping("/create-hr")
   public ResponseEntity<?> createHR() {
       User hr = userService.createUser("hr@franconnect.com", "hr123", "HR Manager", User.UserRole.HR);
       return ResponseEntity.ok("HR created: " + hr.getEmail());
   }
   ```

### HR Login

```bash
curl -X POST http://localhost:1234/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "hr@franconnect.com",
    "password": "hr123"
  }'
```

### HR Endpoints

#### Create Announcement
```bash
curl -X POST http://localhost:1234/api/announcements \
  -H "Authorization: Bearer <HR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Office Holiday",
    "body": "Office will be closed on...",
    "expiryDate": "2025-12-31"
  }'
```

#### Get All Invoices
```bash
curl -X GET http://localhost:1234/api/invoices/all \
  -H "Authorization: Bearer <HR_TOKEN>"
```

#### Approve Invoice
```bash
curl -X PUT "http://localhost:1234/api/invoices/1/status?status=APPROVED&remarks=Approved" \
  -H "Authorization: Bearer <HR_TOKEN>"
```

#### Get Pending Leaves
```bash
curl -X GET http://localhost:1234/api/leaves/pending \
  -H "Authorization: Bearer <HR_TOKEN>"
```

#### Approve Leave
```bash
curl -X PUT "http://localhost:1234/api/leaves/1/approve?approvedBy=HR Manager" \
  -H "Authorization: Bearer <HR_TOKEN>"
```

#### HR Monthly Summary (AI)
```bash
curl -X GET "http://localhost:1234/api/ai/hr-summary?month=5&year=2025" \
  -H "Authorization: Bearer <HR_TOKEN>"
```

---

## 📝 Complete Test Script

Save this as `test-apis.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:1234/api"

echo "=== 1. Health Check ==="
curl -s $BASE_URL/health | jq .

echo -e "\n=== 2. Intern Onboarding ==="
curl -s -X POST $BASE_URL/interns/onboard \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "name": "Test Intern",
    "joiningDate": "2025-05-01",
    "internshipDurationMonths": 6,
    "stipendType": "MONTHLY",
    "stipendAmount": 35000.0,
    "panNumber": "TEST1234F",
    "aadhaarNumber": "123456789012"
  }' | jq .

echo -e "\n=== 3. Login ==="
TOKEN=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123"
  }' | jq -r '.token')

echo "Token: $TOKEN"

echo -e "\n=== 4. Generate Invoice ==="
curl -s -X POST $BASE_URL/invoices/generate \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"month": 5, "year": 2025}' | jq .

echo -e "\n=== 5. Get My Invoices ==="
curl -s -X GET $BASE_URL/invoices/my-invoices \
  -H "Authorization: Bearer $TOKEN" | jq .

echo -e "\n=== 6. Get Leave Balance ==="
curl -s -X GET $BASE_URL/leaves/balance \
  -H "Authorization: Bearer $TOKEN" | jq .

echo -e "\n=== 7. Request Leave ==="
curl -s -X POST $BASE_URL/leaves/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "leaveDate": "2025-06-15",
    "reason": "Personal work"
  }' | jq .

echo -e "\n=== 8. Get Active Announcements ==="
curl -s -X GET $BASE_URL/announcements/active | jq .

echo -e "\n=== Testing Complete ==="
```

**Make it executable:**
```bash
chmod +x test-apis.sh
./test-apis.sh
```

---

## 🛠️ Using Postman/Thunder Client

### Import Collection

1. **Create New Collection:** "InternFlow API"

2. **Add Environment Variables:**
   - `base_url`: `http://localhost:1234/api`
   - `token`: (will be set after login)

3. **Create Requests:**

   **Auth:**
   - POST `/auth/login`
   - Body: `{"email": "...", "password": "..."}`
   - Save token to environment variable

   **Intern:**
   - POST `/interns/onboard`
   - POST `/invoices/generate`
   - GET `/invoices/my-invoices`
   - GET `/invoices/{id}/html`
   - POST `/leaves/request`
   - GET `/leaves/balance`
   - POST `/ai/policy-buddy`

   **HR:**
   - POST `/announcements`
   - GET `/invoices/all`
   - PUT `/invoices/{id}/status`
   - GET `/leaves/pending`
   - PUT `/leaves/{id}/approve`

4. **Set Authorization:**
   - Type: Bearer Token
   - Token: `{{token}}`

---

## 🐛 Troubleshooting

### Application Won't Start
- Check if port 1234 is available: `lsof -i :1234`
- Check Java version: `java -version` (should be 21+)
- Check logs for errors

### 401 Unauthorized
- Token expired (default: 24 hours)
- Token not included in header
- Token format wrong (should be `Bearer <token>`)

### 400 Bad Request
- Check request body format
- Check required fields
- Check date formats (YYYY-MM-DD)

### 500 Internal Server Error
- Check application logs
- Verify database connection
- Check if entities exist

---

## ✅ Quick Test Checklist

- [ ] Application starts successfully
- [ ] Health check returns 200
- [ ] Intern can register
- [ ] Intern can login and get token
- [ ] Intern can generate invoice
- [ ] Intern can view invoice HTML
- [ ] Intern can request leave
- [ ] Intern can check leave balance
- [ ] HR can view all invoices
- [ ] HR can approve/reject leaves
- [ ] Announcements work
- [ ] AI features work (if API key configured)

---

**Happy Testing! 🎉**

