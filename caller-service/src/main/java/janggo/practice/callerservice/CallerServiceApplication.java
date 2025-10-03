package janggo.practice.callerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class CallerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallerServiceApplication.class, args);
    }

}
