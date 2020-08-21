package nl.leomoot.springsociallogin.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import lombok.RequiredArgsConstructor;
import nl.leomoot.springsociallogin.security.oauth2.CustomOAuth2UserService;
import nl.leomoot.springsociallogin.security.oauth2.CustomOidcUserService;
import nl.leomoot.springsociallogin.security.oauth2.OAuth2AuthenticationSuccessHandler;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
     
    private final CustomOidcUserService customOidcUserService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .httpBasic(b -> b.disable())
            .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .oauth2Login()
            .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
            .userInfoEndpoint(u -> u.oidcUserService(customOidcUserService)) // Open ID
            .successHandler(successHandler)
       //     .failureHandler(authenticationFailureHandler)
            ;
            
    }
}
