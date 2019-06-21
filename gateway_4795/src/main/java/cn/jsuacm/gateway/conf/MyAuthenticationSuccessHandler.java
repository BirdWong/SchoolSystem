package cn.jsuacm.gateway.conf;

import cn.jsuacm.gateway.pojo.User;
import cn.jsuacm.gateway.service.UserService;
import cn.jsuacm.gateway.util.JwtUser;
import cn.jsuacm.gateway.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MyAuthenticationSuccessHandler
 * @Description 登录成功后的操作
 * @Author h4795
 * @Date 2019/06/17 22:17
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler{


    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        System.out.println("登陆成功");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");


        final JwtUser userDetail = (JwtUser) authentication.getPrincipal();
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority role : userDetail.getAuthorities()){
            authorities.add(role.getAuthority());
        }
        User user = userService.getUser(userDetail.getUsername());

        String jwtToken = JwtUtils.createJwtToken(user.getUid(), user.getAccountNumbser(), authorities);
        httpServletResponse.addHeader("Authorization",JwtUtils.PREFIX+jwtToken);
        httpServletResponse.getWriter().write("{\"status\":\"true\",\"result\":\"ok\"}");
        httpServletResponse.getWriter().flush();
    }
}
