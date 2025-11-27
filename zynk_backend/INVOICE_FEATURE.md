# Invoice Generation Feature

## Overview
The invoice generation feature automatically creates professional invoices matching your exact template format. The system calculates all values automatically and generates HTML that can be converted to PDF.

## Features

### ✅ Automatic Invoice Generation
- **Invoice Number Format:** `006/2025-26` (month number + financial year)
- **Automatic Calculations:**
  - Billing period dates (month start/end)
  - Working days (excluding weekends)
  - Paid/unpaid leaves from leave module
  - Final stipend amount
- **Amount in Words:** Automatically converts amount to words (e.g., "Thirty five thousand only")

### ✅ Invoice Template
Matches your exact format:
- Invoice number and date
- From section (Intern details)
- To section (FranConnect details)
- Billing table with all calculations
- Amount in words
- Bank details
- Signature section

## API Endpoints

### 1. Generate Invoice
**POST** `/api/invoices/generate`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
  "month": 9,
  "year": 2025
}
```

**Response:** Invoice object with all calculated values

### 2. Get Invoice HTML
**GET** `/api/invoices/{invoiceId}/html`

**Response:** HTML string that can be:
- Displayed in browser
- Converted to PDF using jsPDF + html2canvas
- Sent to HR via email

**Example:**
```bash
curl -X GET http://localhost:1234/api/invoices/1/html \
  -H "Authorization: Bearer <token>"
```

### 3. Get Invoice Details
**GET** `/api/invoices/{invoiceId}`

Returns invoice JSON with all details.

### 4. Get My Invoices
**GET** `/api/invoices/my-invoices`

**Headers:**
```
Authorization: Bearer <jwt-token>
```

Returns list of all invoices for logged-in intern.

## Frontend Integration

### Step 1: Generate Invoice
```javascript
// Generate invoice for a month
const generateInvoice = async (month, year) => {
  const response = await axios.post('/api/invoices/generate', 
    { month, year },
    { headers: { 'Authorization': `Bearer ${token}` } }
  );
  return response.data; // Returns invoice object with ID
};
```

### Step 2: Get Invoice HTML
```javascript
// Get invoice HTML
const getInvoiceHtml = async (invoiceId) => {
  const response = await axios.get(`/api/invoices/${invoiceId}/html`,
    { headers: { 'Authorization': `Bearer ${token}` } }
  );
  return response.data; // Returns HTML string
};
```

### Step 3: Display/Download PDF
```javascript
// Option 1: Display in new window
const displayInvoice = async (invoiceId) => {
  const html = await getInvoiceHtml(invoiceId);
  const newWindow = window.open();
  newWindow.document.write(html);
  newWindow.document.close();
};

// Option 2: Convert to PDF using jsPDF
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

const downloadInvoicePDF = async (invoiceId) => {
  const html = await getInvoiceHtml(invoiceId);
  
  // Create temporary div with invoice HTML
  const tempDiv = document.createElement('div');
  tempDiv.innerHTML = html;
  tempDiv.style.position = 'absolute';
  tempDiv.style.left = '-9999px';
  document.body.appendChild(tempDiv);
  
  // Convert to canvas then PDF
  const canvas = await html2canvas(tempDiv);
  const imgData = canvas.toDataURL('image/png');
  
  const pdf = new jsPDF('p', 'mm', 'a4');
  const imgWidth = 210;
  const pageHeight = 295;
  const imgHeight = (canvas.height * imgWidth) / canvas.width;
  let heightLeft = imgHeight;
  
  let position = 0;
  pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
  heightLeft -= pageHeight;
  
  while (heightLeft >= 0) {
    position = heightLeft - imgHeight;
    pdf.addPage();
    pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
    heightLeft -= pageHeight;
  }
  
  pdf.save(`invoice-${invoiceId}.pdf`);
  document.body.removeChild(tempDiv);
};
```

## Invoice Format Details

### Invoice Number
- Format: `006/2025-26`
- `006` = Month number since joining (001, 002, 003...)
- `2025-26` = Financial year

### From Section
Includes:
- Name
- Address (full address from city, state, pincode)
- Contact (phone number)
- E-mail
- PAN
- Aadhar (formatted with spaces: `6943 0537 2619`)

### Billing Table
- **From:** Month start date (e.g., `1-Sep-25`)
- **Till:** Month end date (e.g., `30-Sep-25`)
- **Total Working days:** Calculated excluding weekends
- **Monthly Rate:** Intern's stipend amount
- **Total Amount:** Calculated after deducting unpaid leaves

### Amount in Words
Automatically converts to words:
- `35000` → "Thirty five thousand rupees only"
- `15000.50` → "Fifteen thousand rupees and fifty paise"

### Bank Details
- Bank Name
- Account Number
- IFSC Code
- Branch Name

### Signature
- Displays signature image if uploaded
- Shows intern name below signature

## Workflow

1. **Intern generates invoice:**
   - Selects month and year
   - System validates month is within internship period
   - System calculates all values automatically
   - Invoice is created with status `PENDING`

2. **Intern views/downloads invoice:**
   - Gets invoice HTML
   - Can view in browser
   - Can download as PDF
   - Can send to HR

3. **HR reviews invoice:**
   - HR can see all invoices
   - HR can update status: `APPROVED` or `PAID`
   - HR can add remarks

## Example Usage

### Complete Flow
```javascript
// 1. Generate invoice for September 2025
const invoice = await generateInvoice(9, 2025);
console.log('Invoice ID:', invoice.id);

// 2. Get invoice HTML
const html = await getInvoiceHtml(invoice.id);

// 3. Display in new window
displayInvoice(invoice.id);

// 4. Or download as PDF
downloadInvoicePDF(invoice.id);
```

## Notes

1. **Invoice Validation:**
   - Month must be within internship period
   - Only one invoice per month per intern
   - System prevents duplicate invoices

2. **Automatic Calculations:**
   - Working days exclude weekends
   - Unpaid leaves are deducted from stipend
   - Paid leaves don't affect stipend

3. **Signature:**
   - Upload signature during onboarding
   - Signature file path stored in `InternDetails.signatureFilePath`
   - Signature displayed in invoice HTML if available

4. **Phone Number:**
   - Added to `InternOnboardingRequest`
   - Optional field
   - Displayed in invoice if provided

## Testing

### Test Invoice Generation
```bash
# 1. Login
curl -X POST http://localhost:1234/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"intern@example.com","password":"password"}'

# 2. Generate invoice (use token from step 1)
curl -X POST http://localhost:1234/api/invoices/generate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"month":9,"year":2025}'

# 3. Get invoice HTML
curl -X GET http://localhost:1234/api/invoices/1/html \
  -H "Authorization: Bearer <token>"
```

---

**Your invoice generation is ready! 🎉**

The system automatically generates invoices matching your exact template format. Interns can generate, view, and download invoices, and HR can review and approve them.

