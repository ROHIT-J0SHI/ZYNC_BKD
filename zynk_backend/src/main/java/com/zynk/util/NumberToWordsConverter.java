package com.zynk.util;

public class NumberToWordsConverter {
    
    private static final String[] ones = {
        "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
        "seventeen", "eighteen", "nineteen"
    };
    
    private static final String[] tens = {
        "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };
    
    public static String convertToWords(double amount) {
        long rupees = (long) amount;
        long paise = Math.round((amount - rupees) * 100);
        
        String rupeesWords = convertNumberToWords(rupees);
        String result = capitalizeFirst(rupeesWords) + " rupees";
        
        if (paise > 0) {
            String paiseWords = convertNumberToWords(paise);
            result += " and " + paiseWords + " paise";
        } else {
            result += " only";
        }
        
        return result;
    }
    
    private static String convertNumberToWords(long number) {
        if (number == 0) {
            return "zero";
        }
        
        if (number < 20) {
            return ones[(int) number];
        }
        
        if (number < 100) {
            return tens[(int) (number / 10)] + 
                   (number % 10 != 0 ? " " + ones[(int) (number % 10)] : "");
        }
        
        if (number < 1000) {
            return ones[(int) (number / 100)] + " hundred" + 
                   (number % 100 != 0 ? " " + convertNumberToWords(number % 100) : "");
        }
        
        if (number < 100000) {
            return convertNumberToWords(number / 1000) + " thousand" + 
                   (number % 1000 != 0 ? " " + convertNumberToWords(number % 1000) : "");
        }
        
        if (number < 10000000) {
            return convertNumberToWords(number / 100000) + " lakh" + 
                   (number % 100000 != 0 ? " " + convertNumberToWords(number % 100000) : "");
        }
        
        return convertNumberToWords(number / 10000000) + " crore" + 
               (number % 10000000 != 0 ? " " + convertNumberToWords(number % 10000000) : "");
    }
    
    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

