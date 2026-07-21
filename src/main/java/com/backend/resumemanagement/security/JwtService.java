package com.backend.resumemanagement.security;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Service
public class JwtService {

        private final String secretKey = "mySecretKeyForResumeManagementSystem123456789";

        private Key getSigningKey() {
                return Keys.hmacShaKeyFor(secretKey.getBytes());
        }

        // Generate Token
        public String generateToken(
                        String email,
                        String role) {

                return Jwts.builder()

                                .setSubject(email)

                                .claim(
                                                "role",
                                                role)

                                .setIssuedAt(
                                                new Date())

                                .setExpiration(
                                                new Date(
                                                                System.currentTimeMillis()
                                                                                + 1000 * 60 * 60))

                                .signWith(
                                                getSigningKey())

                                .compact();
        }

        // Get Email
        public String getEmail(
                        String token) {

                return Jwts.parser()

                                .setSigningKey(
                                                getSigningKey())

                                .build()

                                .parseClaimsJws(
                                                token)

                                .getBody()

                                .getSubject();
        }

        // Get Role
        public String getRole(
                        String token) {

                return Jwts.parser()

                                .setSigningKey(
                                                getSigningKey())

                                .build()

                                .parseClaimsJws(
                                                token)

                                .getBody()

                                .get(
                                                "role",
                                                String.class);
        }
}