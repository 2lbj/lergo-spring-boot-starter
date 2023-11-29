package com.lergo.framework.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.*;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.stereotype.Component;

import java.util.*;

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


    public static void main(String[] args) {

        Map<String,String> payload = new HashMap<>();
        payload.put("uuid","345345");
        payload.put("ts", DateUtil.current()+"");

        String testAppKey = "testAppkey";
        String testAppSecret = "*************************";
        String token = createToken(payload,testAppKey,testAppSecret, Collections.singletonList("role-admin"));

        System.out.println(token);

        JwtVerifyResult jwtVerifyResult = claimsToken(token,testAppKey,testAppSecret);

        if(jwtVerifyResult.getSuccess()){
            payload = jwtVerifyResult.getPayload();
            System.out.println("负载："+payload.toString());
        }else{
            System.out.println("验证失败："+jwtVerifyResult.msg);
        }

        String rightToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwiYWRtaW4iOnRydWUsIm5hbWUiOiJsb29seSJ9." +
                "U2aQkC2THYV9L0fTN-yBBI7gmo5xhmvMhATtu8v0zEA";

        final JWT jwt = JWTUtil.parseToken(rightToken);

        System.out.println(jwt.getHeader(JWTHeader.TYPE));
        System.out.println(jwt.getPayload("sub"));
    }
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
     * @param payload   jwt负载
     * @param appkey    签名key
     * @param appsecret 签名秘钥
     * @return
     */
    public static String createToken(Map<String, String> payload, String appkey, String appsecret, List<String> roles) {

        Date dateNow = new Date();
        //1.构建自定义的签名摘要（安全2）
        String authToken = SecureUtil.md5(SecureUtil.sha256(appkey + "_" + DateUtil.formatDateTime(dateNow) + "_" + appsecret));
        payload.put(AUTHENTICATOR_KEY, authToken);

        //2.将用户角色信息添加到JWT负载中
        payload.put("roles", String.join(",", roles));  // 将用户角色信息添加到JWT负载中

        //3.创建 jwt 签名，存放参数；2.签名时间 3。设置过期时间为当天结束（validateToken方法：容忍校验2分钟），4.hash256签名为（安全4）
        return JWT.create()
                .addPayloads(payload)
                .setIssuedAt(dateNow)
                .setExpiresAt(DateUtil.offsetSecond(dateNow,10))
                .sign(jwtSigner);
    }

    /**
     * jwt 验证，1）hash256签名，2）失效时间，3）自定义二重安全校验
     *
     * @param token     生成的jwt token
     * @param appkey    签名key
     * @param appsecret 签名秘钥
     * @return map :当error = "ok"，会额外多出 payload
     */
    public static JwtVerifyResult claimsToken(String token, String appkey, String appsecret) {

        try {
            JWT jwt = JWT.of(token);
            jwt.setSigner(jwtSigner);

            //1.验证hash256签名
            if (!jwt.verify()) {
                return new JwtVerifyResult(false, "hash256签名校验失败");
            }

            //2.验证签名的时间，容忍度为2分钟
            if (!jwt.validate(120L)) {
                return new JwtVerifyResult(false, "时间负载校验失败");
            }

            //3.获取二重 authToken 认证校验
            JWTPayload jwtPayload = jwt.getPayload();
            Date subAtDate = jwtPayload.getClaimsJson().getDate(RegisteredPayload.ISSUED_AT);

            String atScource = String.valueOf(jwtPayload.getClaim(AUTHENTICATOR_KEY));
            String authToken = SecureUtil.md5(SecureUtil.sha256(appkey + "_" +DateUtil.formatDateTime(subAtDate) + "_" + appsecret));

            if (StrUtil.isEmpty(atScource) || !atScource.equals(authToken)) {
                return new JwtVerifyResult(false, "二重身份认证校验失败");
            }

            //4.将负载参数转换成map返回
            JSONObject payloadJson = jwtPayload.getClaimsJson();
            payloadJson.remove(AUTHENTICATOR_KEY);
            JwtVerifyResult jwtVerifyResult = new JwtVerifyResult(true, "签名有效");
//            jwtVerifyResult.setPayload(JSONUtil.toBean(payloadJson, new TypeReference<Map<String, String>>() {
//
//            }, true));
            jwtVerifyResult.setPayload(JSONUtil.toBean(payloadJson, new TypeReference<Map<String, String>>() {}, true));

            String roles = jwtVerifyResult.getPayload().get("roles");  // 从JWT负载中获取用户角色信息
            jwtVerifyResult.getPayload().put("roles", roles);

            return jwtVerifyResult;

        } catch (ValidateException e) {
            return new JwtVerifyResult(true, "无效的签名");
        }
    }

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

        public JwtVerifyResult() {
        }

        public JwtVerifyResult(Boolean success, String msg) {
            this.success = success;
            this.msg = msg;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Map<String, String> getPayload() {
            return payload;
        }

        public void setPayload(Map<String, String> payload) {
            this.payload = payload;
        }

        @Override
        public String toString() {
            return "JwtResult{" +
                    "success=" + success +
                    ", msg='" + msg + '\'' +
                    ", payload=" + payload +
                    '}';
        }
    }

}
