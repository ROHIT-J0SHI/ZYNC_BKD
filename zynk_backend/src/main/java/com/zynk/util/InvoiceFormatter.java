package com.zynk.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class InvoiceFormatter {
    
    /**
     * Formats invoice number as: 006/2025-26
     * Where 006 is the month number and 2025-26 is financial year
     */
    public static String formatInvoiceNumber(String monthNumber, YearMonth invoiceMonth) {
        int year = invoiceMonth.getYear();
        int nextYear = year + 1;
        String financialYear = year + "-" + String.valueOf(nextYear).substring(2);
        return monthNumber + "/" + financialYear;
    }
    
    /**
     * Formats date as: 30-Sep-2025
     */
    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return date.format(formatter);
    }
    
    /**
     * Formats date range as: 1-Sep-25 to 30-Sep-25
     */
    public static String formatDateRange(LocalDate from, LocalDate till) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy");
        return formatter.format(from) + " to " + formatter.format(till);
    }
    
    /**
     * Formats Aadhaar with spaces: 6943 0537 2619
     */
    public static String formatAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() != 12) {
            return aadhaar;
        }
        return aadhaar.substring(0, 4) + " " + aadhaar.substring(4, 8) + " " + aadhaar.substring(8, 12);
    }
}

