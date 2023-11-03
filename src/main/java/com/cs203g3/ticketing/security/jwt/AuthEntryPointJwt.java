package com.cs203g3.ticketing.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    // Inject a HandlerExceptionResolver to handle exceptions
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        // Log an error message indicating an unauthorized error and include the error message from the AuthenticationException
        logger.error("Unauthorised error: {}", authException.getMessage());
        
        // Use the HandlerExceptionResolver to handle the authentication exception by resolving it and sending an appropriate response
        resolver.resolveException(request, response, null, authException);
    }

}
