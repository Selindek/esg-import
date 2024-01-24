package com.esg.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
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
  public void testGivenMissingCsvWhenRunImportThenErrorIsLogged() throws Exception {
    ApplicationArguments args = new DefaultApplicationArguments("target/test-classes/nosuchfile.csv","--api.url=http://localhost/test");
    ConsoleCaptor console = new ConsoleCaptor();
    
    runner.run(args);
    
    console.close();
    var output = console.getStandardOutput();
    
    assertThat(output.get(output.size()-1).contains("Cannot find file"), is(true));
    
  }
  
  @Test
  public void testGivenMissingCsvWhenRunImportThenPostIsNotCalled() throws Exception {
    ApplicationArguments args = new DefaultApplicationArguments("target/test-classes/nosuchfile.csv","--api.url=http://localhost/test");
    runner.run(args);
    verify(restTemplate, times(0)).postForEntity(any(), any(), any());
  }

}
