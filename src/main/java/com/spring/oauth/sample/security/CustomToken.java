package com.spring.oauth.sample.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomToken {

    private String access_token;

    private String token_type;

    private String refresh_token;

    private Long expires_in;

    private String scope;

    private String customInfo;

    private Long refresh_expires_in;

    private Set<Map<String,String>> authorities;

}
