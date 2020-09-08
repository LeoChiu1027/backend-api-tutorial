package com.spring.oauth.sample.controller;


import com.spring.oauth.sample.dto.LoginRequest;
import com.spring.oauth.sample.dto.LoginResponse;
import com.spring.oauth.sample.security.CookieUtil;
import com.spring.oauth.sample.security.CustomToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.security.Principal;

@Slf4j
@Validated
@RestController
public class LoginController {


    @Value("${jwt.clientId:oauth-sample}")
    private String clientId;

    @Value("${jwt.client-secret:secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CookieUtil cookieUtil;

    @PostMapping("/api/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        MultiValueMap<String , Object> body = new LinkedMultiValueMap<>();
        body.add("username", loginRequest.getUsername());
        body.add("password", loginRequest.getPassword());
        body.add("grant_type", "password");
        HttpEntity<MultiValueMap<String , Object>> requestEntity = new HttpEntity<>(body,createHeaders(clientId, clientSecret));
        String authenticatedUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/oauth/token";
        ResponseEntity<CustomToken> tokenResponse = restTemplate.exchange(authenticatedUrl, HttpMethod.POST, requestEntity, CustomToken.class);

        System.out.println(tokenResponse.getBody());
        HttpHeaders responseHeaders = new HttpHeaders();
        addAccessTokenCookie(responseHeaders, tokenResponse.getBody());
        addRefreshTokenCookie(responseHeaders, tokenResponse.getBody());

        LoginResponse loginResponse = new LoginResponse(LoginResponse.SuccessFailure.SUCCESS, "Auth successful. Tokens are created in cookie.");
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }


    @GetMapping("/api/auth/user")
    public Principal user (Principal principal) {
        return principal;
    }

    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, CustomToken token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(token.getAccess_token(), token.getExpires_in()).toString());
    }

    private void addRefreshTokenCookie(HttpHeaders httpHeaders, CustomToken token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(token.getRefresh_token(), token.getRefresh_expires_in()).toString());
    }
}
