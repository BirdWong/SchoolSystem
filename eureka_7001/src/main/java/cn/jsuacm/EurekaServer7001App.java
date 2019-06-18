package cn.jsuacm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka 服务器启动类
 * EurekaServer服务器端启动类，接受其他微服务注册进来
 * @author h4795
 * @data 2019/02/17
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServer7001App {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServer7001App.class, args);
    }
}
