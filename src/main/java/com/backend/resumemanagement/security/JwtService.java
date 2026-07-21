package com.backend.resumemanagement.security;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

        private final String secretKey = "mySecretKeyForResumeManagementSystem123456789";

        public String generateToken(
                        String email,
                        String role) {

                return Jwts.builder()
                                .setSubject(email)
                                .claim("role", role)
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(
                                                                System.currentTimeMillis()
                                                                                + 1000 * 60 * 60))
                                .signWith(
                                                SignatureAlgorithm.HS256,
                                                secretKey)
                                .compact();
        }

        public String getEmail(String token) {

                return Jwts.parser()
                                .setSigningKey(secretKey)
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .getSubject();
        }

        public String getRole(String token) {

                return Jwts.parser()
                                .setSigningKey(secretKey)
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .get("role", String.class);
        }
}