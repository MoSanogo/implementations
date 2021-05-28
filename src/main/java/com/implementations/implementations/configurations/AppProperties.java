package com.implementations.implementations.configurations;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
//This class is biding between app section in the application.properties and a POJO.

@ConfigurationProperties(prefix="app")
@Getter @Setter
public class AppProperties {
    private final Auth auth=new Auth();
    private final OAuth2 oauth2=new OAuth2();
    @Getter @Setter
    public static class Auth {
        private String tokenSecret;
        private long tokenExpirationMsec;
    }
    @Getter @Setter
    public static final class OAuth2{
        private List<String> authorizedRedirectUris=new ArrayList<>();
    }

}
