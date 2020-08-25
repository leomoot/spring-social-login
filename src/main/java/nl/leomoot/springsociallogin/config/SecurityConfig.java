package nl.leomoot.springsociallogin.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import lombok.RequiredArgsConstructor;
import nl.leomoot.springsociallogin.security.oauth2.CustomOAuth2UserService;
import nl.leomoot.springsociallogin.security.oauth2.CustomOidcUserService;

@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
    private final CustomOidcUserService customOidcUserService;
    private final CustomOAuth2UserService customOAuth2UserService;
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .cors(c -> c
                    .and()
            )
            .sessionManagement(s -> s
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )    
            .csrf(c -> c
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .formLogin(f -> f
                    .disable()
            )
            .httpBasic(h -> h
                    .disable()
            )            
            .authorizeRequests(a -> a
                    .antMatchers("/", "/error", "/webjars/**").permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )        
            .oauth2Login(o -> o
                    .userInfoEndpoint(u -> u
                            .userService(customOAuth2UserService)
                            .oidcUserService(customOidcUserService)
                    )
            );
         // @formatter:on
    }
}
