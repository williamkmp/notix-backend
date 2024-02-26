package com.william.notix;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotixApplication {

    public static void main(String[] args) {
        SpringApplication app = new  SpringApplication(NotixApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
