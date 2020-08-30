package nl.leomoot.springsociallogin.security.oauth2;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOidcUserService extends OidcUserService {

    private CustomOAuth2UserService customOAuth2UserService;

    @Override
    public OidcUser loadUser(final OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {        
        var oidcUser = super.loadUser(oidcUserRequest);

        var principal = customOAuth2UserService.buildPrincipal(oidcUser, oidcUserRequest.getClientRegistration().getRegistrationId());
        principal.setClaims(oidcUser.getClaims());
        principal.setIdToken(oidcUser.getIdToken());
        principal.setUserInfo(oidcUser.getUserInfo());

        return principal;      
    }
}
