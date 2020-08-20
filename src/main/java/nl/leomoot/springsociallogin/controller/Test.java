package nl.leomoot.springsociallogin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<String> checklogin(OAuth2AuthenticationToken authentication) {
        OAuth2User user = authentication.getPrincipal();
        String name = user.getAttribute("name");
       
        return ResponseEntity.ok(String.format("Hello %s, you were authenticated using %s -> %s", name, 
                authentication.getAuthorizedClientRegistrationId(), user));
    }
}
