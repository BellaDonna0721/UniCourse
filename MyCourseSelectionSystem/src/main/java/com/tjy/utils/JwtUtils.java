package com.tjy.utils;

import com.tjy.pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    /**
     * 过期时间，1天
     */
    private static final long EXPIRE = 60000 * 60 * 24;
    /**
     * 加密密钥
     */
    private static final String SECRET = "net.example.SSMdemo";
    /**
     * 使用HS256算法生成密钥
     */
    private static final SecretKey KEY = Jwts.SIG.HS256.key().random(new SecureRandom(SECRET.getBytes())).build();
    /**
     * 主题
     */
    private static final String SUBJECT = "SSMdemo";

    /**
     * 根据用户信息生成令牌
     *
     * @param user
     * @return
     */
    public static String geneJsonWebToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("username", user.getUsername());
        String token = Jwts.builder().claims()
                .add(claims)  // 添加内容
                .subject(SUBJECT)   // 声明主题
                .issuedAt(new Date())   // 创建JWT时的时间戳
                .expiration(new Date(System.currentTimeMillis() + EXPIRE))  // 设置过期时间
                .and()  // 返回JwtBuilder配置
                .signWith(KEY)  // 签名
                .compact(); // 紧凑
        return token;
    }


    /**
     * 解析JWT令牌
     */
    public static Claims checkJWT(String token) {
        try {
            final Claims claims = Jwts.parser()
                    .verifyWith(KEY)    // 验证所有遇到的JWS签名
                    .build()
                    .parse(token).accept(Jws.CLAIMS)   // 解析jws
                    .getPayload();  // JWT有效载荷
            return claims;
        } catch (Exception e) {
            return null;
        }
    }
}

