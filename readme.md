# Statistics Challenge

## Introduction
The solution is a simple Spring Boot Application.
For the sake of this demonstration, the valid transactions are stored in a buffer.
The buffer is flushed once the statistics are requested.

In order to achieve a high scalability to retrieve the statistics data, I decided to parallelise the computation of all the values.
Furthermore, we are using Parallel streams. That should, in theory, if sufficient threads are available, calculate the values
for the statistics at O(1) time.


Another possibilty that I haven't shown here is to update a single statistic object asynchronously.

## Run the solution
You need Java 8 and Maven installed on your machine.
To execute the test suite, enter the following in your console:


```sh
mvn clean install
```

The project and their tests should complete successfully.


## Run
To run the application, enter the following command in your console:



```sh
 mvn spring-boot:run

```

Now the required endpoints are available under `http://localhost:8090/transactions` and `http://localhost:8090/statistics`

