package com.implementations.implementations.oauth2usersinfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.implementations.implementations.exceptions.OAuth2AuthenticationProcessingException;

import lombok.extern.slf4j.Slf4j;
/*
This class is a factory class for OAuth2UserInfo.
In the FacebookUserInfo section,I have serialize the OAuth2UserInfo we got back from
facebook OAuth2 client to have a better understanding about the attribute object.
It is not necessary and you can take it out from your code when inspiring from this
code.:-)
 */

import java.util.Map;
@Slf4j
public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            String registrationId, Map<String,Object> attributes
    ) throws JsonProcessingException {
        if(registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())){
            return new GoogleOAuth2UserInfo(attributes);
        }else if(
                registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.toString())
        ){
            ObjectMapper objectMapper= new ObjectMapper();
            String jsonString=objectMapper.writeValueAsString(attributes);
            log.info("We are logging the retuning attributes from facebook.");
            System.out.println(jsonString);
            return new FacebookOAuth2UserInfo(attributes);
        }else if(
                registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())
        ){
            return new GithubOAuth2UserInfo(attributes);
        }else {
            throw new OAuth2AuthenticationProcessingException(
                    "Sorry ! Login with "+ registrationId+ "is not supported"
            );

        }

    }
}
