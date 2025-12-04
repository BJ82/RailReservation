package com.rail.app.railreservation.security.util;

import com.rail.app.railreservation.security.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "lkjasd-oiuqwewqe-12414-987897-wioruweitu";
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    @Autowired
    private static UserService userService;

    public static String generateToken(String username){

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean isJWTValid(String jwt){

        boolean isJWTValid = true;

        if(isNonEmpty(jwt) && isNotNull(jwt)) {

            JwtParser jwtParser = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build();

            Jws jwtToken = (Jws) jwtParser.parse(jwt);
            Claims bodyJwt = (Claims) jwtToken.getBody();


            //TODO Check username against DB
            String userNameFrmJwt = bodyJwt.getSubject();
            UserDetails userDetails = userService.loadUserByUsername(userNameFrmJwt);

            if(userDetails !=null && isNotNull(userDetails.getUsername())){
                if (!userDetails.getUsername().equals(userNameFrmJwt))
                    isJWTValid = false;
            }


            //TODO Check expiration
            Date expiryDate = bodyJwt.getExpiration();
            if(expiryDate != null){
                if (!expiryDate.after(new Date()))
                    isJWTValid = false;
            }
        }
        return isJWTValid;
    }

    public static String getUserName(String token){

        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        Jws jwtToken = (Jws) jwtParser.parse(token);
        Claims bodyJwt = (Claims) jwtToken.getBody();

        return bodyJwt.getSubject();
    }

    private static boolean isNonEmpty(String str){

        boolean isNonEmpty = true;
        isNonEmpty = !("".equals(str));
        return isNonEmpty;
    }

    private static boolean isNotNull(String str){

        return (str == null)?true:false;

    }

}

