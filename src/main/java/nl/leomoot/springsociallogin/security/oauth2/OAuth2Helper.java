package nl.leomoot.springsociallogin.security.oauth2;

import java.util.Optional;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import nl.leomoot.springsociallogin.exception.OAuth2AuthenticationProcessingException;
import nl.leomoot.springsociallogin.model.User;
import nl.leomoot.springsociallogin.repository.UserRepository;
import nl.leomoot.springsociallogin.security.oauth2.user.OAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.OAuth2UserInfoFactory;
import nl.leomoot.springsociallogin.security.oauth2.user.UserPrincipal;

public class OAuth2Helper {
    
    private final UserRepository userRepository;
    private final String registrationId;    
    private OAuth2UserInfo oAuth2UserInfo;
    
    public OAuth2Helper(UserRepository userRepository, OAuth2UserRequest oAuth2UserRequest) {
        this.userRepository = userRepository;
        this.registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
    }
    
    public static OAuth2Helper process(UserRepository userRepository, OAuth2UserRequest oAuth2UserRequest) {
        return new OAuth2Helper(userRepository, oAuth2UserRequest);
    }
    
    public OidcUser withOidc2User(OidcUser oidcUser) {        
        oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oidcUser.getAttributes());
        
        return UserPrincipal.create(handleDatabaseUser(), oidcUser.getClaims(), oAuth2UserInfo, oidcUser.getIdToken());
    }

    public OAuth2User withOAuth2User(OAuth2User oAuth2User) {
        oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        return UserPrincipal.create(handleDatabaseUser(), oAuth2UserInfo);
    }
    
    private User handleDatabaseUser() {
        if(StringUtils.isBlank(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found in UserInfo.");
        }

        User user;
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(OAuth2UserInfoFactory.getOAuth2Provider(registrationId))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user);
        } else {
            user = registerNewUser();
        }
        
        return user;
    }
    
    private User registerNewUser() {
        User user = new User();

        user.setProvider(OAuth2UserInfoFactory.getOAuth2Provider(registrationId));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        
        return userRepository.save(existingUser);
    }   
}
