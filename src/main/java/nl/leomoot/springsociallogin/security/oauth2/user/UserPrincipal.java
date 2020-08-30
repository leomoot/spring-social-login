package nl.leomoot.springsociallogin.security.oauth2.user;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nl.leomoot.springsociallogin.model.User;

@SuppressWarnings("serial")
@Getter @Setter @RequiredArgsConstructor
public class UserPrincipal implements OidcUser, UserDetails, CredentialsContainer {

    private final User user;
    private String name;
    private OidcUserInfo userInfo;
    private OidcIdToken idToken;
    private Map<String, Object> claims;
    private Map<String, Object> attributes;
    private Set<String> roles;
    
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public void eraseCredentials() {
        attributes = null;
        claims = null;
        userInfo = null;
        idToken = null;
    }

    @Override
    public String getPassword() {
        return null;
    }
    
    @Override
    public String getUsername() {
        return user.getName();
    }
   
    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }  
}
