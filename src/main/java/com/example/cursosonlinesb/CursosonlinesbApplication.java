package com.example.cursosonlinesb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.dialect.springdata.SpringDataDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

@SpringBootApplication
public class CursosonlinesbApplication {

    public static void main(String[] args) {
        SpringApplication.run(CursosonlinesbApplication.class, args);
    }

    /**
     * Agrega el dialecto Spring Data Dialect a Thymeleaf
     **/
    @Bean
    public SpringDataDialect springDataDialect() {
        return new SpringDataDialect();
    }

    /**
     * Agrega el dialecto Spring Security Dialect a Thymeleaf
     **/
    @Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }

}
