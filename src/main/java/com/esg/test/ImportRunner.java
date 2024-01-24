package com.esg.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportRunner implements ApplicationRunner{
    
  private static final String POSTCODE = "Postcode";
  private static final String COUNTRY = "Country";
  private static final String COUNTY = "County";
  private static final String TOWN = "Town";
  private static final String ADDRESS_LINE2 = "Address Line2";
  private static final String ADDRESS_LINE1 = "Address Line1";
  private static final String CUSTOMER_NAME = "Customer Name";
  private static final String CUSTOMER_REF = "Customer Ref";
  
  @Value("${api.url}")
  private String apiUrl;
  
  private final RestTemplate restTemplate;
  
  @Override
  public void run(ApplicationArguments args) throws Exception {
    
    if (args.getNonOptionArgs().isEmpty()) {
      log.error("Add the csv file name as a parameter");
    } else {
      var name = args.getNonOptionArgs().get(0);

      File file = new File(name);

      var urls = args.getOptionValues("api.url");
      var url = (urls!= null && !urls.isEmpty())?urls.get(0) : apiUrl; 
      try {
        log.info("processing {}...", file.getAbsoluteFile());
        var customers = readCsv(new FileInputStream(file));
        
        log.info("{} entries found", customers.size());
        upload(customers, url);
      } catch (FileNotFoundException ex) {
        log.error("Cannot find file {}", file.getAbsoluteFile());
      }

    }
    
  }
  
  private void upload(List<Customer> customers, String url) {
    
    log.info("connecting to {}", url);
    for(Customer customer: customers) {
      log.info("posting {}", customer.getCustomerRef());
      try {
        restTemplate.postForEntity(url, customer, Void.class);
      } catch (HttpStatusCodeException ex) {
        log.error("Unsuccessful POST request: {}", ex.getStatusCode());
      }
    }
    
  }

  public List<Customer> readCsv(InputStream is) {
    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        CSVParser csvParser = new CSVParser(fileReader,
            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

      List<Customer> customers= new ArrayList<>();

      for (CSVRecord csvRecord : csvParser.getRecords()) {
        Customer customer = new Customer();
        if(csvRecord.isMapped(CUSTOMER_REF)) {
          customer.setCustomerRef(csvRecord.get(CUSTOMER_REF));
        }
        if(csvRecord.isMapped(CUSTOMER_NAME)) {
          customer.setCustomerName(csvRecord.get(CUSTOMER_NAME));
        }
        if(csvRecord.isMapped(ADDRESS_LINE1)) {
          customer.setAddressLine1(csvRecord.get(ADDRESS_LINE1));
        }
        if(csvRecord.isMapped(ADDRESS_LINE2)) {
          customer.setAddressLine2(csvRecord.get(ADDRESS_LINE2));
        }
        if(csvRecord.isMapped(TOWN)) {
          customer.setTown(csvRecord.get(TOWN));
        }
        if(csvRecord.isMapped(COUNTY)) {
          customer.setCounty(csvRecord.get(COUNTY));
        }
        if(csvRecord.isMapped(COUNTRY)) {
          customer.setCountry(csvRecord.get(COUNTRY));
        }
        if(csvRecord.isMapped(POSTCODE)) {
          customer.setPostcode(csvRecord.get(POSTCODE));
        }

        customers.add(customer);
      }

      return customers;
    } catch (IOException e) {
      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
    }
  }
  
}
