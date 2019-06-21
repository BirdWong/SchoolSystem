package cn.jsuacm.ccw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * @ClassName CcwSystem8001Application
 * @Description ccwsystem启动类
 * @Author h4795
 * @Date 2019/06/18 21:24
 */
@EnableEurekaClient
@SpringBootApplication
public class CcwSystem8001Application {
    public static void main(String[] args) {
        SpringApplication.run(CcwSystem8001Application.class, args);
    }
}
