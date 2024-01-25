package com.esg.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import nl.altindag.console.ConsoleCaptor;

@ExtendWith(MockitoExtension.class)
class ImportRunnerTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private ImportRunner runner;

  @Test
  public void testGivenMissingCsvParameterWhenRunImportThenErrorIsLogged() throws Exception {
    ApplicationArguments args = new DefaultApplicationArguments("--api.url=http://localhost/test");
    ConsoleCaptor console = new ConsoleCaptor();
    
    runner.run(args);
    
    console.close();
    var output = console.getStandardOutput();
    
    assertThat(output.get(output.size()-1).contains("Add the csv file name as a parameter"), is(true));
    
  }
  
  @Test
  public void testGivenValidCsvWithThreeEntriesWhenRunImportThenPostIsCalledThreeTimes() throws Exception {
    ApplicationArguments args = new DefaultApplicationArguments("target/test-classes/test1.csv","--api.url=http://localhost/test");
    runner.run(args);
    verify(restTemplate, times(3)).postForEntity(eq("http://localhost/test"), any(), any());
  }


  @Test
  public void testGivenValidCsvWithOneEntryWhenRunImportThenPostIsCalledWithProperArgument() throws Exception {
    Customer expected = new Customer();
    expected.setCustomerRef("123");
    expected.setCustomerName("Jane Doe");
    expected.setAddressLine1("10 Downing Street");
    expected.setAddressLine2("");
    expected.setTown("London");
    expected.setCounty("Greater London");
    expected.setCountry("England");
    expected.setPostcode("SW1A 2AA");
    ApplicationArguments args = new DefaultApplicationArguments("target/test-classes/test2.csv","--api.url=http://localhost/test");
    runner.run(args);
    verify(restTemplate, times(1)).postForEntity(eq("http://localhost/test"), eq(expected), any());
  }

  @Test
  public void testGivenServerIsUnaccessibleWhenRunImportThenErrorIsLogged() throws Exception {
    ApplicationArguments args = new DefaultApplicationArguments("target/test-classes/test1.csv","--api.url=http://invalid.com/nosuchendpoint");
    
    when(restTemplate.postForEntity(eq("http://invalid.com/nosuchendpoint"), any(), any()))
      .thenThrow(new ResourceAccessException("Connection refused"));
    
    ConsoleCaptor console = new ConsoleCaptor();
    
    runner.run(args);
    
    console.close();
    var output = console.getStandardOutput();
    
    assertThat(output.get(output.size()-1).contains("Cannot access http://invalid.com/nosuchendpoint"), is(true));
  }
  
  
  @Test
  public void testGivenNonExistingCsvWhenRunImportThenErrorIsLogged() throws Exception {
    ApplicationArguments args = new DefaultApplicationArguments("target/test-classes/nosuchfile.csv","--api.url=http://localhost/test");
    ConsoleCaptor console = new ConsoleCaptor();
    
    runner.run(args);
    
    console.close();
    var output = console.getStandardOutput();
    
    assertThat(output.get(output.size()-1).contains("Cannot find file"), is(true));
    
  }
  
  @Test
  public void testGivenNonExistingCsvWhenRunImportThenPostIsNotCalled() throws Exception {
    ApplicationArguments args = new DefaultApplicationArguments("target/test-classes/nosuchfile.csv","--api.url=http://localhost/test");
    runner.run(args);
    verify(restTemplate, times(0)).postForEntity(any(), any(), any());
  }

}
