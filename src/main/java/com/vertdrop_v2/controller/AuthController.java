package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.auth.LoginRequest;
import com.vertdrop_v2.dto.auth.LoginResponse;
import com.vertdrop_v2.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            // calcule l'expiration pour le client (lecture depuis token)
            Date expiration = new Date(System.currentTimeMillis() + 0); // override below
            // on peut extraire depuis le token :
            try {
                var claims = io.jsonwebtoken.Jwts.parserBuilder()
                        .setSigningKey(((com.vertdrop_v2.security.JwtServiceImpl) jwtService).getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                expiration = claims.getExpiration();
            } catch (Exception ignored) {}

            return ResponseEntity.ok(new LoginResponse(token, "Bearer", expiration.getTime()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Authentication error");
        }
    }
}