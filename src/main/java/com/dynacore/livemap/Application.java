package com.dynacore.livemap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import reactor.core.publisher.Hooks;

@SpringBootApplication(exclude = {
        HibernateJpaAutoConfiguration.class
})
public class Application {
  public static void main(String[] args) {
    Hooks.onOperatorDebug();
    SpringApplication.run(Application.class, args);
  }
}
