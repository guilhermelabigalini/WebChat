/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 *
 * @author guilherme
 */
@SpringBootApplication
@EnableWebSocket
@ComponentScan
public class Application extends SpringBootServletInitializer {

    private static final Class<Application> APPLICATION_CLASS = Application.class;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(APPLICATION_CLASS);
    }
//
//    @Bean
//    public ServletContextAware endpointExporterInitializer(final ApplicationContext applicationContext) {
//        return new ServletContextAware() {
//            @Override
//            public void setServletContext(ServletContext servletContext) {
//                ServerEndpointExporter exporter = new ServerEndpointExporter();
//                exporter.setApplicationContext(applicationContext);
//                exporter.afterPropertiesSet();
//            }
//        };
//    }
}
