package com.implementations.implementations.jwt;

import com.implementations.implementations.services.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetails;
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        PathMatcher pathLogin=new AntPathMatcher("/login");
        StaticResourceRequest.StaticResourceRequestMatcher staticResourceRequestMatcher= PathRequest
                .toStaticResources().atCommonLocations();
        /*The following condition tells spring boot not to execute this filter if the request is meant
        * for retrieving static files such javascript ,css,image and so on. */
        log.debug(request.getServletPath());
            if(staticResourceRequestMatcher.matches(request)||pathLogin.match(request.getServletPath(),"/login")){
                filterChain.doFilter(request,response);
                return;
            }

          try{
              String jwt=getJwtFromRequest(request);
              //jwt may be null.So we check it out with this if-condition.
              if(StringUtils.hasText(jwt)){
                  Long userId= tokenProvider.getUserIdFromToken(jwt);
                  UserDetails userDetails=customUserDetailsService.loadUserById(userId);
                  UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                  authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                  SecurityContextHolder.getContext().setAuthentication(authentication);
              }
              filterChain.doFilter(request,response);
          }catch(Exception ex){
              throw new IllegalStateException(ex.getMessage());
          }

    }


    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken=request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
           return bearerToken.substring(7,bearerToken.length());
        }
        return null;
    }
}
