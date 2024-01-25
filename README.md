# ESG Test Task - Import App

Build the project with Maven then run it form inside an IDE or from the command line:

**java -jar target/import-0.0.1-SNAPSHOT.jar {filename} [--api.url={customer.endpoint}]**

filename can be absolute or relative to the current directory.

There is a test.csv file in the main directory in the project which contains some basic entries
+ one bad entry with a missing Customer Ref

The application sends the customer data to the api.url defined in the application.properties file,
defaulted to http://localhost:8080/customers

The esg.test.server should be launched beforehand otherwise a connection exception is thrown to the console.

The api.url can be also overridden with a launch parameter: --api.url


example:

**java -jar target/import-0.0.1-SNAPSHOT.jar test.csv --api.url=http://localhost:8080/customers**
