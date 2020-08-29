package nl.leomoot.springsociallogin.security.oauth2.user;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public UserPrincipal(final Long id, final String email, final Collection<? extends GrantedAuthority> authorities, 
            final Map<String, Object> attributes) {

        this.id = id;
        this.email = email;     
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public static UserPrincipal create(final User user, final OidcUser openIdUser) {   
        var principal = new UserPrincipal(user.getId(), user.getEmail(), 
                openIdUser.getAuthorities(), openIdUser.getAttributes());
        principal.setClaims(openIdUser.getClaims());
        principal.setIdToken(openIdUser.getIdToken());
        
        return principal;
    }

    public static UserPrincipal create(final User user, final OAuth2User ouath2User) {   
        return new UserPrincipal(user.getId(), user.getEmail(), 
                ouath2User.getAuthorities(), ouath2User.getAttributes());
    }
    
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
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

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }
}
