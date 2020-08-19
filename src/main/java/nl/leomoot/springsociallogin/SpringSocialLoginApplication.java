package nl.leomoot.springsociallogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import nl.leomoot.springsociallogin.config.ApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class SpringSocialLoginApplication {

  public static void main(final String[] args) {
    SpringApplication.run(SpringSocialLoginApplication.class, args);
  }

}
