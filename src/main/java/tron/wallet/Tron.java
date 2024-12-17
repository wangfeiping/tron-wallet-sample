package tron.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.boot.autoconfigure.domain.EntityScan;
// import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableScheduling
@SpringBootApplication
public class Tron {
 
  public static void main(String[] args) {
    SpringApplication.run(Tron.class, args);
  }
}
