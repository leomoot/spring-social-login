package nl.leomoot.springsociallogin.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
    private final OidcUserService oidcUserService;
    
    public SecurityConfig(final OidcUserService oidcUserService) {
        this.oidcUserService = oidcUserService;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {        
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .oauth2Login()
            .defaultSuccessUrl("/oauth2LoginSuccess")
            .userInfoEndpoint(u -> u.oidcUserService(oidcUserService)); // OpenID
    }
}
