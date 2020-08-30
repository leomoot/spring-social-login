package nl.leomoot.springsociallogin.security.oauth2;

import java.util.Map;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import nl.leomoot.springsociallogin.model.User;
import nl.leomoot.springsociallogin.repository.UserRepository;
import nl.leomoot.springsociallogin.security.oauth2.user.AmazonOAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.GithubOAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.GoogleOAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.OAuth2UserInfo;
import nl.leomoot.springsociallogin.security.oauth2.user.UserPrincipal;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oAuth2User = super.loadUser(userRequest);
     
        return buildPrincipal(oAuth2User, userRequest.getClientRegistration().getRegistrationId());           
    }
    
    public UserPrincipal buildPrincipal(final OAuth2User oath2User, final String providerName) {
        var attributes = oath2User.getAttributes();
        var userInfo = getUserInfo(providerName, attributes);
        
        // is email filled in?        
        if(StringUtils.isBlank(userInfo.getEmail())) {
            throw new AuthenticationServiceException("There's no email address available in the given UserInfo.");
        }
                    
        User user;
        var userOptional = userRepository.findByEmail(userInfo.getEmail());
        if(userOptional.isPresent()) {
            user = userOptional.get();            
            if(!user.getProvider().equals(providerName)) {
                throw new AuthenticationServiceException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
        }
       
        user = registerOrUpdateUser(userInfo, providerName);
        
        var principal = new UserPrincipal(user);
        principal.setAttributes(attributes);
        principal.setName(oath2User.getName());

        return principal;
    }
    
    private User registerOrUpdateUser(final OAuth2UserInfo userInfo, final String providerName) {
        var user = new User();

        user.setProvider(providerName);
        user.setProviderId(userInfo.getId());
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setImageUrl(userInfo.getImageUrl());
        
        return userRepository.save(user);
    }
    
    private static OAuth2UserInfo getUserInfo(final String providerName, final Map<String, Object> attributes) {
       
        switch (providerName.toLowerCase()) {
            case "google": return new GoogleOAuth2UserInfo(attributes);
            case "github": return new GithubOAuth2UserInfo(attributes);
            case "amazon": return new AmazonOAuth2UserInfo(attributes);
            default:
                throw new AuthenticationServiceException("Login using " + providerName + " is not supported.");
        }
    }
}
