package com.project.shopapp.components;

import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
//    tg ket thuc token
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private int expiration; // save to enviroment variable: luu thanh bien moi truong trong file yml
//    ham tao token
    public String generateToken(User user) throws  Exception{
        // properties => claims (cac thuoc tinh cua user)
        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey();
        // truyền các tham số sau vào token (clame)
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("userId", user.getId());
        try {
            String token = Jwts.builder()
                    .setClaims(claims) // how to extract claims from this? (1)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
//                    khi sinh ra token can co 1 key de bao mat, de dich nguoc lai ra cac claims
//                    truyen secretKey de co the lay ra cac claims trong token, tuc de xem trong token chua cai j
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            // You can use Logger, instead System.out.println
            throw new InvalidParamException("Cannot create jwt token, error: " + e.getMessage());
        }
    }
//    ma hoa va chuyen doi tu secretKet sang dang Key
    private Key getSignInKey() {
//        Keys.hmacShaKeyFor(Decoders.BASE64.decode("neMfzBRCIcXhE5k6LQXmHa1gFZCXq4fUIvlxzDHrCh8="))
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; //256 bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }
//    ham de extract claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // truyen dung signInKey dung de ma hoa thi moi lay duoc cac claims
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims); // can thuoc tinh gi thi truyen vao claimsResolver()
    }
//    check expiration: check xem token da het han chua
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
//    lay phoneNumber tu token
    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }
//  check username co token con han khong va username co trung voi phone number truyen vao?
    public boolean validateToken(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
