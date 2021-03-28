package org.firstSpringMvcApp.web;

import org.firstSpringMvcApp.service.BlackListMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BlackListInterceptor implements HandlerInterceptor {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    BlackListMBean blackListMBean;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        logger.info("check address {} ...", ip);
        if(blackListMBean.shouldBlock(ip)){
            logger.warn("should block address {}.",ip);
            response.sendError(403);
            return false;
        }else{
            return true;
        }
    }
}
