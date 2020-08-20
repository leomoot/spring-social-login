package nl.leomoot.springsociallogin.security.oauth2;

import java.util.Optional;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import nl.leomoot.springsociallogin.exception.OAuth2AuthenticationProcessingException;
import nl.leomoot.springsociallogin.model.User;
import nl.leomoot.springsociallogin.repository.UserRepository;
import nl.leomoot.springsociallogin.security.oauth2.user.OAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.OAuth2UserInfoFactory;

@RequiredArgsConstructor
@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    
    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(oidcUserRequest);
        try {
            return processOAuth2User(oidcUserRequest, oidcUser);
        } catch (AuthenticationException aEx) {           
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(aEx.getMessage(), aEx.getCause());
        }
    }

    private OidcUser processOAuth2User(OidcUserRequest oidcUserRequest, OidcUser oidcUser) {
        
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oidcUserRequest.getClientRegistration().getRegistrationId(), oidcUser.getAttributes());
        if(StringUtils.isBlank(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found.");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(OAuth2UserInfoFactory.getOAuth2Provider(oidcUserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oidcUserRequest, oAuth2UserInfo);
        }
        
        return oidcUser;
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(OAuth2UserInfoFactory.getOAuth2Provider(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        
        return userRepository.save(existingUser);
    }
}