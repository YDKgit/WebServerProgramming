package com.example.springstudy.config;

import com.example.springstudy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final DataSource dataSource;
    private final MemberRepository memberRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (memberRepository.count() > 0) {
            return;
        }

        ResourceDatabasePopulator populator =
                new ResourceDatabasePopulator(new ClassPathResource("data.sql"));

        populator.setSqlScriptEncoding("UTF-8");
        populator.execute(dataSource);
    }
}