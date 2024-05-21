package io.geekya215.nyaoj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.geekya215.nyaoj.*")
public class NyaojApplication {

    public static void main(String[] args) {
        SpringApplication.run(NyaojApplication.class, args);
    }

}
