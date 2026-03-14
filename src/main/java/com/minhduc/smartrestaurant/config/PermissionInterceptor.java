package com.minhduc.smartrestaurant.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.minhduc.smartrestaurant.util.error.PermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requiredPermission = httpMethod + " " + path;

        boolean isAllow = authentication != null
                && authentication.getAuthorities() != null
                && authentication.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .anyMatch(requiredPermission::equals);

        if (!isAllow) {
            throw new PermissionException("Bạn không có quyền truy cập endpoint này");
        }

        return true;
    }

}