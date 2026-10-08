package com.mediconnect.config;

import com.mediconnect.model.User;
import com.mediconnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticatedUserInterceptor implements HandlerInterceptor {

    public static final String USER_ATTRIBUTE = AuthenticatedUserInterceptor.class.getName() + ".user";

    private final UserService userService;

    public AuthenticatedUserInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return false;
        }

        String rawToken = authorization.substring("Bearer ".length()).trim();
        if (rawToken.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return false;
        }

        User user = userService.authenticate(rawToken).orElse(null);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired or invalid");
            return false;
        }

        request.setAttribute(USER_ATTRIBUTE, user);
        return true;
    }
}
