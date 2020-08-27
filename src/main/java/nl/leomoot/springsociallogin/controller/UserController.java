package nl.leomoot.springsociallogin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {
    
    
    private final ObjectMapper objectMapper;
   
    @GetMapping("/me")
    public ResponseEntity<String> checklogin(final OAuth2AuthenticationToken authentication) throws JsonProcessingException {
        var user = authentication.getPrincipal();
        
        return ResponseEntity.ok(String.format("Hello %s, you were authenticated using %s -> %s", user.getName(), 
                authentication.getAuthorizedClientRegistrationId(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user)));
    }
}
