package nl.leomoot.springsociallogin.security.oauth2.user;

import java.util.Map;

public class AmazonOAuth2UserInfo extends OAuth2UserInfo {

    public AmazonOAuth2UserInfo(final Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("user_id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
