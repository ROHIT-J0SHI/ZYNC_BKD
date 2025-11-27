package com.zynk.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class WorkingDaysCalculator {
    
    /**
     * Calculates working days between two dates (excluding weekends)
     */
    public static int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return 0;
        }
        
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int workingDays = 0;
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return workingDays;
    }
}

