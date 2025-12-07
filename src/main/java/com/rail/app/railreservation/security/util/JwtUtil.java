package com.rail.app.railreservation.security.util;

import com.rail.app.railreservation.security.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String SECRET;
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    @Value("${jwt.expiry.time}")
    private long EXPIRATION_TIME;

    @Autowired
    private static UserService userService;

    public String generateToken(String username){

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isJWTValid(String jwt){

        boolean isJWTValid = true;

        if(isNonEmpty(jwt) && isNotNull(jwt)) {

            JwtParser jwtParser = Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build();

            Jws jwtToken = (Jws) jwtParser.parse(jwt);
            Claims bodyJwt = (Claims) jwtToken.getBody();


            //Check username against DB
            String userNameFrmJwt = bodyJwt.getSubject();
            UserDetails userDetails = userService.loadUserByUsername(userNameFrmJwt);

            if(userDetails !=null && isNotNull(userDetails.getUsername())){
                if (!userDetails.getUsername().equals(userNameFrmJwt))
                    isJWTValid = false;
            }


            //Check expiration
            Date expiryDate = bodyJwt.getExpiration();
            if(expiryDate != null){
                if (!expiryDate.after(new Date()))
                    isJWTValid = false;
            }
        }
        return isJWTValid;
    }

    public String getUserName(String token){

        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        Jws jwtToken = (Jws) jwtParser.parse(token);
        Claims bodyJwt = (Claims) jwtToken.getBody();

        return bodyJwt.getSubject();
    }

    private boolean isNonEmpty(String str){

        boolean isNonEmpty = true;
        isNonEmpty = !("".equals(str));
        return isNonEmpty;
    }

    private boolean isNotNull(String str){

        return (str == null)?true:false;

    }

}

