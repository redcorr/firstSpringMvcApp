package org.firstSpringMvcApp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

//@Component
public class LocaleInterceptor implements HandlerInterceptor {

    @Autowired
    @Qualifier("i18n")
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            Locale locale = localeResolver.resolveLocale(request);

            logger.info("start interceptor");
            modelAndView.addObject("__messageSource__", messageSource);
            modelAndView.addObject("__locale__", locale);
            System.out.println(request.getHeader("referer"));
            System.out.println(response.getHeader("referer"));
    }
}
