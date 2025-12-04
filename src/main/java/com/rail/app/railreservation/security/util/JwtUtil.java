package com.rail.app.railreservation.security.util;

import com.rail.app.railreservation.security.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
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

        boolean isJWTValid = false;

        if(isNonEmpty(jwt) && isNotNull(jwt)){

            try{
                JwtParser jwtParser = Jwts.parserBuilder().build();
                Jwt jwtToken = jwtParser.parse(jwt);
                Claims claims = (Claims) jwtToken.getBody();
            }
            catch(SignatureException signatureEx){

            }


            //TODO Verify Signature
            JwsHeader header = (JwsHeader) jwtToken.getHeader();
            String algo = header.getCompressionAlgorithm();

            //TODO Check username against DB
            String userNameFrmJwt = claims.getSubject();
            UserDetails userDetails = userService.loadUserByUsername(userNameFrmJwt);
            if(userDetails.getUsername().equals(userNameFrmJwt))
                isJWTValid = true;

            //TODO Check expiration
            Date expiryDate = claims.getExpiration();
            if(expiryDate.after(new Date()))
                isJWTValid = true;
        }

        return isJWTValid;
    }

    private static boolean isNonEmpty(String str){

        boolean isNonEmpty = true;
        isNonEmpty = !("".equals(str));
        return isNonEmpty;
    }

    private static boolean isNotNull(String str){

        return (str == null)?true:false;

    }

    private static boolean verifySignature(String jwt) throws Exception {

        boolean verifySignature = false;
        SignatureAlgorithm sa = SignatureAlgorithm.HS256;
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(), sa.getJcaName());

        JwtParser jwtParser = Jwts
                .verifyWith(secretKeySpec)
                .build();
        try {

            jwtParser.parse(jwt);
            verifySignature = true;

        } catch (Exception e) {
            throw new Exception("Could not verify JWT token integrity!", e);
        }
        finally {
            return verifySignature;
        }
    }
    private String getHeader(String jwt){

        String[] jwtPart = jwt.split("\\.");

        return new String(getBase64Decoder().decode(jwtPart[0]));
    }

    private String getPayload(String jwt){

        String[] jwtPart = jwt.split("\\.");

        return new String(getBase64Decoder().decode(jwtPart[1]));
    }

    private String get

    Base64.Decoder getBase64Decoder(){

        return Base64.getUrlDecoder();
    }
}

