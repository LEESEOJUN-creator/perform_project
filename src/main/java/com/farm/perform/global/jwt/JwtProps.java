package com.farm.perform.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProps {
    private String secret;
    private long accessTokenExpireTime;
    private long refreshTokenExpireTime;
}
