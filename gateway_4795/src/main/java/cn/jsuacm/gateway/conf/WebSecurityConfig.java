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
                        "/**.html",
                        "/favicon.ico",
                        "/**/**.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()


                // 测试调试测试
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/api").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()















                /*
                 * ******权限中心模块安全配置********
                 */
                // 添加权限管理
                .antMatchers("/authentication/add/ing/*").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})
                .antMatchers("/authentication/add/menber/*").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})
                .antMatchers("/authentication/add/admin/*").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)
                .antMatchers("/authentication/add/teacher/*").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)
                .antMatchers("/authentication/add/administrator/*").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)


                //删除权限管理
                .antMatchers("/authentication/delete/ing/*").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})
                .antMatchers("/authentication/delete/menber/*").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})
                .antMatchers("/authentication/delete/admin/*").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)
                .antMatchers("/authentication/delete/teacher/*").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)
                .antMatchers("/authentication/delete/administrator/*").hasAnyAuthority(AuthenticationService.ADMINISTRATOR)

                // 修改用户密码
                .antMatchers("/authentication/updatePassword").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 获取用户的权限
                .antMatchers("/authentication/getAuthenticationByUid/*").permitAll()

                // 其他的权限操作，必须拥有管理员或者超级管理员权限
                .antMatchers("/authentication/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 用户信息修改权限管理
                .antMatchers("/user/sendRegisterEmail").permitAll()
                .antMatchers("/user/registerUser").permitAll()
                .antMatchers("/user/sendUpdateEmail").permitAll()
                .antMatchers("/user/updatePasswordByEmail").permitAll()

                // 查看所有用户信息以及其权限信息
                .antMatchers("/user/getPage/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 查看实验室所有成员列表
                .antMatchers("/user/getCcwMenber").permitAll()
                // 查看正在实验室成员列表
                .antMatchers("/user/getCcwIngMenber").permitAll()



                // 给其他模块用来判断是否有这个用户,或者获取这个用户的信息
                .antMatchers("/user/isUser/*").permitAll()
                .antMatchers("/user/getUserById/*").permitAll()
                .antMatchers("/user/getUserByAccountNumber/*").permitAll()


                /*
                 *  *********工作室路径安全配置*************
                 *  - 模块:ccwsystem-8001
                 *  - 路由路径： ccw
                 */


                /*
                 * *********文章内容操作********
                 *        标识前缀 article
                 */
                // 管理员分页获取用户的所有文章/私有文章/草稿文章列表
                .antMatchers("/ccw/article/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 分页获取最热的文章列表
                .antMatchers("/ccw/article/getHots/**").permitAll()

                // 获取这个用户最新的文章列表，自动过滤私有/草稿文章
                .antMatchers("/ccw/article/getNewArticle/**").permitAll()

                // 获取一个用户公开的文章列表
                .antMatchers("/ccw/article/getUserPublicArticleList/**").permitAll()

                // 获取一个用户公开的文章归档
                .antMatchers("/ccw/article/getUserPublicArchive/*").permitAll()

                // 获取一个用户的公开的文章内容
                .antMatchers("/ccw/article/userArticle/*").permitAll()

                // 查看一篇/所有文章的观看数量
                .antMatchers("/ccw/article/view/*").permitAll()
                .antMatchers("/ccw/article/views/*").permitAll()

                /*
                 * ***********分类接口*******
                 *      前缀标识：category
                 */
                // 添加一个一级分类
                .antMatchers("/ccw/category/addFirstCategory").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 添加一个二级分类
                .antMatchers("/ccw/category/addSecondCategory").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 删除一个一级分类
                .antMatchers("/ccw/category/deleteCategory/*").hasAnyAuthority(new String[]{AuthenticationService.ADMINISTRATOR, AuthenticationService.ADMIN})

                // 更新一个分类
                .antMatchers("/ccw/category/updateCategory").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                //获取所有的分类信息
                .antMatchers("/ccw/category/getAllCategory").permitAll()

                // 获取所有的一级分类
                .antMatchers("/ccw/category/getFirstCategory").permitAll()

                // 通过一级分类id获取二级分类列表
                .antMatchers("/ccw/category/getSecondByCid/*").permitAll()


                /*
                 * ***********标签接口操作**************
                 *      前缀标识：label
                 */


                // 获取所有用户的所有的标签
                .antMatchers("/ccw/label/getAll").permitAll()
                // 通过标签id获取标签信息
                .antMatchers("/ccw/label/getById/*").permitAll()
                // 通过用户id获取标签信息
                .antMatchers("/ccw/label/getByUid/*").permitAll()


                /*
                 * ***********文章信息接口**************
                 *      前缀标识：articleInfomation
                 */
                // 管理员的操作
                .antMatchers("/ccw/articleInfomation/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

                // 通过文章id/ 用户id/ 标签id/ 分类id获取用户公开的文章信息
                .antMatchers("/ccw/articleInfomation/getPublicByAid").permitAll()
                .antMatchers("/ccw/articleInfomation/getPublicByCid").permitAll()
                .antMatchers("/ccw/articleInfomation/getPublicByLid").permitAll()
                .antMatchers("/ccw/articleInfomation/getPublicByUid").permitAll()


                /*
                 * ***********es搜索接口操作*********
                 *      前缀标识：search
                 */
                // 通过文章信息/用户名搜索文章
                .antMatchers("/ccw/search/article/**").permitAll()
                // 通过书籍的信息搜索书籍
                .antMatchers("/ccw/search/book/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR, AuthenticationService.MENBER})










                /*
                 * ************图书接口操作**************
                 *      前缀标识：book
                 */
                .antMatchers("/ccw/book/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})







                /*
                 * ***********借阅操作接口*************
                 *      前缀标识：borrow
                 */
                .antMatchers("/ccw/borrow/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})


                .antMatchers("/ccw/borrow/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR, AuthenticationService.ING})


                /*
                 * ************项目成员接口***********
                 *      前缀标识：collaborators
                 */
                .antMatchers("/ccw/collaborators/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMINISTRATOR,AuthenticationService.ADMIN})
                .antMatchers("/ccw/collaborators/**").hasAnyAuthority(AuthenticationService.ING)


                /*
                 * ***********项目接口操作***********
                 *      前缀标识：project
                 */
                .antMatchers("/ccw/project/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})


                /*
                 * ***********招新表单接口操作**********
                 *      前缀标识: form
                 */
                .antMatchers("/ccw/form/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMINISTRATOR, AuthenticationService.ADMIN})
                .antMatchers("/ccw/form/get").permitAll()


                /*
                 * **********招新时间接口操作**********
                 *      前缀标识：limitTime
                 */
                .antMatchers("/ccw/limitTime/**").hasAnyAuthority(new String[]{AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})
                .antMatchers("/ccw/limitTime/get").permitAll()


                /*
                 * **********用户报名接口操作*********
                 *      前缀标识: recruitment
                 */
                .antMatchers("/ccw/recruitment/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMINISTRATOR, AuthenticationService.ADMIN})
                .antMatchers(HttpMethod.POST,"/ccw/recruitment/add").permitAll()
                .antMatchers(HttpMethod.PUT,"/ccw/recruitment/upload").permitAll()
                .antMatchers("/ccw/recruitment/**").hasAnyAuthority(AuthenticationService.ING)




                /*
                 * ************公告分类接口****************
                 *        前缀标识：annoucementCategory
                 */
                .antMatchers("/ccw/annoucementCategory/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMINISTRATOR, AuthenticationService.ADMIN})
                .antMatchers("/ccw/annoucementCategory/getById/*").permitAll()
                .antMatchers("/ccw/annoucementCategory/getOfAll").permitAll()
                .antMatchers("/ccw/annoucementCategory/getOfAdmin/*").hasAnyAuthority(new String[]{AuthenticationService.ADMINISTRATOR, AuthenticationService.ADMIN})
                .antMatchers("/ccw/annoucementCategory/getOfAdministrator/*").hasAuthority(AuthenticationService.ADMINISTRATOR)


                /*
                 * ************公告文章接口*************
                 *        前缀标识：announcement
                 */
                .antMatchers("/ccw/announcement/admin/**").hasAnyAuthority(new String[]{AuthenticationService.ADMINISTRATOR, AuthenticationService.ADMIN})
                .antMatchers("/ccw/announcement/getById/**").permitAll()
                .antMatchers("/ccw/announcement/getPages").permitAll()



                .antMatchers("/ccw/**").hasAnyAuthority(new String[]{AuthenticationService.MENBER, AuthenticationService.ADMIN, AuthenticationService.ADMINISTRATOR})

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
