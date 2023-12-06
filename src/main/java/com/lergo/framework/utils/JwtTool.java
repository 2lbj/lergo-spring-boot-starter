package com.lergo.framework.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.RegisteredPayload;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtTool {

//    @Value("${jwt.secret}")
//    private String secret;
//
//    @Value("${jwt.expiration}")
//    private Long expiration;
//
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
//                .signWith(SignatureAlgorithm.HS512, secret)
//                .compact();
//    }
//
//    public String getUsernameFromToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    public Claims claimsToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token)
//                .getBody();
//    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * jwt 签名秘钥，可以更换其他不常用的自定义签名器： JWTSignerUtil
     */
    static final JWTSigner jwtSigner = JWTSignerUtil.hs256("lerGo".getBytes());
    /**
     * 自定义二重认证 token key
     */
    static final String AUTHENTICATOR_KEY = "at";

    /**
     * 生成安全的 jwt token
     * 理论上:内部系统交互：每隔3个月，更新 appkey，appsecret
     * 外部系统交互：不用更新 appkey，appsecret，但必须要求其验证负载 ts 和 at（authToken）
     * <p>
     * 时间校验具有多种方法:生效时间、失效时间、签发时间
     * 生效时间（JWTPayload#NOT_BEFORE）不能晚于当前时间
     * 失效时间（JWTPayload#EXPIRES_AT）不能早于当前时间
     * 签发时间（JWTPayload#ISSUED_AT）不能晚于当前时间
     * 一般时间线是：
     *   (签发时间)->->-(生效时间)->->- [当前时间] ->->-(失效时间)
     * <p>
     * 负载签名时间：iat（签发时间-强制）、exp（过期时间校验-强制）、at（内嵌再次token校验），已被占用
     * 可能使用的高级占用参数：详见 hutool 的RegisteredPayload 接口
     *
     * @param appKey    签名key
     * @param appSecret 签名秘钥
     * @param expiresAt 过期时间（秒）
     * @param payload   jwt负载
     * @return jwtToken
     */
    public static String createToken(String appKey,
                                     String appSecret,
                                     int expiresAt,
                                     Map<String, String> payload) {

        Date dateNow = new Date();
        //构建自定义的签名摘要（安全2）
        payload.put(AUTHENTICATOR_KEY,
                SecureUtil.md5(SecureUtil.sha256(
                        appKey + "_" + DateUtil.formatDateTime(dateNow) + "_" + appSecret)));

        /*
            1.创建 jwt 签名，存放参数；
            2.签名时间
            3.设置过期时间为当天结束（validateToken方法：容忍校验2分钟）
            4.hash256签名为（安全4）
         */
        return JWT.create()
                .setIssuedAt(dateNow)
                .setExpiresAt(DateUtil.offsetSecond(dateNow, expiresAt))
                .addPayloads(payload)
                .sign(jwtSigner);
    }

    /**
     * jwt 验证，1）hash256签名，2）失效时间，3）自定义二重安全校验
     *
     * @param token     生成的jwt token
     * @param appKey    签名key
     * @param appSecret 签名秘钥
     * @param leeway    时间容忍度（秒）
     * @return map :当error = "ok"，会额外多出 payload
     */
    public static JwtVerifyResult claimsToken(String token, String appKey, String appSecret, long leeway) {

        try {
            JWT jwt = JWT.of(token);
            jwt.setSigner(jwtSigner);

            //1.验证hash256签名
            if (!jwt.verify()) {
                //hash256签名校验失败
                return new JwtVerifyResult(false, "hash256 validate FAIL");
            }

            //2.验证签名的时间，容忍度为leeway秒
            if (!jwt.validate(leeway)) {
                //时间负载校验失败
                return new JwtVerifyResult(false, "time validate FAIL");
            }

            //3.获取二重 authToken 认证校验
            JWTPayload jwtPayload = jwt.getPayload();
            Date subAtDate = jwtPayload.getClaimsJson().getDate(RegisteredPayload.ISSUED_AT);

            String atSource = String.valueOf(jwtPayload.getClaim(AUTHENTICATOR_KEY));
            String authToken = SecureUtil.md5(SecureUtil.sha256(appKey + "_" + DateUtil.formatDateTime(subAtDate) + "_" + appSecret));

            if (StrUtil.isEmpty(atSource) || !atSource.equals(authToken)) {
                //二重身份认证校验失败
                return new JwtVerifyResult(false, "second auth token FAIL");
            }

            //4.将负载参数转换成map返回
            JSONObject payloadJson = jwtPayload.getClaimsJson();
            payloadJson.remove(AUTHENTICATOR_KEY);
            //签名有效
            JwtVerifyResult jwtVerifyResult = new JwtVerifyResult(true, "VALID token");
            jwtVerifyResult.setPayload(JSONUtil.toBean(payloadJson, new TypeReference<Map<String, String>>() {}, true));

            String roles = jwtVerifyResult.getPayload().get("roles");  // 从JWT负载中获取用户角色信息
            jwtVerifyResult.getPayload().put("roles", roles);

            return jwtVerifyResult;

        } catch (ValidateException e) {
            //无效的签名
            return new JwtVerifyResult(true, "INVALID token");
        }
    }

    @Data
    public static class JwtVerifyResult {
        /**
         * 1 是否成功
         **/
        private Boolean success;
        /**
         * 2 返回消息
         **/
        private String msg;
        /**
         * 3 jwt负载
         **/
        private Map<String, String> payload;

        public JwtVerifyResult(Boolean success, String msg) {
            this.success = success;
            this.msg = msg;
        }
    }

}
