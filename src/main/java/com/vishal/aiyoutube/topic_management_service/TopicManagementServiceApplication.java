package com.vishal.aiyoutube.topic_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class TopicManagementServiceApplication {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}


	public static void main(String[] args) {
		SpringApplication.run(TopicManagementServiceApplication.class, args);
	}

}
