package nl.leomoot.springsociallogin.controller;


import javax.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class LoginController {
    
    @Inject
    private ObjectMapper objectMapper;
   
    @GetMapping("/oauth2LoginSuccess")
    public ResponseEntity<String> checklogin(OAuth2AuthenticationToken authentication) throws JsonProcessingException {
        OAuth2User user = authentication.getPrincipal();
        String name = user.getAttribute("name");
        
        return ResponseEntity.ok(String.format("Hello %s, you were authenticated using %s -> %s", name, 
                authentication.getAuthorizedClientRegistrationId(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user)));
    }
}
