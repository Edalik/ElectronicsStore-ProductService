package ru.edalik.electronics.store.product.service.service.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.security.oauth2.core.oidc.StandardClaimNames.EMAIL;
import static org.springframework.security.oauth2.core.oidc.StandardClaimNames.PREFERRED_USERNAME;

@Slf4j
@Service
@AllArgsConstructor
public class UserContextService {

    public Jwt getPrincipal() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("No authentication found");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        } else {
            throw new IllegalStateException("No principal found");
        }
    }

    public UUID getUserGuid() {
        return UUID.fromString(getPrincipal().getSubject());
    }

    public String getPreferredUserName() {
        return getPrincipal().getClaimAsString(PREFERRED_USERNAME);
    }

    public String getEmail() {
        return getPrincipal().getClaimAsString(EMAIL);
    }

    public String getTokenString() {
        return getPrincipal().getTokenValue();
    }

}