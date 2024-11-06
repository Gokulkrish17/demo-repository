package com.Sadetechno.api_getway.filter;

import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/verifyOtp",
            "/api/auth/otp",
            "/api/auth/forgot-password",
            "/api/auth/reset-password"
    );

    private final String secretKey = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3"; // Replace with your actual secret key

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Bypass filter for excluded paths
            if (isExcludedPath(path)) {
                return chain.filter(exchange);
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

            // Check if the "Authorization" header exists and starts with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Extract token from header
            String token = authHeader.substring(7);

            // Validate the token
            if (!isTokenValid(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // If valid, proceed with the request
            return chain.filter(exchange);
        };
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isTokenValid(String token) {
        try {
            // Decode the token with Apache Commons Codec
            byte[] decodedKey = Base64.decodeBase64(secretKey);

            // Parse the JWT and validate it
            Claims claims = Jwts.parser()
                    .setSigningKey(decodedKey) // Use the decoded secret key
                    .parseClaimsJws(token)
                    .getBody();

            // Additional checks can be added here (e.g., check claims, roles, etc.)
            return !claims.getExpiration().before(new java.util.Date()); // Ensure token is not expired
        } catch (SignatureException | ExpiredJwtException e) {
            // Token is invalid or expired
            return false;
        } catch (Exception e) {
            // Other exceptions, token may not be valid
            return false;
        }
    }

    public static class Config {
        // Configuration properties for the filter can be added here if needed
    }
}
