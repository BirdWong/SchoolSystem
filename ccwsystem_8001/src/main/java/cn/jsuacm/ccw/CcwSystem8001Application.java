package cn.jsuacm.ccw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @ClassName CcwSystem8001Application
 * @Description ccwsystem启动类
 * @Author h4795
 * @Date 2019/06/18 21:24
 */
@EnableEurekaClient
@SpringBootApplication
@EnableSwagger2
public class CcwSystem8001Application {
    public static void main(String[] args) {
        // 解决elasticsearch和redis的冲突
        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(CcwSystem8001Application.class, args);
    }
}
