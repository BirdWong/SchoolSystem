package cn.jsuacm.gateway.filter;

import cn.jsuacm.gateway.service.MyUserDetailsService;
import cn.jsuacm.gateway.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName MyJwtTokenFilter
 * @Description token拦截器处理
 * @Author h4795
 * @Date 2019/06/17 22:28
 */
@Component
public class MyJwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    MyUserDetailsService myUserDetailsService;

    private String tokenHeader = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("进入token过滤器");
        String authHeader = httpServletRequest.getHeader(tokenHeader);

        if (authHeader != null && authHeader.startsWith(JwtUtils.PREFIX)){
            String authToken = authHeader.substring(JwtUtils.PREFIX.length());
            Claims Claims = JwtUtils.parseJWT(authToken);
            String username = String.valueOf(Claims.get("id"));
            System.out.println("username:" + username);
            //验证token,具体怎么验证看需求，可以只验证token不查库，把权限放在jwt中即可
            UserDetails UserDetails = myUserDetailsService.loadUserByUsername(username);
            if(JwtUtils.isTokenExpired(Claims)){//token过期
                System.out.println("token过期" + authToken);
            }else{
                System.out.println("token没过期，放行" + authToken);
                //这里只要告诉springsecurity权限即可，账户密码就不用提供验证了，这里我们把UserDetails传给springsecurity，以便以后我们获取当前登录用户
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(UserDetails, null, UserDetails.getAuthorities());
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(null, null, UserDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                logger.info(String.format("Authenticated userDetail %s, setting security context", username));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
