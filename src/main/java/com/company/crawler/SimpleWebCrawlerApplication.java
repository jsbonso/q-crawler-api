package com.company.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *
 * @author Jon Bonso
 *
 * Main Class for the Web Crawler API Service
 *
 */
@SpringBootApplication
@EnableCaching

public class SimpleWebCrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleWebCrawlerApplication.class, args);
	}
}
