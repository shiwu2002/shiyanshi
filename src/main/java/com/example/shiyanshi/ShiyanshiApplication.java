package com.example.shiyanshi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.shiyanshi.mapper")
@EnableScheduling
public class ShiyanshiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShiyanshiApplication.class, args);
	}

}
