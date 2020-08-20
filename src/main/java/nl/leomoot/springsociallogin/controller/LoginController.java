package nl.leomoot.springsociallogin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
   
    @GetMapping("/oauth2LoginSuccess")
    public ResponseEntity<String> checklogin(OAuth2AuthenticationToken authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2 = (OAuth2AuthenticationToken) auth;
            Object details = auth.getDetails();
          
        }
        return ResponseEntity.ok(authentication.getAuthorizedClientRegistrationId());
    }
}
