package com.implementations.implementations.securities;

import com.implementations.implementations.components.HttpCookieOAuth2AuthorizationRequestRepository;
import com.implementations.implementations.components.OAuth2AuthenticationFailureHandler;
import com.implementations.implementations.components.OAuth2AuthenticationSuccessHandler;

import com.implementations.implementations.jwt.TokenAuthenticationFilter;
import com.implementations.implementations.services.CustomOAuth2UserService;
import com.implementations.implementations.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfigure extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(){
        return new TokenAuthenticationFilter();
    }
    /*
    BY default ,Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save the
    authorization request .But ,since our service is stateless ,we can't save it in a session.
    we'll save the request in a base64 encoded cookie instead.
     */

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthenticationRequestRepository(){
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(16);
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                   .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
//                .csrf(
//                       csrf->csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                )
                .csrf().disable()
                .formLogin()
                    .disable()
                .httpBasic()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    .and()
                .authorizeRequests()
                    .antMatchers("/","/error","/favicon.ico",
                      "/**/*.png","/**/*.gif","/**/*.svg","/**/*.jpg","/**/*.hmtl","/**/*.css","/**/*.js")
                      .permitAll()
                    .antMatchers("/auth/**","/**oauth2/**")
                      .permitAll()
                    .anyRequest()
                      .authenticated()
                    .and()
                    .oauth2Login()
                       .authorizationEndpoint()
                          .baseUri("/oauth2/callback/*")
                          .authorizationRequestRepository(cookieAuthenticationRequestRepository())
                          .and()
                       .redirectionEndpoint()
                           .baseUri("/oauth2/callback/*")
                           .and()
                       .userInfoEndpoint()
                           .userService(customOAuth2UserService)
                            .and()
                       .successHandler(oAuth2AuthenticationSuccessHandler)
                       .failureHandler(oAuth2AuthenticationFailureHandler);
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
