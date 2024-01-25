package com.esg.test;

import lombok.Data;

@Data
public class Customer {

  private String customerRef;
  private String customerName;
  private String addressLine1;
  private String addressLine2;
  private String town;
  private String county;
  private String country;
  private String postcode;
  
}
