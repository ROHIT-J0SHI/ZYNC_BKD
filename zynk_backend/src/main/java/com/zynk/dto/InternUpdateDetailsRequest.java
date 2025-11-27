package com.zynk.dto;

import lombok.Data;

@Data
public class InternUpdateDetailsRequest {
    private String panNumber;
    private String aadhaarNumber;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankName;
    private String bankBranch;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phoneNumber;
}

