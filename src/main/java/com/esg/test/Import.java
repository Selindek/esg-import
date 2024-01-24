package com.esg.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Import implements ApplicationRunner{

    
  @Value("${api.url}")
  private String apiUrl;
  
  /**
   * Java application main method.
   *
   * @param args launch arguments
   */
  public static void main(String[] args) {
    final SpringApplication application = new SpringApplication(Import.class);
    final Properties properties = new Properties();
    properties.setProperty("spring.main.banner-mode", "off");
    properties.setProperty("logging.level.com.esg.test", "INFO");
    properties.setProperty("logging.level", "DEBUG");
    properties.setProperty("logging.pattern.console", "%clr(%m){faint}%n");
    application.setDefaultProperties(properties);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.run(args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    
    if (args.getNonOptionArgs().isEmpty()) {
      log.error("add full path of the csv file as a parameter");
      System.exit(0);
    } 
    
    var name = args.getNonOptionArgs().get(0);

    //Path path = Path.of(name);
    File file = new File(name);

    
    
    if(!file.exists()) {
      log.error("Cannot find file {}", file.getAbsoluteFile());
      System.exit(0);
    }

    log.info("processing {}...", file.getAbsoluteFile());

    var customers = readCsv(new FileInputStream(file));
    
    log.info("{} entries found" , customers.size());
    
    upload(customers);
  }

  
  private void upload(List<Customer> customers) {
    
    
    RestTemplate restTemplate = new RestTemplate();
    
    log.info(apiUrl);
    for(Customer customer: customers) {
      log.info("posting {}", customer.getCustomerRef());
      try {
        restTemplate.postForEntity(apiUrl, customer, Void.class);
      } catch (HttpStatusCodeException ex) {
        log.error("Unsuccessful POST request: {}", ex.getStatusCode());
      }
    }
    
  }

  public static List<Customer> readCsv(InputStream is) {
    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        CSVParser csvParser = new CSVParser(fileReader,
            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

      List<Customer> customers= new ArrayList<>();

      for (CSVRecord csvRecord : csvParser.getRecords()) {
        Customer customer = new Customer();
        customer.setCustomerRef(csvRecord.get("Customer Ref"));
        customer.setCustomerName(csvRecord.get("Customer Name"));
        customer.setAddressLine1(csvRecord.get("Address Line1"));
        customer.setAddressLine2(csvRecord.get("Address Line2"));
        customer.setTown(csvRecord.get("Town"));
        customer.setCounty(csvRecord.get("County"));
        customer.setCountry(csvRecord.get("Country"));
        customer.setPostcode(csvRecord.get("Postcode"));

        customers.add(customer);
      }

      return customers;
    } catch (IOException e) {
      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
    }
  }
  
}
