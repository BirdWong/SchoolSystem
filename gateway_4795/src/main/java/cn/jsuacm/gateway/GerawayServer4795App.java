package cn.jsuacm.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @ClassName GerawayServer4795App
 * @Description 路由中心启动类
 * @Author h4795
 * @Date 2019/06/17 19:13
 */
@SpringBootApplication
@EnableZuulProxy
@MapperScan("cn.jsuacm.gateway.mapper")
@EnableSwagger2
public class GerawayServer4795App {
    public static void main(String[] args) {
        SpringApplication.run(GerawayServer4795App.class, args);
    }
}
