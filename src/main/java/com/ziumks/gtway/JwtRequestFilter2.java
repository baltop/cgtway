/*
 * 사용안함. 초기 샘플. global filter 대신 특정경우에만 사용하는 필터로 만들때 코딩하는 샘플.
 */

package com.ziumks.gtway;

import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
import org.springframework.cloud.gateway.filter.GatewayFilter;  
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;  
import org.springframework.http.HttpStatus;  
import org.springframework.stereotype.Component;

@Component  
public class JwtRequestFilter2 extends  
AbstractGatewayFilterFactory<JwtRequestFilter2.Config> {

  final Logger logger =
          LoggerFactory.getLogger(JwtRequestFilter.class);

  public static class Config {
      private String baseMessage;
      private boolean preLogger;
      private boolean postLogger;

      public Config(String baseMessage, boolean preLogger, boolean postLogger) {
          this.baseMessage = baseMessage;
          this.preLogger = preLogger;
          this.postLogger = postLogger;
      }
  }

  @Override
  public GatewayFilter apply(Config config) {
      return (exchange, chain) -> {
          try {
              String token = exchange.getRequest().getHeaders().get("Authorization").get(0).substring(7);
          } catch (NullPointerException e) {
              logger.warn("no token.");
              exchange.getResponse().setStatusCode(HttpStatus.valueOf(401));
              logger.info("status code :" + exchange.getResponse().getStatusCode());
              return chain.filter(exchange);
          }
          return chain.filter(exchange);
      };
  }
}