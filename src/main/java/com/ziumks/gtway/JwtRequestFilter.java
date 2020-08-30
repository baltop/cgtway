/*
 * https://github.com/auth0/java-jwt
 * https://medium.com/trabe/validate-jwt-tokens-using-jwks-in-java-214f7014b5cf
 */
package com.ziumks.gtway;

import java.util.Calendar;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import reactor.core.publisher.Mono;

@Component
public class JwtRequestFilter implements GlobalFilter {
	final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

	// properties에서 키를 가져올 경우 
    private static final String PUBLIC_KEY_ALGORITHM = "RSA";
    private static final String nstring = "mL75Vq8P29qbIlwCEtU3e6-leBis_lXpWaxKFNrLu5DTQuGtiCw4eAGcB-WjRTFIGsl0s77jXGnuMvcH3MBpAZEjVYpNYCmcYjwvF7iBpEk-QvbYFdXNtpPpsI_i7xJJeIwFSI-CpXVXpu3U10E9UNHLXu9InL8pdHpLJ8ax2BuJ3B0dEigSmtWaiFeyyiI6wzB33sVT0ErwyNvdzPymLKbbCzipqYdGAI6IJq8XAH-4OoTPkJIpwLFFp3PvZNQvd7d1w8OoXzqbzvnQStIUYZElCpXW7NoTQxaflVcDcJeUjNpicBtP8jI0_fUIJtPHKNF252rCli0QPva-rSIc1hNY2OXlc1HGtkcodVt_cpbE9ilRSTfP9t0NgZ2KbyHoxP0WxnFqvAR5_G-22wGFIOehxfr8eKD1LczYhnxYVtB1Q3yvKsJKfqedHssB9NkpcLnTpXVhTF8CWPQWYkreJQ_W-Mmv3TIlmQ5U7hRmpLci9P4Sn6KN3tNu1rdthWQOot3AbVWQibtD7A0ngrmnyAfYGfeeRtwNRqo7OqIMmnTTHLIg3J6SuhKGN8T_AK1B3jAy_eKWZbQuguL8IF0Ms9FnZ8TiSp2VWZTwwsbsYmaBmrbktf00dd3G54h3wc8U4z7g8CByQ6L02eW70bbOlbegWfm3m1L4WNV45Jqq6tc";
    private static final String estring = "AQAB";
    
    
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		try {
			String token = exchange.getRequest().getHeaders().get("Authorization").get(0).substring(7);

			DecodedJWT jwt = JWT.decode(token);
			// auth server에서 jwks를 가져올 경우.
//			JwkProvider provider = new UrlJwkProvider("http://localhost:4444");
//			Jwk jwk = provider.get(jwt.getKeyId());
//			logger.info("+++++++++++++++++++++++++"+jwk.getPublicKey().toString());
			
			
			// properties에서 키를 가져올 경우 
			RSAPublicKey publickeyOfRsa = null;
	        try {
	            KeyFactory kf = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
	            BigInteger modulus = new BigInteger(1, Base64.decodeBase64(nstring));
	            BigInteger exponent = new BigInteger(1, Base64.decodeBase64(estring));
	            publickeyOfRsa = (RSAPublicKey) kf.generatePublic(new RSAPublicKeySpec(modulus, exponent));
	        } catch (InvalidKeySpecException e) {
	            throw new InvalidPublicKeyException("Invalid public key", e);
	        } catch (NoSuchAlgorithmException e) {
	            throw new InvalidPublicKeyException("Invalid algorithm to generate key", e);
	        }
			
			
	        // auth server에서 jwks를 가져올 경우.
			// Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
	        
	        // properties에서 키를 가져올 경우 
	        Algorithm algorithm = Algorithm.RSA256(publickeyOfRsa, null);
	        
	        
			algorithm.verify(jwt);
			if (jwt.getExpiresAt().before(Calendar.getInstance().getTime())) {
				throw new RuntimeException("Exired token!");
			}
			logger.info("+++++++++++++++++++++++++"+jwt.getAlgorithm());
			logger.info("+++++++++++++++++++++++++"+jwt.getSubject());
			

		} catch (NullPointerException e) {
			logger.warn("no token.");
			exchange.getResponse().setStatusCode(HttpStatus.valueOf(401));
			logger.info("status code :" + exchange.getResponse().getStatusCode());
			return chain.filter(exchange);
		} catch (JwkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chain.filter(exchange);
	}
}