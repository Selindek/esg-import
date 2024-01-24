package com.esg.test;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Import {

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
  
}
