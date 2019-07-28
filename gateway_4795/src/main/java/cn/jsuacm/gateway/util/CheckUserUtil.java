package cn.jsuacm.gateway.util;

import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName CheckUserUtil
 * @Description 用户本人操作检测
 * @Author h4795
 * @Date 2019/06/20 16:43
 */
public class CheckUserUtil {

    private static String tokenHeader = "Authorization";


    /**
     * 对于需要验证本人操作的行为，进行验证， 其必须登录，并且进行操作的id与token解析的id一致
     * @param req request为了获取其中的token
     * @param uid 需要操作的用户id
     * @return
     */
    public static boolean isUser(HttpServletRequest req, int uid){
        String authHeader = req.getHeader(tokenHeader);
        if (authHeader != null && authHeader.startsWith(JwtUtils.PREFIX)){
            String authToken = authHeader.substring(JwtUtils.PREFIX.length());
            Claims claims = JwtUtils.parseJWT(authToken);
            int id = Integer.valueOf(String.valueOf(claims.get("id")));
            if (id == uid){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
}
