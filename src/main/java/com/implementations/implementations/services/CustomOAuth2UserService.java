package com.implementations.implementations.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.implementations.implementations.dao.IUserRepository;
import com.implementations.implementations.entities.User;
import com.implementations.implementations.exceptions.OAuth2AuthenticationProcessingException;
import com.implementations.implementations.oauth2usersinfo.AuthProvider;
import com.implementations.implementations.oauth2usersinfo.OAuth2UserInfo;
import com.implementations.implementations.oauth2usersinfo.OAuth2UserInfoFactory;
import com.implementations.implementations.securities.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;



import java.util.Optional;

/*
This class is responsible for finding the user details in the database using the
using the attributes filled with user by OAuth2 client.
 */
@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  @Autowired
  private IUserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
  // We get the oAuth2User from the OAuthProvider and it is derived from the scope we ask.
  // From that oAuth2User we build our user info that is in the database.
    OAuth2User oAuth2User = super.loadUser(userRequest);
 try{
   return processOAuth2User(userRequest,oAuth2User);
 }catch( AuthenticationException | JsonProcessingException  ex){
   throw  new RuntimeException(ex.getMessage());
 }
  }


  public OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws JsonProcessingException {
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            userRequest.getClientRegistration().getRegistrationId(),
            oAuth2User.getAttributes());

    if (StringUtils.hasText(oAuth2UserInfo.getEmail())) {
      throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
    }
    Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
    User user;
    if (userOptional.isPresent()) {
      user = userOptional.get();
      if (!user.getProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
        throw new OAuth2AuthenticationProcessingException(
                "Looks like you are signed with " + user.getProvider() + " account .PLease "
                        + " use your " + user.getProvider() + "account login.");
      }
      System.out.println("Logging a value ....");
      System.out.println(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
      user = updateExistingUser(user, oAuth2UserInfo);

    }else{
      user=registerNewUser(userRequest,oAuth2UserInfo);
    }

    return UserPrincipal.create(user);
  }

  private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
     User user=new User();
     user.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
     user.setProviderId(oAuth2UserInfo.getId());
     user.setName(oAuth2UserInfo.getName());
     user.setEmail(oAuth2UserInfo.getEmail());
     user.setImageUrl(oAuth2UserInfo.getImageUrl());
     return userRepository.save(user);
  }


  private User updateExistingUser(User extingUser, OAuth2UserInfo oAuth2UserInfo) {
    extingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
    extingUser.setName(oAuth2UserInfo.getImageUrl());
    return userRepository.save(extingUser);
  }
}
