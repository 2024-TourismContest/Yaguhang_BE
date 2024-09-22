package _4.TourismContest.oauth.application;

import _4.TourismContest.oauth.domain.AuthProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final AuthProvider provider;

    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials, AuthProvider provider) {
        super(principal, credentials);
        this.provider = provider;
    }

    public AuthProvider getProvider() {
        return provider;
    }
}

