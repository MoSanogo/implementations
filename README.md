# implementations

This implements the OAuth2 authentication with Google,Github and Facebook.
The core purpose of this was to fully customize OAuth2Configurer in spring boot replacing the HttpSessionOAuth2AuthorizationRequestRepository 
by HttpCookieOAuth2AuthorizationRequestRepository ,OAuth2AuthenticationFailureHandler which gets invoked when the OAuth2 authentication failes and 
Auth2AuthenticationSuccessHandler which grants a signed token to a user for later authentication.
It also implements the email and password based authentication.
