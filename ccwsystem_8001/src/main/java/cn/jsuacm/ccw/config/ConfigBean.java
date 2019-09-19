package cn.jsuacm.ccw.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.MultipartConfigElement;

/**
 * @ClassName ConfigBean
 * @Description 配置服务之间访问的bean
 * @Author h4795
 * @Date 2019/06/20 21:00
 */
@Configuration
public class ConfigBean {
    @Bean(name = "restTemplate")
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new   SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(5000);
        simpleClientHttpRequestFactory.setReadTimeout(5000);
        return new RestTemplate(simpleClientHttpRequestFactory);
    }


    @Bean(name="remoteRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    /**
     * 限制文件最大上传大小
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize("300KB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("1024KB");
        return factory.createMultipartConfig();
    }
}
