package com.ziumks.gtway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;

import reactor.core.publisher.Mono;

@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication
public class GtwayApplication {

	public static void main(String[] args) {
		System.setProperty("reactor.netty.http.server.accessLogEnabled", "true");
		SpringApplication.run(GtwayApplication.class, args);
	}

//	@RequestMapping("/fallback")
//	public Mono<String> fallback() {
//	  return Mono.just("fallback!");
//	}


	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
		    .route(p -> p
		        .path("/get")
		        .filters(f -> f.addRequestHeader("Hello", "World"))
		        .uri("http://httpbin.org:80"))
//		    .route(p -> p
//		    	.path("/employee")
//		    	.uri("http://localhost:8082"))
//			.route("path_route", r -> r.path("/employee/**/*")
//				.uri("http://localhost:8082"))
			.route("host_route", r -> r.host("*.myhost.org")
				.uri("http://httpbin.org"))
			.route("rewrite_route", r -> r.host("*.rewrite.org")
				.filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
				.uri("http://httpbin.org"))
	        .route(p -> p
	            .host("*.example.com")
	            .filters(f -> f.hystrix(config -> config.setName("mycmd").setFallbackUri("forward:/fallback")))
	            .uri("http://httpbin.org:80"))
			// .route("hystrix_route", r -> r.host("*.hystrix.org")
			// 	.filters(f -> f.hystrix(c -> c.setName("slowcmd")))
			// 	.uri("http://httpbin.org"))
			// .route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
			// 	.filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
			// 	.uri("http://httpbin.org"))
			// .route("limit_route", r -> r
			// 	.host("*.limited.org").and().path("/anything/**")
			// 	.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
			// 	.uri("http://httpbin.org"))
			.build();
	}
	
	
}
