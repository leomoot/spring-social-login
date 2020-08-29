package nl.leomoot.springsociallogin.security.oauth2.user;

import java.util.Map;

/**
 * Base class providing basic methods used by OATH2 providers. Each provider 
 * will extend this class and -eventually- adds his own attributes.
 */
public abstract class OAuth2UserInfo {
 
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}
