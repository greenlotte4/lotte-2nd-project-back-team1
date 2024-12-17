package com.BackEndTeam1.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class SocketIoServerConfig {
    //todo 나중에 설정
    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("your-server-ip.com");  // 또는 'localhost' 또는 '192.168.1.100'

        config.setHostname("localhost");
        config.setPort(8081); // 서버가 사용할 포트

        SocketIOServer server = new SocketIOServer(config);
        return server;
    }
}