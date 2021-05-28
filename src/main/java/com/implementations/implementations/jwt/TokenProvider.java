package com.implementations.implementations.jwt;
import com.implementations.implementations.configurations.AppProperties;
import com.implementations.implementations.securities.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
//    private static final Logger logger= LoggerFactory.getLogger(TokenProvider.class);

  @Autowired
  private AppProperties appProperties;

  public String createToken(Authentication authentication){
      UserPrincipal userPrincipal= (UserPrincipal)authentication.getPrincipal();
      Date now=new Date();
      Date expiryDate=new Date(now.getTime()+ appProperties.getAuth().getTokenExpirationMsec());
      var value=appProperties.getAuth().getTokenSecret().getBytes(StandardCharsets.UTF_8);
      Key key= Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes(StandardCharsets.UTF_8));
          System.out.println(key.toString());
      return Jwts.builder()
              .setSubject(Long.toString(userPrincipal.getId()))
              .setIssuedAt(new Date())
              .setExpiration(expiryDate)
              .signWith(key)
              .compact();
  }
public Long getUserIdFromToken(String token){
      long seconds=3*60;
      Key key= Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes(StandardCharsets.UTF_8));

    try {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setAllowedClockSkewSeconds(seconds)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        Claims body = claims.getBody();
        return Long.parseLong(body.getSubject());
    }catch(JwtException ex){
        throw new IllegalStateException(ex.getMessage());
    }

}
}
