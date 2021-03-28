package org.firstSpringMvcApp;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring.servlet.PebbleViewResolver;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.firstSpringMvcApp.web.ChatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;
import java.util.*;

@Configuration
@ComponentScan
@Component
@EnableWebMvc
@EnableJms
@EnableScheduling
@EnableMBeanExport
@PropertySource({"classpath:/jdbc.properties", "classpath:/smtp.properties",
        "classpath:/jms.properties", "classpath:/task.properties"})
public class Appconfig {
    public static void main(String[] args) throws Exception{
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();
        Context context = tomcat.addWebapp("",new File("src/main/webapp").getAbsolutePath());
        WebResourceRoot resources = context.getResources();

        context.setResources(resources);
        tomcat.start();
        tomcat.getServer().await();
    }

    @Bean
    ViewResolver createViewResolver(@Autowired ServletContext servletContext, @Autowired @Qualifier("i18n") MessageSource messageSource){
        PebbleEngine engine = new PebbleEngine.Builder()
                .loader(new ServletLoader(servletContext)).extension(createExtension(messageSource)).build();
        PebbleViewResolver viewResolver = new PebbleViewResolver(engine);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix("");
        return viewResolver;
    }

    private Extension createExtension(MessageSource messageSource){
        return new AbstractExtension() {
            @Override
            public Map<String, Function> getFunctions() {
                return Map.of("_", new Function() {
                    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
                        String key = (String) args.get("0");
                        List<Object> arguments = this.extractArguments(args);
                        Locale locale = (Locale) context.getVariable("__locale__");
                        return messageSource.getMessage(key, arguments.toArray(), "???" + key + "???", locale);
                    }
                    private List<Object> extractArguments(Map<String, Object> args) {
                        int i = 1;
                        List<Object> arguments = new ArrayList<>();
                        while (args.containsKey(String.valueOf(i))) {
                            Object param = args.get(String.valueOf(i));
                            arguments.add(param);
                            i++;
                        }
                        return arguments;
                    }
                    public List<String> getArgumentNames() {
                        return null;
                    }
                });
            }
        };
    }

    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    @Bean
    DataSource createDataSource(){
        HikariConfig config= new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
    @Bean
    JdbcTemplate createJdbcTemplate(@Autowired DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    PlatformTransactionManager createTxManager(@Autowired DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    WebMvcConfigurer createWebMvcConfigurer(@Autowired HandlerInterceptor[] interceptors){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**").allowedOrigins("*").allowedMethods("GET", "POST");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                for(var interceptor: interceptors){
                    registry.addInterceptor(interceptor);
                }
            }
        };
    }
    @Bean(name="localeResolver")
    LocaleResolver createLocaleResolver(){
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        localeResolver.setDefaultTimeZone(TimeZone.getDefault());
        return localeResolver;
    }

    @Bean("i18n")
    MessageSource createMessageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Bean
    WebSocketConfigurer createWebSocketConfigurer(@Autowired ChatHandler chatHandler){
        return new WebSocketConfigurer() {
            @Override
            public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
                webSocketHandlerRegistry.addHandler(chatHandler, "/chat");
            }
        };
    }

    @Bean
    JavaMailSender createJavaMailSender(
        // smtp.properties:
        @Value("${smtp.host}") String host,
        @Value("${smtp.port}") int port,
        @Value("${smtp.auth}") String auth,
        @Value("${smtp.username}") String username,
        @Value("${smtp.password}") String password,
        @Value("${smtp.debug:true}") String debug){
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        if (port == 587) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        if (port == 465) {
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        props.put("mail.debug", debug);
        return mailSender;
    }

    @Bean
    ConnectionFactory createConnectionFactory(
            @Value("${jms.uri}") String uri, @Value("${jms.username}") String username, @Value("${jms.password}") String password){
        return new ActiveMQJMSConnectionFactory(uri, username, password);
    }

    @Bean
    JmsTemplate createJmsTemplate(@Autowired ConnectionFactory connectionFactory){
        return new JmsTemplate(connectionFactory);
    }

    @Bean("jmsListenerContainerFactory")
    DefaultJmsListenerContainerFactory createJmsListenerContainerFactory(@Autowired ConnectionFactory connectionFactory){
        var factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

}