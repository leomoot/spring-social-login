package nl.leomoot.springsociallogin.security.oauth2.user;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.ToString;
import nl.leomoot.springsociallogin.model.User;

@SuppressWarnings("serial")
@ToString
public class UserPrincipal implements OAuth2User, OidcUser, UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private OidcIdToken idToken;
    private Map<String, Object> claims;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public static UserPrincipal create(User user, OAuth2UserInfo oAuth2UserInfo) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(oAuth2UserInfo.getAttributes());
        
        return userPrincipal;
    }

    public static UserPrincipal create(User user, OAuth2UserInfo oAuth2UserInfo, OidcUser oidcUser) {      
        UserPrincipal userPrincipal = UserPrincipal.create(user, oAuth2UserInfo);
        userPrincipal.setClaims(oidcUser.getClaims());
        userPrincipal.setIdToken(oidcUser.getIdToken());
        
        return userPrincipal;
    }
    
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("name"));
    }

    @Override
    public Map<String, Object> getClaims() {
        return claims;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return new OidcUserInfo(this.attributes);
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }
    
    public void setIdToken(OidcIdToken idToken) {
        this.idToken = idToken;
    }

    public void setClaims(Map<String, Object> claims) {
        this.claims = claims;
    }
}
