package webproject.example.webproject.softuni;

import org.springframework.scheduling.annotation.EnableScheduling;
import webproject.example.webproject.softuni.config.cloudinary.CloudinaryConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CloudinaryConfigProperties.class)
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
