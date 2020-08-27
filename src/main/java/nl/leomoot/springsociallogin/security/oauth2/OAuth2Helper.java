package nl.leomoot.springsociallogin.security.oauth2;

import java.util.Map;
import java.util.Optional;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import nl.leomoot.springsociallogin.exception.OAuth2AuthenticationProcessingException;
import nl.leomoot.springsociallogin.model.User;
import nl.leomoot.springsociallogin.repository.UserRepository;
import nl.leomoot.springsociallogin.security.oauth2.user.AmazonOAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.GithubOAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.GoogleOAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.OAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.UserPrincipal;

@RequiredArgsConstructor
public class OAuth2Helper {
    
    private final UserRepository userRepository;        
    private final String registrationId;
    private final ClientRegistrationRepository clientRegistrationRepository;
    
    public static OAuth2Helper process(final UserRepository userRepository, final String registrationId, 
            final ClientRegistrationRepository clientRegistrationRepository) {
        return new OAuth2Helper(userRepository, registrationId, clientRegistrationRepository);
    }
    
    public OidcUser withOidc2User(final OidcUser openIdUser) {                
        return UserPrincipal.create(createOrUpdateDatabaseUser(openIdUser.getAttributes()), openIdUser);
    }

    public OAuth2User withOAuth2User(final OAuth2User oAuth2User) {         
        return UserPrincipal.create(createOrUpdateDatabaseUser(oAuth2User.getAttributes()), oAuth2User);
    }
   
    private User createOrUpdateDatabaseUser(final Map<String, Object> attributes) {
        var userInfo = getUserInfo(registrationId, attributes);
        if(StringUtils.isBlank(userInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not set in given UserInfo.");
        }

        User user;
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(getProvider(registrationId).getRegistrationId())) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            
            return updateExistingUser(user, userInfo);
        }
        
        return registerNewUser(userInfo);
    }
    
    private User registerNewUser(final OAuth2UserInfo userInfo) {
        var user = new User();

        user.setProvider(getProvider(registrationId).getRegistrationId());
        user.setProviderId(userInfo.getId());
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setImageUrl(userInfo.getImageUrl());
        
        return userRepository.save(user);
    }

    private User updateExistingUser(final User existingUser, final OAuth2UserInfo userInfo) {
        existingUser.setName(userInfo.getName());
        existingUser.setImageUrl(userInfo.getImageUrl());
        
        return userRepository.save(existingUser);
    }
    
    /**
     * I *hate* the below code...
     */
    private OAuth2UserInfo getUserInfo(final String registrationId, final Map<String, Object> attributes) {
        var clientRegistration = getProvider(registrationId);
        
        switch (clientRegistration.getRegistrationId()) {
            case "google": return new GoogleOAuth2UserInfo(attributes);
            case "github": return new GithubOAuth2UserInfo(attributes);
            case "amazon": return new AmazonOAuth2UserInfo(attributes);
            default:
                throw new OAuth2AuthenticationProcessingException("Login using " + registrationId + " is not supported.");
        }
    }
    
    private ClientRegistration getProvider(final String registrationId) {
        return clientRegistrationRepository.findByRegistrationId(registrationId);
    }
}
