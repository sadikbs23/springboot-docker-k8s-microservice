package com.bs23.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator bsBankRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
						.route(r -> r
							.path("/bsbank/accounts/**")
							.filters(f->f.rewritePath("/bsbank/accounts/(?<segment>.*)","/${segment}")
									.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
							.uri("lb://ACCOUNTS"))
						.route(r -> r
							.path("/bsbank/loans/**")
							.filters(f->f.rewritePath("/bsbank/loans/(?<segment>.*)","/${segment}")
									.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
							.uri("lb://LOANS"))
				.		route(r -> r
							.path("/bsbank/cards/**")
							.filters(f->f.rewritePath("/bsbank/cards/(?<segment>.*)","/${segment}")
									.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
							.uri("lb://CARDS")).build();

	}

}
