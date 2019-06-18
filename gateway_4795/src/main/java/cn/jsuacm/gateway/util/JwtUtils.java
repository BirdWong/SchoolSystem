package cn.jsuacm.gateway.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: GYB
 * createAt: 2018/9/14
 */
@Component
public class JwtUtils {



   /*
    iss: 该JWT的签发者
    sub: 该JWT所面向的用户
    aud: 接收该JWT的一方
    exp(expires): 什么时候过期，这里是一个Unix时间戳
    iat(issued at): 在什么时候签发的
   */

    /**
     * 签名秘钥
     */
    public static final String SECRET = "4795";


    /**
     * 签名前缀
     */
    public static final String PREFIX = "RocWong_";

    /**
     * 生成token
     * @param accountNumber 传入账户
     * @param authorizations 权限列表
     * @return
     */
    public static String createJwtToken(String accountNumber, List<String> authorizations){
        String issuer = "4795";
        String subject = "";
        long ttlMillis = 30*60*1000; //30min
        return createJwtToken(accountNumber, authorizations, issuer, subject, ttlMillis);
    }

    /**
     * 生成Token
     *
     * @param accountNumber
     *            号码
     * @param authorizations
     *            权限列表
     * @param issuer
     *            该JWT的签发者，是否使用是可选的
     * @param subject
     *            该JWT所面向的用户，是否使用是可选的；
     * @param ttlMillis
     *            签发时间
     * @return token String
     */
    public static String createJwtToken(String accountNumber,List<String> authorizations, String issuer, String subject, long ttlMillis) {

        // 签名算法 ，将对token进行签名
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成签发时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 通过秘钥签名JWT
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<String, Object>();
        StringBuffer roles = new StringBuffer();
        for (String role:authorizations){
            roles.append(role+",");
        }
        if (roles.length() !=0){
            roles.delete(roles.length() - 1 ,roles.length());
        }

        claims.put("role", roles.toString());
        claims.put("id", accountNumber);

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(accountNumber)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .setClaims(claims)
                .signWith(signatureAlgorithm, signingKey);

        // if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();

    }

    // Sample method to validate and read the JWT
    public static Claims parseJWT(String jwt) {
        // This line will throw an exception if it is not a signed JWS (as expected)
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
                    .parseClaimsJws(jwt).getBody();
            return claims;
        }catch (Exception exception){
            return null;
        }
    }

    /**
     * 验证jwt的有效期
     * @param claims
     * @return
     */
    public static Boolean isTokenExpired(Claims claims) {
        final Date expiration =  claims.getExpiration();
        return expiration.before(new Date());
    }
}
