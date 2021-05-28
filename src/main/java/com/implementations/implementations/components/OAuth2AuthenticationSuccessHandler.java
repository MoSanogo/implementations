package com.implementations.implementations.components;

import com.implementations.implementations.configurations.AppProperties;
import com.implementations.implementations.exceptions.BadRequestException;
import com.implementations.implementations.jwt.TokenProvider;
import com.implementations.implementations.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import static com.implementations.implementations.components.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
private final TokenProvider tokenProvider;
private final AppProperties appProperties;
private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
@Autowired
OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider,AppProperties appProperties,
                                       HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository){
    this.tokenProvider=tokenProvider;
    this.appProperties=appProperties;
    this.httpCookieOAuth2AuthorizationRequestRepository=httpCookieOAuth2AuthorizationRequestRepository;
}
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
       String targetUrl=determineTargetUrl(request,response,authentication);
       if(response.isCommitted()){
           log.debug("Response has been already committed.Unable to redirect to "+targetUrl);
           return ;
       }
        String token= tokenProvider.createToken(authentication);
        response.addHeader("Authorization","Bearer "+token);
        /*Removes temporary authentication-related data which may have been
        stored in the session during the authentication process.*/
        clearAuthenticationAttributes(request, response);
        //Redirect the user.
        getRedirectStrategy().sendRedirect(request,response,targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request,HttpServletResponse response,Authentication authentication){
        Optional<String> redirectUri= CookieUtils.getCookie(request,REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
/*We need to check the redirectUri if the valid one preventing hackers from  sticking redirectUri
in the cookie to redirect user to their own site.
 */
        if(redirectUri.isPresent()&&!isAuthorizedRedirectUri(redirectUri.get())){
            throw new BadRequestException("Sorry !We got an unauthorized redirect URI.");
        }
        String targetUrl=redirectUri.orElse(getDefaultTargetUrl());
/*It is not safe to stick a token in Uri .
       String token= tokenProvider.createToken(authentication);
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token",token)
                .build()
                .toUriString();
*/
        return targetUrl;
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri=URI.create(uri);
        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri->{
                    URI authorizedURI=URI.create(authorizedRedirectUri);
                    //Only validate host and port and let the clients use different path if they wish to.
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                    && authorizedURI.getPort()==clientRedirectUri.getPort()){
                        return true;
                    }else{
                        return false;
                    }
                });
    }
    protected  void clearAuthenticationAttributes(HttpServletRequest request,HttpServletResponse response){
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request,response);
    }
}
