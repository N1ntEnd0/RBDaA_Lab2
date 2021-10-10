package com.lab2.backend.configuration;


import com.lab2.backend.entity.Task;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfiguration {

    @Bean
    @Scope("prototype")
    public Task getBook(){
        return new Task();
    }


}
