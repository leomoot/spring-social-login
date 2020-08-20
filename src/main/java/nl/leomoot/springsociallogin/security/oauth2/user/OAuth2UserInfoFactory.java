package nl.leomoot.springsociallogin.security.oauth2.user;

import java.util.Map;
import java.util.stream.Stream;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import nl.leomoot.springsociallogin.exception.OAuth2AuthenticationProcessingException;

/**
 * Factory class, responsible for determing the OAuth2 provider
 */
public class OAuth2UserInfoFactory {

    OAuth2UserInfoFactory() {
        // nop $90
    }
    
    public static OAuth2UserInfo getOAuth2UserInfo(final String registrationId, final Map<String, Object> attributes) {
        
        CommonOAuth2Provider authProvider = getOAuth2Prodiver(registrationId);
        switch (authProvider) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            default:
                throw new OAuth2AuthenticationProcessingException("Login using " + registrationId + " is not supported.");
        }
    }
    
    public static CommonOAuth2Provider getOAuth2Prodiver(String registrationId) {
        return Stream.of(CommonOAuth2Provider.values())
                .filter(p -> p.toString().equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow();
    }
}
