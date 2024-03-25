package com.chat.chatservice.util;

import com.chat.chatservice.model.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenUtil {
    private static final String SECRET_KEY = "yJhbGciOiJIUzUxMiJ9.eyJzdWIiOiITY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaW.SflKxwRJSMeKKFMeJf36POk6yJV_adQssw5c";
    private static final byte[] SECRET_KEY_BYTES = SECRET_KEY.getBytes();
    private static final long EXPIRATION_TIME = 365L * 24 * 60 * 60 * 1000; // 1 year in milliseconds

    public static String generateToken(String email, String username, UserType userType) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        return Jwts.builder()
                .claim("email", email)
                .claim("username", username)
                .claim("userType", userType.name())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY_BYTES)
                .compact();
    }

    public static String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY_BYTES)
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public static String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY_BYTES)
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    public static UserType getUserTypeFromToken(String token) {
        String userTypeString = Jwts.parser()
                .setSigningKey(SECRET_KEY_BYTES)
                .parseClaimsJws(token)
                .getBody()
                .get("userType", String.class);

        return UserType.valueOf(userTypeString);
    }

    public static boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY_BYTES).parseClaimsJws(token);
            System.out.println("Token successfully parsed and validated.");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to parse and validate token:");
            e.printStackTrace();
            return false;
        }
    }
}