package com.kvanzi.todotaskbackend.auth.internal.configuration;

import com.kvanzi.todotaskbackend.auth.internal.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfiguration {
}
