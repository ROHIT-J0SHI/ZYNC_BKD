package com.zynk.util;

import java.time.LocalDate;
import java.time.YearMonth;

public class InvoiceNumberGenerator {
    
    /**
     * Generates invoice number based on months since joining
     * Formula: months_since_join = (invoiceYear - joinYear)*12 + (invoiceMonth - joinMonth) + 1
     * Format: 001, 002, 003, etc.
     */
    public static String generateInvoiceNumber(LocalDate joiningDate, YearMonth invoiceMonth) {
        YearMonth joiningMonth = YearMonth.from(joiningDate);
        
        int monthsSinceJoin = (invoiceMonth.getYear() - joiningMonth.getYear()) * 12 
                            + (invoiceMonth.getMonthValue() - joiningMonth.getMonthValue()) + 1;
        
        // Ensure it's at least 1
        if (monthsSinceJoin < 1) {
            monthsSinceJoin = 1;
        }
        
        return String.format("%03d", monthsSinceJoin);
    }
    
    /**
     * Gets the first and last day of the month
     */
    public static LocalDate[] getMonthStartEndDates(YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return new LocalDate[]{start, end};
    }
}

