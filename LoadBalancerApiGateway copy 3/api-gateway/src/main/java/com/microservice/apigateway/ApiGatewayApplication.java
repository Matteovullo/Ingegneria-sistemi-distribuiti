package com.microservice.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
/**
 * Main class where spring boot application starts.
 *
 * @author Vishnu Viswambharan
 * @version 1.0
 * @since 2020-08-23
 */

@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
