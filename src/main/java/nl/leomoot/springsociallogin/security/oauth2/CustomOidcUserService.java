package nl.leomoot.springsociallogin.security.oauth2;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import nl.leomoot.springsociallogin.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    
    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {        
        OidcUser oidcUser = super.loadUser(oidcUserRequest);
      
        try {
            return OAuth2Helper.process(userRepository, oidcUserRequest).withOidc2User(oidcUser);
        } catch (AuthenticationException aEx) {           
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(aEx.getMessage(), aEx.getCause());
        }
    }
}