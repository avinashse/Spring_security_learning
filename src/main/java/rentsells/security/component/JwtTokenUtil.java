package rentsells.security.component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import rentsells.security.model.Error;
import rentsells.security.model.ErrorConstant;
import rentsells.security.model.JwtValidation;

@Component
public class JwtTokenUtil implements Serializable {
	private static final long serialVersionUID = -2550185165626007488L;
	
	Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
	
	@Value("${jwt.expirationMs}")
	public Integer tokenValidity;
	@Value("${jwt.secret}")
	private String secret;

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// generate token for user
	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		String token =doGenerateToken(claims, username); 
		TokenManager.getInstance().getTokenManagerMap().put(token, username);
		return token;
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	
	public ResponseEntity<?> validateToken(JwtValidation jwtValidation)
	{
		Error apiError = null;
		
		System.out.println("==jwtValidation starts=");
		logger.info(jwtValidation.toString());
		System.out.println("==jwtValidation ends=");
		
		try
		{
			// Check if input token is not null
			if ( jwtValidation.getToken() == null ) {
				apiError = new Error(ErrorConstant.EMPTY_TOKEN.getErrorCode(),ErrorConstant.EMPTY_TOKEN.getErrorMsg().toString());
			}

			// compare from map the existance of token
			else if ( TokenManager.getInstance().getTokenManagerMap().containsKey(jwtValidation.getToken()))
			{
				// get username from claim settings for input token
				String username = getUsernameFromToken(jwtValidation.getToken());
				
				//compare username of the token from stored map and check the validity of the token
				if( username.equals(TokenManager.getInstance().getTokenManagerMap().get(jwtValidation.getToken()))
						&& !isTokenExpired(jwtValidation.getToken()) ) {
					apiError = new Error(ErrorConstant.SUCCESS.getErrorCode(),ErrorConstant.SUCCESS.getErrorMsg().toString());
					return new ResponseEntity<Error>(apiError,  HttpStatus.OK);
				}
			}
			else {
				apiError = new Error(ErrorConstant.INVALID_TOKEN.getErrorCode(),ErrorConstant.INVALID_TOKEN.getErrorMsg().toString());				
			}

		} catch (IllegalArgumentException e) {
			System.out.println("Unable to get JWT Token");
			apiError = new Error(ErrorConstant.INVALID_TOKEN.getErrorCode(),ErrorConstant.INVALID_TOKEN.getErrorMsg().toString());
			return new ResponseEntity<Error>(apiError,  HttpStatus.BAD_REQUEST);			
		} catch (ExpiredJwtException e) {
			System.out.println("JWT Token has expired" + e);
			apiError = new Error(ErrorConstant.TOKEN_IS_EXPIRED.getErrorCode(),ErrorConstant.TOKEN_IS_EXPIRED.getErrorMsg().toString());
			return new ResponseEntity<Error>(apiError,  HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Error>(apiError,  HttpStatus.BAD_REQUEST);
	}
	
	// validate token
	public Boolean validateToken(String token, String currentUser) {
		final String username = getUsernameFromToken(token);
		return (username.equals(currentUser) && !isTokenExpired(token));
	}
}
