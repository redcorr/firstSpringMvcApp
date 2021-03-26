package org.firstSpringMvcApp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

//@Controller
public class LocaleController {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Lazy
    @Autowired
    LocaleResolver localeResolver;

    @GetMapping("/locale/{local}")
    public ModelAndView setLocale(@PathVariable("local") String local, HttpServletRequest request, HttpServletResponse response){
        int i = local.indexOf('_');
        String language = local.substring(0, i);
        String country = local.substring(i, i+1);
        Locale locale = new Locale(language, country);
        System.out.println(request.getHeader("referer"));
        localeResolver.setLocale(request, response, locale);
        logger.info("locale is set to {}.", locale);
        System.out.println(request.getHeader("referer"));
        String referer = request.getHeader("referer");
        return new ModelAndView("redirect:/");
    }
}
