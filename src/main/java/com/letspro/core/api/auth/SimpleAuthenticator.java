package com.letspro.core.api.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import com.google.common.base.Optional;

public class SimpleAuthenticator implements Authenticator<BasicCredentials, SimplePrincipal> {
    
    private static final String PASS = "coreapi!123";
    
    @Override
    public Optional<SimplePrincipal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if (PASS.equals(credentials.getPassword())) {
            return Optional.of(new SimplePrincipal(credentials.getUsername()));
        }
        return Optional.absent();
    }
}
