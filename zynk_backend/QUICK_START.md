# Quick Start Guide

## 🚀 Start Application

```bash
cd "/home/tanisha.sharma@ad.franconnect.com/Downloads/ZYNK PROJECT"
./mvnw spring-boot:run
```

Wait for: `Started Application in X.XXX seconds`

Application runs on: **http://localhost:1234**

---

## 🧪 Quick API Test

### 1. Test Health
```bash
curl http://localhost:1234/api/health
```

### 2. Register Intern
```bash
curl -X POST http://localhost:1234/api/interns/onboard \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "name": "Test User",
    "joiningDate": "2025-05-01",
    "internshipDurationMonths": 6,
    "stipendType": "MONTHLY",
    "stipendAmount": 35000.0,
    "panNumber": "TEST1234F",
    "aadhaarNumber": "123456789012"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:1234/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123"
  }'
```

**Copy the `token` from response!**

### 4. Generate Invoice (Replace YOUR_TOKEN)
```bash
curl -X POST http://localhost:1234/api/invoices/generate \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"month": 5, "year": 2025}'
```

### 5. Get Invoice HTML
```bash
curl -X GET http://localhost:1234/api/invoices/1/html \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📚 Full Documentation

- **Complete Testing Guide:** See `TESTING_GUIDE.md`
- **API Documentation:** See `API_DOCUMENTATION.md`
- **Invoice Feature:** See `INVOICE_FEATURE.md`

---

## 🎯 Next Steps

1. Start the application
2. Test APIs using curl or Postman
3. Build frontend and connect to these APIs
4. Configure MySQL for production (optional)
5. Add OpenAI API key for AI features (optional)

---

**Ready to go! 🎉**

