package com.rail.app.railreservation.login.service;

import com.rail.app.railreservation.login.dto.LoginRequest;
import com.rail.app.railreservation.login.dto.LoginResponse;
import com.rail.app.railreservation.security.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private AuthenticationManager authenticationManager;


    public LoginService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse login(LoginRequest loginRequest) throws AuthenticationException {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        Authentication authentication ;

        authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username,password));

        return new LoginResponse(JwtUtil.generateToken(username),"Logged In");

    }
}
