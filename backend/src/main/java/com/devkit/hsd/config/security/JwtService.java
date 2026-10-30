package com.devkit.hsd.config.security;

import com.devkit.hsd.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expiration-ms}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshTokenExpiration;


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof User user) {
            claims.put("communityId", user.getCommunity() != null ? user.getCommunity().getId() : null);
            claims.put("systemRole", user.getSystemRole().name());
            claims.put("role", user.getRole() != null ? user.getRole().name() : null);
            claims.put("userId", user.getId());
        }


        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        claims.put(
                "roles",
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );

        return buildToken(claims, userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractCommunityId(String token){
        Object communityId = extractClaim(token, claims -> {
            return claims.get("communityId");
        });

        if (communityId != null){
            return  Long.valueOf(communityId.toString());
        } else {
            return null;
        }
    }

    public String extractSystemRole  (String token){
        Object systemRole = extractClaim(token, claims -> {
            return claims.get("systemRole");
        });

        if (systemRole != null){
            return (systemRole.toString());
        } else {
            return null;
        }
    }
    public Long extractUserId (String token){
        Object userId = extractClaim(token, claims -> {
            return claims.get("userId");
        });

        if (userId != null){
            return  Long.valueOf(userId.toString());
        } else {
            return null;
        }
    }

    public String extractRole  (String token){
        Object role = extractClaim(token, claims -> {
            return claims.get("role");
        });

        if (role != null){
            return (role.toString());
        } else {
            return null;
        }
    }


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }
}
