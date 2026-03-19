package com.avbinvest.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = "com.avbinvest.company.client")
public class CompanyServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CompanyServiceApp.class, args);
    }
}
