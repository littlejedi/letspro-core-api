package com.letspro.core.api.auth;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class SimplePrincipal implements Principal {
    
    private String username;
    private List<String> roles = new ArrayList<String>();
    
    public SimplePrincipal(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public List<String> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getName() {
        return username;
    }
}
