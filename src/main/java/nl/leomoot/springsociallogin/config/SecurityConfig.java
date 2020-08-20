package nl.leomoot.springsociallogin.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import nl.leomoot.springsociallogin.security.oauth2.CustomOAuth2UserService;
import nl.leomoot.springsociallogin.security.oauth2.CustomOidcUserService;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
     
    private final OidcUserService oidcUserService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;
    
    public SecurityConfig(final CustomOAuth2UserService oauth2UserService, final CustomOidcUserService oidcUserService) {
        this.oidcUserService = oidcUserService;
        this.oauth2UserService = oauth2UserService;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .httpBasic(b -> b.disable())
            .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .oauth2Login()
            .userInfoEndpoint(u -> u.userService(oauth2UserService))
            .userInfoEndpoint(u -> u.oidcUserService(oidcUserService));
    }
}
