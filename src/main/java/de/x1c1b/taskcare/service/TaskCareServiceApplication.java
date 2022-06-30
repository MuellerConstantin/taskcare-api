package de.x1c1b.taskcare.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
public class TaskCareServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskCareServiceApplication.class, args);
	}
}
