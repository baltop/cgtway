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
    private static final String nstring = "xjYxI07YvSBuEgiHJYzenonPrsGuVNFOvawzz1-Q-PrrvQNL8iqBw4PWO_oSGTH2d8SMNV_S7Q-bT0wrqgJN2bQ3AS72pYoolYVQVw3P3ICvII44IQkPNg80NtY8J4XKgXioLRcSTCg77dh__1ibcsWeNUDECa4zEP2G6jP4qY2SlIwOQy0uCnnPmq8bYAbrI0mNF7zLjypZtsKf_gLym26viZa7UkepNOP17Pshd0-IFJ2tZD1o_KCzNKe1uTHvlroiCFyAWOqoxR5SGmfvF7qZY97bgZZTf1LuYmlz1VDr3ZhA0PHD7MStu8uyTOr0FouAEGxSqUJya-Wk90BIbXHUzLYGJmvevsTIm5rlqO27hdWiIh13sC6GULJnn-xBWBZ26Lz96jxhrcDePRuXR0zFeryRuMuqgpOgUl63hau7j0Rch0epyDiGXc2AbUCiPw1Bz0Sn_lIIY6LNt3iEvsv0T8ayfSTjXUEnAxo4SnB2gEESmt7tI_plfUk39wXod5uOGUKoCVGSB8F__bxP5BvM_UgcEJi64Z0cMaUF79BFi0SDSnk_TXxZNcMs5BPeGgVCBplb-lR86X5JPIVAdBI2C49vjTkz5y4Fqjo9c3ZcABiGbdYKXaBKEtngbmtTVzRizGvRiNOjaerVZbesU1Zrnp3sMahcbSjo69QLTss";
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