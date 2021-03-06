/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.api.legacy;

import me.os.webchat.rooms.RoomServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 *
 * @author guilherme
 */
@Configuration
@ConditionalOnWebApplication
public class EndpointConfig {

    @Autowired
    RoomServiceFactory factory;

    @Bean
    public ChatServer ChatServerEndpoint() {
        return new ChatServer();
    }

    @Bean
    public ServerEndpointExporter endpointExporter() {
        return new ServerEndpointExporter();
    }
}
