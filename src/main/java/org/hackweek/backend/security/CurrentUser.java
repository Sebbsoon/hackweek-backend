package org.hackweek.backend.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AuthenticationCredentialsNotFoundException("Missing or invalid JWT authentication");
        }
        return jwt;
    }

    public String getClerkUserId() {
        String sub = getJwt().getClaimAsString("sub");
        if (sub == null || sub.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("JWT does not contain sub claim");
        }
        return sub;
    }

    public String getClaim(String name) {
        return getJwt().getClaimAsString(name);
    }
}