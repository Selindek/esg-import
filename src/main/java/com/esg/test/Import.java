package com.esg.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Import Application.
 * 
 * @author István Rátkai (Selindek)
 *
 */
@SpringBootApplication
public class Import {

  public static void main(String[] args) {
    final SpringApplication application = new SpringApplication(Import.class);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.run(args);
  }
  
}
