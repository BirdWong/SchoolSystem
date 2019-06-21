package cn.jsuacm.gateway.conf;


import cn.jsuacm.gateway.filter.MyJwtTokenFilter;
import cn.jsuacm.gateway.pojo.Authentication;
import cn.jsuacm.gateway.service.AuthenticationService;
import cn.jsuacm.gateway.service.MyUserDetailsService;
import cn.jsuacm.gateway.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    // Spring会自动寻找同样类型的具体类注入，这里就是JwtUserDetailsServiceImpl了
    @Autowired
    MyUserDetailsService userDetailsService;

    //登录成功处理类，如返回自定义jwt
    @Autowired
    MyAuthenticationSuccessHandler authenticationSuccessHandler;
    //登录失败处理类
    @Autowired
    MyAuthenticationFailHandler authenticationFailHandler;
    //token 过滤器，解析token
    @Autowired
    MyJwtTokenFilter jwtTokenFilter;
    //权限不足处理类
    @Autowired
    MyAccessDeniedHandler myAccessDeniedHandler;
    //其他异常处理类
    @Autowired
    MyAuthenticationException myAuthenticationException;




    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
                //指定登录的地址，用户登录请求必须是表单格式，application/json方式是接受不到参数的
                //这样写参数可以 http://localhost:8080/user/login?username=admin&password=123456
                        formLogin()
                .loginProcessingUrl("/user/login")
//                .failureForwardUrl("/test/error") //这个没效果
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailHandler)
                .and()
                // 由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 允许对于网站静态资源的无授权访问
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                // 测试调试测试
                .antMatchers("/test/**").permitAll()
                .antMatchers("/haha").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/ccw/article/testtest/*").permitAll()


                /*
                 * ******权限中心模块安全配置********
                 */
                // 添加权限管理
                .antMatchers("/authentication/add/menber").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})
                .antMatchers("/authentication/add/admin").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)
                .antMatchers("/authentication/add/teacher").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)
                .antMatchers("/authentication/add/administrator").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)

                //删除权限管理
                .antMatchers("/authentication/delete/menber").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})
                .antMatchers("/authentication/delete/admin").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)
                .antMatchers("/authentication/delete/teacher").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)
                .antMatchers("/authentication/delete/administrator").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)

                // 修改用户密码
                .antMatchers("/authentication/updatePassword").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 其他的权限操作，必须拥有管理员或者超级管理员权限
                .antMatchers("/authentication/*").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 用户信息修改权限管理
                .antMatchers("/user/sendRegisterEmail").permitAll()
                .antMatchers("/user/registerUser").permitAll()
                .antMatchers("/user/sendUpdateEmail").permitAll()
                .antMatchers("/user/updatePasswordByEmail").permitAll()

                // 查看所有用户信息和权限信息
                .antMatchers("/user/getPage/*").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 给其他模块用来判断是否有这个用户
                .antMatchers("/user/isUser/*").permitAll()


                /*
                 *  *********工作室路径安全配置*************
                 *  - 模块:ccwsystem-8001
                 *  - 路由路径： ccw
                 */
                // 获取一篇公共的文章不需要登录
                .antMatchers("/ccw/article/userArticle/*").permitAll()
                // 获取一个用户的所有阅读量不需要登录
                .antMatchers("/ccw/article/views/*").permitAll()








                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        // 禁用缓存
        httpSecurity.headers().cacheControl();

        // 添加JWT filter
        httpSecurity.addFilterBefore(jwtTokenFilter, LogoutFilter.class)
                // 添加权限不足 filter
                .exceptionHandling().accessDeniedHandler(myAccessDeniedHandler)
                //其他异常处理类
                .authenticationEntryPoint(myAuthenticationException);

    }


    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                // 设置UserDetailsService 获取user对象
                .userDetailsService(this.userDetailsService)
                // 自定义密码验证方法
                .passwordEncoder(new PasswordEncoder() {
                    //这个方法没用
                    @Override
                    public String encode(CharSequence charSequence) {
                        return "";
                    }

                    //自定义密码验证方法,charSequence:用户输入的密码，s:我们查出来的数据库密码
                    @Override
                    public boolean matches(CharSequence charSequence, String s) {
                        // 用户的转为MD5
                        String pass = MD5Util.string2MD5(charSequence.toString());
                        // 数据库的解密成md5
                        s = MD5Util.convertMD5(s);
                        System.out.println("用户输入密码:" + charSequence + "与数据库相同？" + s.equals(pass));
                        return s.equals(pass);
                    }
                });

    }

}
