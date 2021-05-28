package com.implementations.implementations.oauth2usersinfo;
import lombok.*;
import java.util.Map;

@Getter @Setter
public abstract class OAuth2UserInfo {
    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    protected Map<String,Object> attributes;
    public abstract String getId();
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getImageUrl();

}
