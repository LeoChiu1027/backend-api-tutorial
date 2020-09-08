package com.spring.oauth.sample.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class CustomTokenExtractor extends BearerTokenExtractor {

    @Value("${jwt.accessTokenCookieName}")
    private String accessTokenCookieName;

    @Value("${jwt.refreshTokenCookieName}")
    private String refreshTokenCookieName;

    @Override
    protected String extractToken(HttpServletRequest request) {
        String result = getTokenFromRequest(request);
        return SecurityCipher.decrypt(result);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(accessTokenCookieName))
                .findFirst()
                .map(Cookie::getValue).orElse(null);
    }
}
