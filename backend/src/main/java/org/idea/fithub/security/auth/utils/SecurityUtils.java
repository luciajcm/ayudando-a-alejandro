package org.idea.fithub.security.auth.utils;

import org.idea.fithub.security.auth.domain.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }


    public static Long getCurrentUserId() {
        CustomUserDetails user = getCurrentUser();
        return (user != null) ? user.getId() : null;
    }

    public static String getCurrentUsername() {
        CustomUserDetails user = getCurrentUser();
        return (user != null) ? user.getUsername() : null;
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        final String authorityName = "ROLE_" + role.toUpperCase();

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(authorityName));
    }

    public static boolean isCurrentUserOrAdmin(Long targetUserId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null || targetUserId == null) {
            return false;
        }

        // Comprueba si el ID del recurso es igual al ID del usuario logueado
        boolean isOwner = targetUserId.equals(currentUserId);

        // Comprueba si es Admin
        boolean isAdmin = hasRole("ADMIN");

        return isOwner || isAdmin;
    }
}