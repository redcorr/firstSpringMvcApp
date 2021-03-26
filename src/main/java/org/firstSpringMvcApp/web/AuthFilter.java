package org.firstSpringMvcApp.web;

import org.firstSpringMvcApp.entity.User;
import org.firstSpringMvcApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthFilter implements Filter {

    @Autowired
    UserService userService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Basic")){
            Pattern emailPattern = Pattern.compile("Basic\\s.*?:");
            Matcher mE = emailPattern.matcher(authHeader);
            String email = mE.group(0).substring(6,-1);
            Pattern passwordPattern = Pattern.compile(":.*");
            Matcher pE = passwordPattern.matcher(authHeader);
            String password = pE.group(0).substring(1);
            User user = userService.login(email, password);
            request.getSession().setAttribute("User", user);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
