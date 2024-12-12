package com.mudcode.springboot.configuration;

import com.mudcode.springboot.Constant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class ValidateLoginCaptchaCodeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String filterUrl = "/login";
        AntPathRequestMatcher matcher = new AntPathRequestMatcher(filterUrl, "POST");
        if (matcher.matches(request)) {
            String captchaRequestKey = "captcha";
            String captcha = request.getParameter(captchaRequestKey);
            String captchaCode = (String) request.getSession().getAttribute(Constant.CAPTCHA_SESSION_KEY);
            request.getSession().removeAttribute(Constant.CAPTCHA_SESSION_KEY);
            if (captchaCode == null || captchaCode.isEmpty() || !captchaCode.equals(captcha)) {
                String failureUrl = "/login?error";
                AuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler(
                        failureUrl);
                authenticationFailureHandler.onAuthenticationFailure(request, response,
                        new AuthenticationServiceException("invalid captcha code"));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
